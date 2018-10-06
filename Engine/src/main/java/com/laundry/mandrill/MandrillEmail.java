package com.laundry.mandrill;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.pojo.Order;
import com.laundry.pojo.OrderItem;
import com.laundry.util.Util;

public class MandrillEmail {
	
	private static boolean enableEmail = true;
	
	private static final Logger logger = LogManager.getLogger(MandrillEmail.class);
	
	private static final String SUPPORT_PHONE_NUMBER_PLACEHOLDER = "{{supportPhoneNumber}}";
	private static final String SUPPORT_EMAIL_PLACEHOLDER = "{{supportEmail}}";
	private static final String DROP_TIME_SLOT_PLACEHOLDER = "{{dropTimeSlot}}";
	private static final String ORDER_NUMBER_PLACEHOLDER = "{{orderNumber}}";
	private static final String FLAT_NUMBER_PLACEHOLDER = "{{flatNumber}}";
	private static final String SOCIETY_NAME_PLACEHOLDER = "{{societyName}}";
	private static final String PICKUP_TIME_PLACEHOLDER = "{{pickupTime}}";
	private static final String DROP_TIME_PLACEHOLDER = "{{dropTime}}";
	private static final String NUMBER_OF_ITEMS_PLACEHOLDER = "{{numberOfItems}}";
	private static final String BILL_AMOUNT_PLACEHOLDER = "{{billAmount}}";
	private static final String HANGER_NEEDED_PLACEHOLDER = "{{hangerNeeded}}";
	private static final String MODE_OF_PAYMENT_PLACEHOLDER = "{{modeOfPayment}}";
	private static final String DELIVERY_PERSON_NAME_PLACEHOLDER = "{{deliveryPersonName}}";
	private static final String EMAIL_PLACEHOLDER = "{{email}}";
	private static final String PASSWORD_PLACEHOLDER = "{{password}}";
	private static final String WHATSAPP_PLACEHOLDER = "{{whatsapp}}";
	private static final String INTERCOM_PLACEHOLDER = "{{intercom}}";

	private static final String ORDER_ITEM_ROWS_PLACEHOLDER = "{{itemRows}}";
	private static final String DISPLAY_PAYTM_PLACEHOLDER = "{{displayPayTM}}";
	
	private static String key = "MANDRILL_API_KEY";

