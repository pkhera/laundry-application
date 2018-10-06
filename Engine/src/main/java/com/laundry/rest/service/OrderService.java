package com.laundry.rest.service;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundry.bean.OrderFeedbackBean;
import com.laundry.dao.AdminDAO;
import com.laundry.dao.LaundryDAO;
import com.laundry.dao.OrderDAO;
import com.laundry.dao.PromotionDAO;
import com.laundry.dao.StoreDAO;
import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.DeliveryPerson;
import com.laundry.pojo.Order;
import com.laundry.pojo.OrderItem;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.Status;
import com.laundry.pojo.TimeSlot;
import com.laundry.pojo.User;
import com.laundry.rest.util.SMSUtil;
import com.laundry.util.Util;
 
@Path("/order")
public class OrderService {
	
	private static final Logger logger = LogManager.getLogger(OrderService.class);
	
	private static DateFormat df = new SimpleDateFormat("dd MMM yyyy");
	public static final Integer OPENING_TIME = 8;
	public static final Integer CLOSING_TIME = 22;
	private static final Integer AVAILABLE_SLOTS_PER_HOUR = 10;
	
	private static final String EXP_DELIVERY_UNAVAILABLE_MSG = "Express Delivery is only available from " + OPENING_TIME + " AM to " + (CLOSING_TIME - 13) + " PM.";
	
	public static final java.nio.file.Path scheduleFilePath = Paths.get(Util.getApplicationProperty("schedule.file"));

	static{
		if(!scheduleFilePath.toFile().exists() || scheduleFilePath.toFile().isDirectory()){
			scheduleFilePath.getParent().toFile().mkdirs();
			try {
				boolean fileCreated = scheduleFilePath.toFile().createNewFile();
				logger.info("Schedule File created - " + scheduleFilePath.getFileName() + " - " + fileCreated);
			} catch (IOException e) {
				logger.fatal("�rror creating schedule file.");
			}
		}
		else{
			logger.info("Schedule File exisits - " + scheduleFilePath.getFileName());
		}
	}
	
