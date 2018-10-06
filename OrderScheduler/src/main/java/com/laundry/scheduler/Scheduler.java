package com.laundry.scheduler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundry.pojo.Order;
import com.laundry.pojo.PaymentTransaction;
import com.laundry.util.Util;

public class Scheduler {
	
	private static final Logger logger = LogManager.getLogger(Scheduler.class);
	private static final String ENGINE_URL = Util.getApplicationProperty("engine.url");
	private static final String USERNAME = Util.getApplicationProperty("adminuser.username");
	private static final String PASSWORD = Util.getApplicationProperty("adminuser.password");
	private static final String AUTH_STRING = "Basic " + Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());
	
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		logger.info("Application URL - " + ENGINE_URL);
		logger.info("Auth String - " + AUTH_STRING);
	}
	
	public static void main(String[] args) {
		String scheduleFilePath = Util.getApplicationProperty("schedule.file");
		logger.info("Reading scheduled orders from " + scheduleFilePath);
		Path p = Paths.get(scheduleFilePath);
		JsonFactory jf = new JsonFactory();
		JsonParser jp = null;
		MappingIterator<Order[]> orderIterator = null;
		try {
			jp = jf.createParser(p.toFile());
			ObjectMapper om = new ObjectMapper();
			orderIterator = om.readValues(jp, Order[].class);
			Calendar today = Calendar.getInstance();
			List<Long> failedOrders = new ArrayList<>();
			int orderCount = 0;
			if(orderIterator != null && orderIterator.hasNext()){
				Order[] orders = orderIterator.next();
				for(Order order : orders){
					int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
					Boolean createOrder = false;
					if(order.getSchedule() != null){
						switch(dayOfWeek){
							case Calendar.MONDAY:createOrder = order.getSchedule().get("Monday");break;
							case Calendar.TUESDAY:createOrder = order.getSchedule().get("Tuesday");break;
							case Calendar.WEDNESDAY:createOrder = order.getSchedule().get("Wednesday");break;
							case Calendar.THURSDAY:createOrder = order.getSchedule().get("Thursday");break;
							case Calendar.FRIDAY:createOrder = order.getSchedule().get("Friday");break;
							case Calendar.SATURDAY:createOrder = order.getSchedule().get("Saturday");break;
							case Calendar.SUNDAY:createOrder = order.getSchedule().get("Sunday");break;
						}
					}
					if(createOrder != null && createOrder){
						orderCount++;
						if(order.getModeOfPayment() == null){
							order.setModeOfPayment(PaymentTransaction.PAYMENT_MODE_COD);
						}
						Calendar pickupTime = Calendar.getInstance();
						pickupTime.setTime(order.getPickupTime());
						pickupTime.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
						order.setPickupTime(pickupTime.getTime());
						
						Calendar dropTime = Calendar.getInstance();
						long diff = dropTime.getTime().getTime() - pickupTime.getTime().getTime();
						long addDay = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
						if(addDay < 0){
							addDay = 0;
						}
						dropTime.setTime(order.getDropTime());
						dropTime.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE) + (int)addDay);
						order.setDropTime(dropTime.getTime());
						
						boolean orderPlaced = false;
						int retries = 0;
						boolean retry = false;
						do{
							retry = false;
							try{
								orderPlaced = placeOrder(order);
							}catch(Exception e){
								if(retries++ < 3){
									retry = true;
								}
							}
						}
						while(retry);
						if(!orderPlaced){
							logger.error("Failed to place order - " + order.toString());
							failedOrders.add(order.getOrderId());
						}
					}
				}
			}
			logger.info("Total orders - " + orderCount);
			logger.info("Successful orders - " + (orderCount - failedOrders.size()));
			logger.info("Failed orders - " + failedOrders.size() + " - " + failedOrders);
		} catch (IOException e) {
			logger.error("Error reading file - " + scheduleFilePath, e);
		}
		finally{
			try {
				if(orderIterator != null){
					orderIterator.close();
				}
				if(jp != null){
					jp.close();
				}
			} catch (IOException e) {
				logger.error("Error closing orderIterator.", e);
			}
		}
	}

	private static boolean placeOrder(Order order) {
		Entity<Order> entity = Entity.json(order);
		boolean orderPlaced = false;
		Client client = null;
		Response response = null;
		try{
			client = ClientBuilder.newClient();
			Builder builder = client.target(ENGINE_URL + "/order").request();
			builder.header("Authorization", AUTH_STRING);
			
			logger.info("Placing order - " + order.toString());
			response = builder.method("POST", entity);
			String message = response.readEntity(String.class);
			if(response.getStatus() == Status.OK.getStatusCode()){
				logger.info("Order placed successfully.");
				orderPlaced = true;
			}
			else{
				logger.warn("Order Response - " + message);
			}
		}
		finally{
			if(response != null){
				response.close();
			}
			if(client != null){
				client.close();
			}
		}
		return orderPlaced;
	}
}