	public static void sendEmail(String email, String name, String subject, String html, String body, boolean force){
		if((force || enableEmail) && email != null){
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					MultivaluedHashMap<String, String> formData = new MultivaluedHashMap<>();
				    formData.add("from", "BandName <a@l.c>");
				    formData.add("to", name + " <" + email + ">");
				    formData.add("subject", subject);
				    if(html == null){
				    	formData.add("text", body);
				    }
				    else{
				    	formData.add("html", html);
				    }
					System.out.println(formData);
				    Entity<Form> entity = Entity.form(formData);
				    
					Client client = null;
					Response response = null;
					try{
						client = ClientBuilder.newClient();
						response = client.target("https://api.mailgun.net/v3/mg.laundry.in/messages")
								.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
								.header("Authorization", Util.getBasicAuthString("api", key)).post(entity);
						System.out.println(response.readEntity(String.class));
					}
					catch(Exception ex){
						logger.error("Error sending email to : " + email, ex);
					}
					finally{
						if(response != null){
							response.close();
						}
						if(client != null){
							client.close();
						}
					}
				}
			};
			Thread t = new Thread(r);
			t.start();
		}
	}
	
	private static String readTemplate(String templateFileName){
		InputStream is = MandrillEmail.class.getClassLoader().getResourceAsStream("com/laundry/email/" + templateFileName);
		byte[] buffer = new byte[1024];
		String s = "";
		try {
			int bytesRead = is.read(buffer);
			while(bytesRead > 0){
				s += new String(buffer, 0, bytesRead);
				bytesRead = is.read(buffer);
			}
		} catch (IOException e) {
			logger.error("Unable to send confirmed estimate", e);
		}
		finally{
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Error closing stream for " + templateFileName, e);
			}
		}
		return s;
	}
	
	public static void sendConfirmedEstimate(String toEmail, String toName, String supportPhoneNumber, String supportEmail, Order order){
		Date dropTimePlusOne = (Date) order.getDropTime().clone();
		Long numItems = order.getNumberOfItems();
		if(order.getNumberOfItems() == null){
			numItems = 0L;
			for(OrderItem orderItem : order.getOrderItems()){
				numItems += orderItem.getQuantity();
			}
		}
		dropTimePlusOne.setHours(dropTimePlusOne.getHours() + 1);
		
		Date pickupTimePlusOne = (Date) order.getPickupTime().clone();
		pickupTimePlusOne.setHours(pickupTimePlusOne.getHours() + 1);
		
		String template = readTemplate("Confirmed Estimate.html");
		String html = template //.replace(SUPPORT_PHONE_NUMBER_PLACEHOLDER, supportPhoneNumber)
			.replace(SUPPORT_EMAIL_PLACEHOLDER, supportEmail)
			.replace(DROP_TIME_SLOT_PLACEHOLDER, Util.H_MM_A.format(order.getDropTime()) + " and " + Util.H_MM_A.format(dropTimePlusOne))
			.replace(ORDER_NUMBER_PLACEHOLDER, String.valueOf(order.getOrderId()))
			.replace(FLAT_NUMBER_PLACEHOLDER, order.getAddress().getFlatNumber())
			.replace(SOCIETY_NAME_PLACEHOLDER, order.getAddress().getSociety().getName())
			.replace(PICKUP_TIME_PLACEHOLDER, Util.H_MM_A.format(pickupTimePlusOne))
			.replace(DROP_TIME_PLACEHOLDER, Util.H_MM_A.format(dropTimePlusOne))
			.replace(NUMBER_OF_ITEMS_PLACEHOLDER, String.valueOf(numItems))
			.replace(BILL_AMOUNT_PLACEHOLDER, order.getBillAmount().equals(BigDecimal.ZERO) ? "To be calculated" : String.valueOf(order.getBillAmount()))
			.replace(HANGER_NEEDED_PLACEHOLDER, order.isHanger() ? "Yes" : "No")
			.replace(MODE_OF_PAYMENT_PLACEHOLDER, order.getModeOfPayment())
			.replace(DELIVERY_PERSON_NAME_PLACEHOLDER, order.getDropPerson() != null ? order.getDropPerson().getFirstName() : "A delivery person");
		sendEmail(toEmail, toName, "Order Number " + order.getOrderId() + " On website.in", html, "", false);
	}
	
	public static void sendOrderInvoice(String toEmail, String toName, Order order, BigDecimal walletBalance){
		String template = readTemplate("orderInvoice.html");
		StringBuilder itemRowsBuilder = new StringBuilder();
		for(OrderItem orderItem : order.getOrderItems()){
			itemRowsBuilder.append("<tr><td>").append(orderItem.getCategory().getName())
			.append("</td><td align=\"right\" width=\"33%\">").append(orderItem.getQuantity())
			.append("</td><td align=\"right\" width=\"25%\">&#8377;").append(orderItem.getRate().multiply(BigDecimal.valueOf(orderItem.getQuantity())).intValue())
			.append("</td></tr>");
		}
		String html = template.replace(BILL_AMOUNT_PLACEHOLDER, String.valueOf(order.getBillAmount().intValue()))
				.replace(BILL_AMOUNT_PLACEHOLDER, String.valueOf(order.getBillAmount().intValue()))
				.replace(ORDER_NUMBER_PLACEHOLDER, String.valueOf(order.getOrderId()))
				.replace(ORDER_ITEM_ROWS_PLACEHOLDER, itemRowsBuilder.toString())
				.replace(DISPLAY_PAYTM_PLACEHOLDER, "COD".equals(order.getModeOfPayment()) ? "none !important" : "block")
				.replace(MODE_OF_PAYMENT_PLACEHOLDER, "COD".equals(order.getModeOfPayment()) ? order.getModeOfPayment() : "")
				.replace("{{walletBalance}}", walletBalance.toString());
		sendEmail(toEmail, toName, "Order Number " + order.getOrderId() + " On LAUNDRY.IN", html, "", false);
	}
	
	public static void sendForgotPassword(String toEmail, String toName){
		String template = readTemplate("forgotPassword.html");
		sendEmail(toEmail, toName, "Password Recovery for LAUNDRY.IN", template, "", false);
	}
	
	public static void sendOrderConfirmation(String toEmail, String toName, Order order, boolean force){
		Long numItems = order.getNumberOfItems();
		if(order.getNumberOfItems() == null){
			numItems = 0L;
			if(order.getOrderItems() != null){
				for(OrderItem oi : order.getOrderItems()){
					numItems += oi.getQuantity();
				}
			}
		}
		Date pickupTimePlusOne = (Date) order.getPickupTime().clone();
		pickupTimePlusOne.setHours(pickupTimePlusOne.getHours() + 1);
		Date dropTimePlusOne = (Date) order.getDropTime().clone();
		dropTimePlusOne.setHours(dropTimePlusOne.getHours() + 1);
		
		String template = readTemplate("confirmOrder.html");
		String html = template.replace(ORDER_NUMBER_PLACEHOLDER, String.valueOf(order.getOrderId()))
				.replace(ORDER_NUMBER_PLACEHOLDER, String.valueOf(order.getOrderId()))
				.replace(FLAT_NUMBER_PLACEHOLDER, order.getAddress().getFlatNumber())
				.replace(SOCIETY_NAME_PLACEHOLDER, order.getAddress().getSociety().getName())
				.replace(PICKUP_TIME_PLACEHOLDER, Util.H_MM_A.format(pickupTimePlusOne))
				.replace(DROP_TIME_PLACEHOLDER, Util.H_MM_A.format(dropTimePlusOne))
				.replace(NUMBER_OF_ITEMS_PLACEHOLDER, String.valueOf(numItems))
				.replace(BILL_AMOUNT_PLACEHOLDER, order.getBillAmount().equals(BigDecimal.ZERO) ? "To be calculated" : "&#8377; " + String.valueOf(order.getBillAmount()))
				.replace(HANGER_NEEDED_PLACEHOLDER, order.isHanger() ? "Yes" : "No")
				.replace(MODE_OF_PAYMENT_PLACEHOLDER, order.getModeOfPayment())
				.replace(DELIVERY_PERSON_NAME_PLACEHOLDER, order.getPickupPerson() != null ? order.getPickupPerson().getFirstName() : "A delivery boy");
		sendEmail(toEmail, toName, "Order Number " + order.getOrderId() + " On website.in", html, "", force);
	}
	
	public static void sendWelcomeEmail(String toEmail, String toName, String password, String supportPhoneNumber,
			String whatsapp, String intercom){
		String template = readTemplate("welcome.html");
		String html = template
			.replace(EMAIL_PLACEHOLDER, toEmail)
			.replace(PASSWORD_PLACEHOLDER, password)
//			.replace(SUPPORT_PHONE_NUMBER_PLACEHOLDER, supportPhoneNumber)
			.replace(WHATSAPP_PLACEHOLDER, whatsapp)
			.replace(INTERCOM_PLACEHOLDER, intercom);
		sendEmail(toEmail, toName, "Welcome to website.in", html, "", false);
	}
	
	/**
	 * @return the enableEmail
	 */
	public static boolean isEnableEmail() {
		return enableEmail;
	}

	/**
	 * @param enableEmail the enableEmail to set
	 */
	public static void setEnableEmail(boolean enableEmail) {
		MandrillEmail.enableEmail = enableEmail;
	}
	
	
}