	/**
	 * 
	 * @api {post} /order Add Order
	 * @apiName Add Order
	 * @apiGroup Order
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {String} pickupTime  Pickup Time.
	 * @apiParam {String} dropTime  Drop Time.
	 * @apiParam {String} express  Express Delivery.
	 * @apiParam {String} folded  Folded.
	 * @apiParam {String} hanger  Hanger.
	 * @apiParam {String} addressId  Address Id.
	 * @apiParam {String} orderItems[]->category->categoryId  Category Id.
	 * @apiParam {String} orderItems[]->quantity  Quantity.
	 * @apiParam {String} orderItems[]->rate  Rate.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 *
	 * @apiExample Sample Request Payload
	 * 	{
     *	  	"pickupTime":"2015-01-01 12:00",
  	 *		"dropTime":"2015-01-01 13:00",
  	 *		"express":true,
  	 *		"hanger":true,
  	 *		"userId":"37",
  	 *		"addressId": "23",
  	 *		"orderItems":[
     *		{
     *		 "category": 
     *		   {
     *		     "categoryId": "1"
     *		   },
     *		 "quantity":"3",
     *		 "rate":"5"
     *		}
     *		]
	 *	}
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 * 		"message": "Order was placed successfully."
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "Invalid order details. Please verify and try again."
	 * }
	 * 
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 401 Unauthorized
	 * {
	 * }
	 *
	 * @param authString
	 * @param order
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Order order){
		logger.info("Request recieved for adding order. Scheduled = " + order.getSchedule());
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			order.setBillAmount(BigDecimal.ZERO);
			List<OrderItem> itemsToRemove = new ArrayList<>();
			Long numItems = 0L;
			for(OrderItem item : order.getOrderItems()){
				if(item.getQuantity() == 0){
					itemsToRemove.add(item);
				}
				else{
					numItems += item.getQuantity();
					order.setBillAmount(order.getBillAmount().add(
							item.getRate().multiply(BigDecimal.valueOf(item.getQuantity()))
					));
				}
			}
			if(!numItems.equals(0L)){
				order.setNumberOfItems(numItems);
			}
			order.getOrderItems().removeAll(itemsToRemove);
			OrderDAO dao = OrderDAO.getInstance();
			StoreDAO storeDAO = StoreDAO.getInstance();
			order.setStoreId(storeDAO.getStoreId(order.getAddressId()));
			ResponseMessage rm = order.validate();
			if(rm == null){
				try{
					Map<String,Boolean> schedule = order.getSchedule();
					boolean repeat = schedule.containsValue(true);
					if(order.isExpress()){
						setExpressOrder(order);
					}
					order = dao.addOrder(order);
					if(order.getPromotionId() != null){
						PromotionDAO promotionDAO = PromotionDAO.getInstance();
						promotionDAO.addUserPromotionUsage(order.getUserId(), order.getPromotionId());
					}
					order = Util.autoAssignDeliveryPerson(order, true);
					responseMessage = new ResponseMessage("Order was placed successfully.");
					status = Response.Status.OK;
					
					if(repeat){
						order.setSchedule(schedule);
						boolean scheduled = schedule(order);
						if(!scheduled){
							responseMessage = new ResponseMessage("Order was placed successfully. However there was a problem scheduling your order. Please try again.");
						}
					}
					String pickupPersonName = "A delivery boy";
					if(order.getPickupPersonId() != null){
						DeliveryPerson pickupPerson = order.getPickupPerson();
						pickupPersonName = pickupPerson.getFirstName();
						String flatNumber = (order.getAddress() != null ? order.getAddress().getFlatNumber() : ""); 
						SMSUtil.sendPickupOrderSMS(pickupPerson.getPhoneNumber(), String.valueOf(order.getOrderId()), 
								Util.H_MM_A.format(order.getPickupTime()), flatNumber, order.getComments(),
								user.getFirstName(), user.getPhoneNumber());
					}
					
					SMSUtil.sendOrderConfirmationSMS(user.getPhoneNumber(), pickupPersonName, Util.H_MM_A.format(order.getPickupTime()), 
							String.valueOf(order.getNumberOfItems()), Util.H_MM_A.format(order.getDropTime()), order.isHanger(), order.isExpress());
					MandrillEmail.sendOrderConfirmation(user.getEmail(), user.getFirstName() + " " + user.getLastName(), order, false);
					MandrillEmail.sendOrderConfirmation("admin@laundry.in", user.getFirstName() + " " + user.getLastName(), order, true);
				}
				catch(Exception ex){
					logger.error("Error while processing order request", ex);
					responseMessage = new ResponseMessage(ex.getMessage());
					if(EXP_DELIVERY_UNAVAILABLE_MSG.equals(ex.getMessage())){
						status = Response.Status.BAD_REQUEST;
					}
					else{
						status = Response.Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				responseMessage = rm;
				status = Response.Status.BAD_REQUEST;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(status == Response.Status.OK ? order : responseMessage).build();
	}
	
	public boolean schedule(Order order){
		JsonFactory jf = new JsonFactory();
		boolean scheduleUpdated = false;
		boolean addSchedule = true;
		MappingIterator<Order[]> orderIterator = null;
		JsonParser jp = null; 
		try {
			jp = jf.createParser(scheduleFilePath.toFile());
			ObjectMapper om = new ObjectMapper();
			orderIterator = om.readValues(jp, Order[].class);
			if(orderIterator != null && orderIterator.hasNext()){
				List<Order> orders = null;
				Order[] orderArray = orderIterator.next();
				if(orderArray != null){
					orders = new ArrayList<>(Arrays.asList(orderArray));
				}
				if(orders != null){
					for(Order o : orders){
						if(o.getUserId().equals(order.getUserId())){
//							addSchedule = false;
//							if(o.getSchedule() != null && !o.getSchedule().equals(order.getSchedule())){
//								o.setSchedule(order.getSchedule());
//								scheduleUpdated = true;
//							}
							orders.remove(o);
							break;
						}
					}
				}
				else{
					orders = new ArrayList<Order>();
				}
				if(addSchedule){
					scheduleUpdated = true;
					orders.add(order);
				}
				if(scheduleUpdated){
					logger.info("scheduling order");
					Writer writer = null;
					try{
						writer = new FileWriter(scheduleFilePath.toFile(), false);
						ObjectMapper om1 = new ObjectMapper();
						om1.writeValue(writer, orders);
					}
					catch(Exception ex){
						logger.error("Error while processing scheduled order request", ex);
						return false;
					}
					finally{
						if(writer != null){
							try {
								writer.close();
							} catch (IOException e) {
								logger.error("Error closing file - " + scheduleFilePath.getFileName(), e);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("Error reading file - " + scheduleFilePath, e);
			return false;
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
		return true;
	}
	
	@PUT
	@Path("/{orderId}/feedback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response feedback(@HeaderParam("authorization") String authString, @PathParam("orderId") Long orderId,
			@QueryParam("feedback") Integer feedback, @QueryParam("feedbackComments") String feedbackComments){
		logger.info("Request recieved for updating order feedback");
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			try{
				OrderDAO dao = OrderDAO.getInstance();
				dao.setOrderFeedback(orderId, feedback, feedbackComments);
				responseMessage = new ResponseMessage("Thankyou for your feedback.");
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error while processing order feedback", ex);
				responseMessage = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, Order order){
		logger.info("Request recieved for updating order");
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			Long exPickupPersonId = order.getPickupPersonId();
			try{
				OrderDAO orderDAO = OrderDAO.getInstance();
				LaundryDAO laundryDAO = LaundryDAO.getInstance();
				orderDAO.rescheduleOrder(order);
				order = Util.autoAssignDeliveryPerson(order, true);
				responseMessage = new ResponseMessage("Your order has been rescheduled successfully.");
				status = Response.Status.OK;
				
				if(order.getPickupPersonId() != null){
					if(exPickupPersonId != null){
						DeliveryPerson exPickupPerson = laundryDAO.getDeliveryPerson(exPickupPersonId);
						if(exPickupPerson != null){
							MandrillEmail.sendEmail(exPickupPerson.getEmail(), exPickupPerson.getFirstName() + " " + exPickupPerson.getLastName(), 
									"Order No. [" + order.getOrderId() + "] has been rescheduled.", "",  order.toString(), true);
						}
					}
				}
				MandrillEmail.sendOrderConfirmation(user.getEmail(), user.getFirstName() + " " + user.getLastName(), order, false);
			}
			catch(Exception ex){
				logger.error("Error while processing update order request", ex);
				responseMessage = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	/**
	 * @api {get} /order/category Get Category Prices
	 * @apiName Get Category Prices
	 * @apiGroup Order
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {String} societyId  Comma-seperated list of society ids. If blank, will return category prices for all societies.
	 * 
	 * @apiSuccess {String} category->categoryId  Category Id.
	 * @apiSuccess {String} category->name  Cateogry name.
	 * @apiSuccess {String} category->pluralName  Plural Category Name.
	 * @apiSuccess {String} category->description  Category Description.
	 * @apiSuccess {String} category->defaultRate  Default Rate.
	 * @apiSuccess {String} societyId  Society Id.
	 * @apiSuccess {String} rate  Rate.
	 * 
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * [
     *	{
     *	   "category": {
     *	       "categoryId": 1,
     *	       "name": "Shirt",
     *	       "pluralName": "Shirts",
     *	       "description": "Shirts",
     *	       "defaultRate": 5
     *	   },
     *	   "societyId": 1,
     *	   "rate": 8
     *	},
     *	{
     *	   "category": {
     *	       "categoryId": 1,
     *	       "name": "Shirt",
     *	       "pluralName": "Shirts",
     *	       "description": "Shirts",
     *	       "defaultRate": 5
     *	   },
     *	   "societyId": 2,
     *	   "rate": 7
     *	}
     * ]
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 401 Unauthorized
	 * {
	 * }
	 * 
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 * 	"message": "Query param societyId should be a comma-seperated string of integers"
	 * }
	 * 
	 * @param authString
	 * @param societyId
	 * @return
	 */
	@GET
	@Path("/category")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCategories(@HeaderParam("authorization") String authString, @QueryParam("societyId") String societyId){
		logger.info("Request recieved for fetching categories");
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			try{
				OrderDAO dao = OrderDAO.getInstance();
				List<Integer> societyIds = Util.toIntegerList(societyId);
				response = dao.getCategoryPrices(societyIds);
				status = Response.Status.OK;
			}
			catch(NumberFormatException nfe){
				logger.error("Query param societyId should be a comma-seperated string of integers", nfe);
				response = new ResponseMessage("Query param societyId should be a comma-seperated string of integers");
				status = Response.Status.BAD_REQUEST;
			}
			catch(Exception ex){
				logger.error("Error fetching categories", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/orderCancellationReasons")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrderCancellationReasons(@HeaderParam("authorization") String authString){
		logger.info("Request recieved for fetching order cancellation reasons");
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			try{
				OrderDAO dao = OrderDAO.getInstance();
				response = dao.getOrderCancellationReasons();
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error fetching cancellation reasons", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
 
	/**
	 * @api {get} /order Get Orders
	 * @apiName Get Orders
	 * @apiGroup Order
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiSuccess {String} orderId  Order Id.
	 * @apiSuccess {String} orderTime  Order Time.
	 * @apiSuccess {String} pickupTime  Pickup Time.
	 * @apiSuccess {String} dropTime  Drop Time.
	 * @apiSuccess {String} userId  User Id.
	 * @apiSuccess {String} addressId  Address Id.
	 * @apiSuccess {String} folded  Folded.
	 * @apiSuccess {String} hanger  Hanger.
	 * @apiSuccess {String} billAmount  Bill Amount.
	 * @apiSuccess {String} express  Express Delivery.
	 * @apiSuccess {String} orderItems[]->orderId  Order Id.
	 * @apiSuccess {String} orderItems[]->category->categoryId  Cateogry Id.
	 * @apiSuccess {String} orderItems[]->category->name  Cateogry name.
	 * @apiSuccess {String} orderItems[]->category->pluralName  Cateogry Plural Name.
	 * @apiSuccess {String} orderItems[]->category->description  Cateogry Description.
	 * @apiSuccess {String} orderItems[]->category->defaultRate  Cateogry Default Rate.
	 * @apiSuccess {String} orderItems[]->quantity  Quantity.
	 * @apiSuccess {String} orderItems[]->rate  Rate.
	 * @apiSuccess {String} orderStatuses[]->orderId  OrderId.
	 * @apiSuccess {String} orderStatuses[]->updatedTime  Updated Time.
	 * @apiSuccess {String} orderStatuses[]->status->statusId  Status Id.
	 * @apiSuccess {String} orderStatuses[]->status->status  Status.
	 * @apiSuccess {String} currentStatus->orderId  OrderId.
	 * @apiSuccess {String} currentStatus->updatedTime  Updated Time.
	 * @apiSuccess {String} currentStatus->status->statusId  Status Id.
	 * @apiSuccess {String} currentStatus->status->status  Status.
	 * 
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * [
     *	{
     *	 	"orderId": 80,
     *	 	"orderTime": "2015-07-11 11:53",
     *  	"pickupTime": "0018-01-05 05:21",
     *   	"dropTime": "0018-01-05 05:30",
     *   	"userId": 37,
     *   	"addressId": 23,
     *   	"folded": false,
     *   	"hanger": true,
     *   	"billAmount": 0,
     *   	"orderItems": [
     *       	{
     *          	"orderId": 80,
     *           	"category": {
     *               	"categoryId": 1,
     *               	"name": "Shirt",
     *               	"pluralName": "Shirts",
     *               	"description": "Shirts",
     *               	"defaultRate": 5
     *           	},
     *           	"quantity": 4,
     *           	"rate": 5
     *           }
     *   	],
     *   	"orderStatuses": [
     *      	{
     *           	"orderId": 80,
     *           	"status": {
     *               	"statusId": 0,
     *               	"status": "Pending"
     *           	},
     *           	"updatedTime": "2015-01-01 06:30 AM"
     *       	},
     *   	],
     *   	"currentStatus": {
     *       	"statusId": 2,
     *       	"status": "Reached Store"
     *   	},
     *   	"express": true
     *	},
     * ]
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 401 Unauthorized
	 * {
	 * }
	 * 
	 * 
	 * @param authString
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrders(@HeaderParam("authorization") String authString){
		logger.info("Request recieved for fetching orders");
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			try{
				OrderDAO dao = OrderDAO.getInstance();
				response = dao.getOrders(user.getUserId());
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error fetching order", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	/**
	 * @api {get} /order Get Orders
	 * @apiName Get Orders
	 * @apiGroup Order
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiSuccess {String} orderId  Order Id.
	 * @apiSuccess {String} orderTime  Order Time.
	 * @apiSuccess {String} pickupTime  Pickup Time.
	 * @apiSuccess {String} dropTime  Drop Time.
	 * @apiSuccess {String} userId  User Id.
	 * @apiSuccess {String} addressId  Address Id.
	 * @apiSuccess {String} folded  Folded.
	 * @apiSuccess {String} hanger  Hanger.
	 * @apiSuccess {String} billAmount  Bill Amount.
	 * @apiSuccess {String} express  Express Delivery.
	 * @apiSuccess {String} orderItems[]->orderId  Order Id.
	 * @apiSuccess {String} orderItems[]->category->categoryId  Cateogry Id.
	 * @apiSuccess {String} orderItems[]->category->name  Cateogry name.
	 * @apiSuccess {String} orderItems[]->category->pluralName  Cateogry Plural Name.
	 * @apiSuccess {String} orderItems[]->category->description  Cateogry Description.
	 * @apiSuccess {String} orderItems[]->category->defaultRate  Cateogry Default Rate.
	 * @apiSuccess {String} orderItems[]->quantity  Quantity.
	 * @apiSuccess {String} orderItems[]->rate  Rate.
	 * @apiSuccess {String} orderStatuses[]->orderId  OrderId.
	 * @apiSuccess {String} orderStatuses[]->updatedTime  Updated Time.
	 * @apiSuccess {String} orderStatuses[]->status->statusId  Status Id.
	 * @apiSuccess {String} orderStatuses[]->status->status  Status.
	 * @apiSuccess {String} currentStatus->orderId  OrderId.
	 * @apiSuccess {String} currentStatus->updatedTime  Updated Time.
	 * @apiSuccess {String} currentStatus->status->statusId  Status Id.
	 * @apiSuccess {String} currentStatus->status->status  Status.
	 * 
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
     *	{
     *	 	"orderId": 80,
     *	 	"orderTime": "2015-07-11 11:53",
     *  	"pickupTime": "0018-01-05 05:21",
     *   	"dropTime": "0018-01-05 05:30",
     *   	"userId": 37,
     *   	"addressId": 23,
     *   	"folded": false,
     *   	"hanger": true,
     *   	"billAmount": 0,
     *   	"orderItems": [
     *       	{
     *          	"orderId": 80,
     *           	"category": {
     *               	"categoryId": 1,
     *               	"name": "Shirt",
     *               	"pluralName": "Shirts",
     *               	"description": "Shirts",
     *               	"defaultRate": 5
     *           	},
     *           	"quantity": 4,
     *           	"rate": 5
     *           }
     *   	],
     *   	"orderStatuses": [
     *      	{
     *           	"orderId": 80,
     *           	"status": {
     *               	"statusId": 0,
     *               	"status": "Pending"
     *           	},
     *           	"updatedTime": "2015-01-01 06:30 AM"
     *       	},
     *   	],
     *   	"currentStatus": {
     *       	"statusId": 2,
     *       	"status": "Reached Store"
     *   	},
     *   	"express": true
     *	}
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 401 Unauthorized
	 * {
	 * }
	 * 
	 * 
	 * @param authString
	 * @return
	 */
	@GET
	@Path("/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrder(@HeaderParam("authorization") String authString, @PathParam("orderId") Long orderId){
		logger.info("Request recieved for fetching order : " + orderId);
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			OrderDAO dao = OrderDAO.getInstance();
			if(dao.doesOrderBelongToUser(orderId, user.getUserId())){
			try{
				response = dao.getOrder(orderId);
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error fetching order", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
			}
			else{
				response = new ResponseMessage("You are unauthorised for this operation");
				status = Response.Status.FORBIDDEN;
			}
		}
		else{
			response = new ResponseMessage("You are unauthorised for this operation");
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/statistics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrderStatistics(@HeaderParam("authorization") String authString, @QueryParam("sinceDays") Long sinceDays){
		logger.info("Request recieved for fetching order statistics");
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			OrderDAO dao = OrderDAO.getInstance();
			try{
				response = dao.getOrderStatistics(user.getUserId(), sinceDays);
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error fetching order statistics", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			response = new ResponseMessage("You are unauthorised for this operation");
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Path("feedback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response feedback(@HeaderParam("authorization") String authString, Map<String,Object> input){
		logger.info("Request recieved for updating order feedback");
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			OrderDAO dao = OrderDAO.getInstance();
			try{
				Long orderId = Long.parseLong(input.get("orderId").toString());
				
				String feedbackComments = input.get("feedbackComments").toString();
				boolean allowed = dao.doesOrderBelongToUser(orderId, user.getUserId());
				if(!allowed){
					responseMessage = new ResponseMessage("You are not authorized for this operation.");
					status = Response.Status.FORBIDDEN;
				}
				else{
					List<Map<String, Object>> orderFeedback = (List<Map<String, Object>>) input.get("orderFeedback");
					List<OrderFeedbackBean> ofbs = new ArrayList<>(orderFeedback.size());
					for(Map<String,Object> of : orderFeedback){
						OrderFeedbackBean ofb = new OrderFeedbackBean();
						ofb.setOrderId(orderId);
						ofb.setRating(new BigDecimal(of.get("rating").toString()));
						ofb.setFeedbackCriteriaId(Long.parseLong(((Map<String,Object>)of.get("feedbackCriteria")).get("id").toString()));
						ofbs.add(ofb);
					}
					boolean feedbackSaved = dao.addOrderFeedback(orderId, ofbs, feedbackComments);
					if(feedbackSaved){
						responseMessage = new ResponseMessage("Order Feedback was submitted successfully.");
						status = Response.Status.OK;
					}
					else{
						responseMessage = new ResponseMessage("Error while submitting feedback.");
						status = Response.Status.BAD_REQUEST;
					}
				}
			}
			catch(Exception ex){
				logger.error("Error processing order feedback", ex);
				responseMessage = new ResponseMessage("Error while submitting feedback.");
				status = Response.Status.BAD_REQUEST;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelOrder(@HeaderParam("authorization") String authString, @QueryParam("orderId") Long orderId,
			@QueryParam("orderCancellationReason") Long orderCancellationReason){
		logger.info("Request recieved for order cancellation");
		Object response = null;
		Response.Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			if(orderId == null){
				response = new ResponseMessage("orderId is required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				OrderDAO dao = OrderDAO.getInstance();
				boolean permitted = dao.doesOrderBelongToUser(orderId, user.getUserId());
				if(permitted){
					List<Order> orders = AdminDAO.getInstance().setOrderStatus(Arrays.asList(orderId), Status.CANCELLED, orderCancellationReason);
					for(Order order : orders){
						response = new ResponseMessage("Order cancelled successfully.");
						status = Response.Status.OK;
						DeliveryPerson deliveryPerson = order.getPickupPerson();
						if(deliveryPerson != null){
							MandrillEmail.sendEmail(deliveryPerson.getEmail(), deliveryPerson.getFirstName() + " " + deliveryPerson.getLastName(), 
									"Order No. [" + order.getOrderId() + "] has been cancelled.", "", order.toString(), true);
						}
					}
				}
				else{
					response = new ResponseMessage("You are not authorized to perform this action.");
					status = Response.Status.FORBIDDEN;
				}
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/timeslots")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeSlots(@HeaderParam("authorization") String authString, @QueryParam("societyId") String societyId,
			@QueryParam("date") String dateString){
		logger.info("Request recieved for fetching order time slots");
		Response.Status status = null;
		User user = Util.getUser(authString);
		List<TimeSlot> timeSlots = null;
		if(user != null){
			try{
				timeSlots = getTimeSlots(dateString, societyId);
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error processing time slots", ex);
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(timeSlots).build();
	}
	
	public static List<TimeSlot> getTimeSlots(String dateString, String societyId) throws ParseException{
		List<TimeSlot> timeSlots = new ArrayList<>();
		Date date = df.parse(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Calendar now = Calendar.getInstance();
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.set(Calendar.AM_PM, Calendar.AM);
		
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);
		tomorrow.set(Calendar.HOUR, 0);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 0);
		tomorrow.set(Calendar.MILLISECOND, 0);
		tomorrow.set(Calendar.AM_PM, Calendar.AM);
		
		int fromHour = OPENING_TIME;
		if(date.equals(today.getTime()) || (date.after(today.getTime()) && date.before(tomorrow.getTime()))){
			int currentHour = now.get(Calendar.HOUR_OF_DAY);
			int currentMinutes = now.get(Calendar.MINUTE);
			if(currentMinutes > 29){
				currentHour ++;
			}
			if(currentHour < OPENING_TIME){
				fromHour = OPENING_TIME;
			}
			else{
				fromHour = currentHour;
			}
			
		}
		OrderDAO dao = OrderDAO.getInstance();
		Map<Integer,Integer> pickupAndDropTimes = dao.getPickupAndDropTimes(societyId, date);
		String amPM = "AM";
		String amPMPlus1 = "AM";
		
		
		for(int i=fromHour; i<CLOSING_TIME;i++){
			int hour = i;
			int hourPlus1 = i+1;
			
			if(i == 11){
				amPMPlus1 = "PM";
			}
			else if(i == 12){
				hourPlus1 = 1;
				amPM = "PM";
				amPMPlus1 = "PM";
			}
			else if (i > 12){
				hour = i%12;
				hourPlus1 = hour + 1;
				amPM = "PM";
				amPMPlus1 = "PM";
			}
			String hourString = "0";
			if(i>8){
				hourString = "";
			}
			String time = hourString + hour + ":00 " + amPM;
			String timeSlot = hour + " " + amPM + " to " + hourPlus1 + " " + amPMPlus1;
			Integer availableTimeSlots = AVAILABLE_SLOTS_PER_HOUR;
			if(pickupAndDropTimes.containsKey(i)){
				availableTimeSlots -= pickupAndDropTimes.get(i);
				if(availableTimeSlots < 0){
					availableTimeSlots = 0;
				}
			}
			timeSlots.add(new TimeSlot(time, timeSlot, availableTimeSlots));
		}
		return timeSlots;
	}
	
	private void setExpressOrder(Order order) throws Exception{
		Calendar now = Calendar.getInstance();
		int currentHour = now.get(Calendar.HOUR_OF_DAY);
		int currentMinutes = now.get(Calendar.MINUTE);
		
		if(currentHour > OPENING_TIME && currentHour < CLOSING_TIME - 1){
			Date pickupTime = now.getTime();
			pickupTime.setHours(currentMinutes >= 30 ? currentHour + 1 : currentHour);
			pickupTime.setMinutes(0);
			
			Date dropTime = now.getTime();
			dropTime.setHours(currentMinutes >= 30 ? currentHour + 2 : currentHour + 1);
			dropTime.setMinutes(0);
			
			order.setPickupTime(pickupTime);
			order.setDropTime(dropTime);
		}
		else{
			throw new Exception(EXP_DELIVERY_UNAVAILABLE_MSG);
		}
	}
}