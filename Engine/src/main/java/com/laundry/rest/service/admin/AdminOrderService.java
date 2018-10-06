package com.laundry.rest.service.admin;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

import com.laundry.bean.UserDeviceTokenBeanKey;
import com.laundry.dao.AdminDAO;
import com.laundry.dao.LaundryDAO;
import com.laundry.dao.OrderDAO;
import com.laundry.dao.PromotionDAO;
import com.laundry.dao.StoreBagDAO;
import com.laundry.dao.StoreDAO;
import com.laundry.dao.UserDAO;
import com.laundry.google.GCM;
import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.Address;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.DeliveryPerson;
import com.laundry.pojo.Order;
import com.laundry.pojo.OrderItem;
import com.laundry.pojo.PaymentTransaction;
import com.laundry.pojo.Promotion;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.Status;
import com.laundry.pojo.TimeSlot;
import com.laundry.pojo.User;
import com.laundry.rest.service.OrderService;
import com.laundry.rest.util.SMSUtil;
import com.laundry.util.Util;
 
@Path("/admin/order")
public class AdminOrderService {
	
	private static final Logger logger = LogManager.getLogger(AdminOrderService.class);
	
	private static final String SUPPORT_PHONE_NUMBER = "+91 987 654 3210";
	private static final String SUPPORT_EMAIL = "support@laundry.in";
	private static final String WHATSAPP = "+91 987 654 3210";
	
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
	 * @param changePassword
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Order order){
		logger.info("Request recieved from admin for adding order");
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			order.setBillAmount(BigDecimal.ZERO);
			Long numItems = 0L;
			List<OrderItem> itemsToRemove = new ArrayList<>();
			if(order.getOrderItems() != null){
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
				order.getOrderItems().removeAll(itemsToRemove);
			}
			if(!numItems.equals(0L)){
				order.setNumberOfItems(numItems);
			}
			
			UserDAO userDAO = UserDAO.getInstance();
			logger.debug("user - " + order.getUser().getFirstName());
			if(order.getUser().getUserId() == null || order.getUser().getUserId() == 0){
				User user = order.getUser();
				Address defaultAddress = user.getAddresses().get("Home");
				user.setEmail(defaultAddress.getFlatNumber() + "@laundry.in" );
				user.setDefaultAddress(defaultAddress);
				boolean userAdded = userDAO.addUser(user);
				order.setAddressId(defaultAddress.getAddressId());
				order.setAddress(defaultAddress);
			}
			order.setModeOfPayment("COD"); 
			order.setDropTime(getDropTime(order.getPickupTime(), order.isExpress()));
			
			if(order.getUserId() == null && order.getUser() != null){
				order.setUserId(order.getUser().getUserId());
			}
			if(order.getAddressId() == null && order.getAddress() != null){
				order.setAddressId(order.getAddress().getAddressId());
			}
			if(order.getStoreId() == null){
				StoreDAO storeDAO = StoreDAO.getInstance();
				order.setStoreId(storeDAO.getStoreId(order.getAddressId()));
			}
			
			String pickupPersonName = "A delivery boy";
			try{
				OrderDAO orderDAO = OrderDAO.getInstance();
				orderDAO.addOrder(order);
				order = Util.autoAssignDeliveryPerson(order, true);
				responseMessage = new ResponseMessage("Order was placed successfully.");
				status = Response.Status.OK;
				String flatNumber = (order.getAddress() != null ? order.getAddress().getFlatNumber() : "");
				User user = order.getUser();
				if(order.getPickupPersonId() != null){
					DeliveryPerson pickupPerson = order.getPickupPerson();
					SMSUtil.sendPickupOrderSMS(pickupPerson.getPhoneNumber(), String.valueOf(order.getOrderId()), 
							Util.H_MM_A.format(order.getPickupTime()), flatNumber, order.getComments(),
							user.getFirstName(), user.getPhoneNumber());
//						MandrillEmail.sendEmail(pickupPerson.getEmail(), pickupPerson.getFirstName() + " " + pickupPerson.getLastName(), 
//								"Order No. [" + order.getOrderId() + "] has been assigned to you for pickup.", order.toString());
					
				}
				SMSUtil.sendOrderConfirmationSMS(user.getPhoneNumber(), pickupPersonName, Util.H_MM_A.format(order.getPickupTime()), 
						String.valueOf(order.getNumberOfItems()), "", order.isHanger(), order.isExpress());
//					SMSUtil.sendOrderConfirmationSMS(user.getPhoneNumber(), user.getFirstName(), String.valueOf(order.getOrderId())
//							, pickupPersonName, String.valueOf(order.getPickupTime()));
				MandrillEmail.sendOrderConfirmation(user.getEmail(), user.getFirstName() + " " + user.getLastName(), order, false);
				MandrillEmail.sendOrderConfirmation("admim@laundry.in", user.getFirstName() + " " + user.getLastName(), order, true);
			}
			catch(Exception ex){
				logger.error("Error while processing order request", ex);
				responseMessage = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(status == Response.Status.OK ? order : responseMessage).build();
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
		logger.info("Admin request recieved for fetching categories");
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			try{
				List<Integer> societyIds = Util.toIntegerList(societyId);
				OrderDAO dao = OrderDAO.getInstance();
				response = dao.getCategoryPrices(societyIds);
				status = Response.Status.OK;
			}
			catch(NumberFormatException nfe){
				logger.error("Query param societyId should be a comma-seperated string of integers", nfe);
				response = new ResponseMessage("Query param societyId should be a comma-seperated string of integers");
				status = Response.Status.BAD_REQUEST;
			}
			catch(Exception ex){
				logger.error("Error processing order categories", ex);
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
	public Response getOrders(@HeaderParam("authorization") String authString, @QueryParam("statuses") String statuses, @QueryParam("storeId") Long storeId){
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			try{
				response = AdminDAO.getInstance().getOrders(Util.toIntegerList(statuses), storeId);
				status = Response.Status.OK;
			}
			catch(Exception ex){
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Path("status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStatus(@HeaderParam("authorization") String authString, @QueryParam("orderId") String orderId, 
			@QueryParam("statusId") Integer statusId, Map<String, String> input){
		Map<String, Object> response = new HashMap<>();
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		List<String> orderIdStrings = Arrays.asList(orderId.split(","));
		List<Long> orderIds = new ArrayList<>();
		for(String oid : orderIdStrings){
			Long id = Long.parseLong(oid);
			if(id!=null){
				orderIds.add(id);
			}
		}
		if(adminUser != null){
			if(orderIds == null || statusId == null){
				response.put("message","orderId and statusId are required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				AdminDAO adminDao = AdminDAO.getInstance();
				List<Order> orders = adminDao.setOrderStatus(orderIds, statusId);
				for(Order order : orders){
					response.put("order", order);
					response.put("message","Order updated successfully.");
					status = Response.Status.OK;
					
					User user = order.getUser();
					String message = null;
					UserDAO userDAO = UserDAO.getInstance();
					switch(statusId){
						case Status.CANCELLED :
						{
							message = "Your order [" + order.getOrderId() + "] has been cancelled.";
//							MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), 
//									message , "", order.toString());
							break;
						}
						case Status.REJECTED :
						{
							message = "Your order [" + order.getOrderId() + "] has been rejected.";
//							MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), 
//									message, "", order.toString());
							break;
						}
						case Status.PICKUP_ARRIVING:
						{
							if(order.getBagId() != null && order.getStoreId() != null){
								StoreBagDAO.getInstance().setBagAvailability(order.getBagId(), order.getStoreId(), false);
							}
							break;
						}
						case Status.CLOTHES_REEICVED:
						{
							if(Status.PICKUP_ARRIVING != order.getCurrentStatus().getStatusId()){
								adminDao.setOrderStatus(Arrays.asList(order.getOrderId()), Status.PICKUP_ARRIVING);
							}
							order = Util.autoAssignIronPerson(order);
							message = "Your clothes for order [" + order.getOrderId() + "] have been received.";
							if(order.getIronPerson() != null){
								message = "Your clothes for order [" + order.getOrderId() + "] have been received and are being ironed.";
							}
							SMSUtil.sendOrderRecievedSMS(user.getPhoneNumber(), user.getFirstName(), String.valueOf(order.getOrderId()), String.valueOf(order.getBillAmount()));
							MandrillEmail.sendConfirmedEstimate(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
									SUPPORT_PHONE_NUMBER, SUPPORT_EMAIL, order);
							break;
						}
						case Status.IRONING:
						{
							message = "Your clothes for order [" + order.getOrderId() + "] are being ironed.";
//							MandrillEmail.sendConfirmedEstimate(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
//									SUPPORT_PHONE_NUMBER, SUPPORT_EMAIL, order);
							break;
						}
						case Status.PACKED:
						{
							order = Util.autoAssignDeliveryPerson(order, false);
//							MandrillEmail.sendConfirmedEstimate(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
//									SUPPORT_PHONE_NUMBER, SUPPORT_EMAIL, order);
							break;
						}
						case Status.DISPATCHED:
						{
							message = "Your order [" + order.getOrderId() + "] has been dispatched.";
							break;
						}
						case Status.DELIVERED:
						{
							if(order.getBagId() != null && order.getStoreId() != null){
								StoreBagDAO.getInstance().setBagAvailability(order.getBagId(), order.getStoreId(), true);
							}
							message = "Your order [" + order.getOrderId() + "] has been delivered.";
							
							LaundryDAO laundryDAO = LaundryDAO.getInstance();
							BigDecimal cashAmount = BigDecimal.ZERO;
							if(input != null && input.containsKey("cashPaid")){
								Long userId = Long.valueOf(input.get("userId"));
								cashAmount = new BigDecimal(input.get("cashPaid"));
								if(cashAmount.compareTo(BigDecimal.ZERO) > 0){
									PaymentTransaction paymentTransaction = new PaymentTransaction();
									paymentTransaction.setGatewayName("CASH");
									paymentTransaction.setStatus(PaymentTransaction.TXN_SUCCESS);
									paymentTransaction.setTransactionAmount(cashAmount);
									paymentTransaction.setTransactionDateTime(new Date());
									paymentTransaction.setUserId(userId);
									
									Long transactionId = laundryDAO.addPaymentTransaction(paymentTransaction);
									if(transactionId != null){
										laundryDAO.addTransaction(transactionId, null, userId, cashAmount);
										response.put("transactionId", transactionId);
										status = Response.Status.OK;
									}
									else{
										response.put("message", "Transaction failed. Unable to add cash payment transaction");
										status = Response.Status.INTERNAL_SERVER_ERROR;
									}
								}
							}
							OrderDAO orderDAO = OrderDAO.getInstance();
							orderDAO.addOrderTransaction(order.getOrderId(), user.getUserId(), order.getBillAmount());
							BigDecimal discount = null;
							if(PaymentTransaction.PAYMENT_MODE_PAYTM.equals(order.getModeOfPayment()) && order.getPromotionId() != null){
								PromotionDAO promotionDAO = PromotionDAO.getInstance();
								Promotion promotion = promotionDAO.getPromotion(order.getPromotionId(), order.getUserId());
								if(promotion.getPercentageCredit() > 0){
									discount = order.getBillAmount().multiply(BigDecimal.valueOf(promotion.getPercentageCredit())).divide(BigDecimal.valueOf(100));
									if(discount.compareTo(promotion.getMaxCredit()) > 0){
										discount = promotion.getMaxCredit();
									}
									discount.setScale(2, RoundingMode.HALF_EVEN);
								}
								else if(promotion.getFixedCredit().compareTo(BigDecimal.ZERO) > 0){
									discount = promotion.getFixedCredit();
								}
								if(discount != null){
									laundryDAO.addTransaction(null, order.getOrderId(), order.getUserId(), discount);
								}
							}
							BigDecimal amountDeducted = order.getBillAmount().subtract(cashAmount);
							if(discount != null){
								amountDeducted = amountDeducted.subtract(discount);
							}
							amountDeducted.setScale(0);
							
							
							BigDecimal walletBalance = userDAO.getWalletBalance(user.getUserId());
							SMSUtil.sendOrderCompletionSMS(user.getPhoneNumber(), "+91 987 654 3210", amountDeducted.compareTo(BigDecimal.ZERO) == 0 ? null : amountDeducted.toString(), walletBalance.toString());
							MandrillEmail.sendOrderInvoice(user.getEmail(), user.getFirstName() + " " + user.getLastName(), order, walletBalance);
							MandrillEmail.sendOrderInvoice("admin@laundry.in", user.getFirstName() + " " + user.getLastName(), order, walletBalance);
							
							break;
						}
					}
					if(message != null){
						List<UserDeviceTokenBeanKey> deviceTokens = AdminDAO.getInstance().getUserTokens(user.getUserId());
						for(UserDeviceTokenBeanKey udtbk : deviceTokens){
							if(udtbk.getDeviceType() == 1){
								String token = GCM.sendMessage(udtbk.getDeviceToken(), "STATUS_" + order.getOrderId(), message, GCM.ACTION_STATUS_CHANGE,
										GCM.URL_STATUS_CHANGE, order.getOrderId());
								userDAO.deleteDeviceToken(udtbk);
								if(token != null && !token.equals(udtbk.getDeviceToken())){
									userDAO.addUserDeviceToken(udtbk.getUserId(), udtbk.getDeviceType(), token);
								}
							}
						}
					}
				}
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("orderId") List<Long> orderIds){
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(orderIds == null){
				response = new ResponseMessage("orderId is required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				List<Order> orders = AdminDAO.getInstance().setOrderStatus(orderIds, Status.DELETED);
				for(Order order : orders){
					response = new ResponseMessage("Order deleted successfully.");
					status = Response.Status.OK;
					
					if(order.getPickupPerson() != null){
						DeliveryPerson deliveryPerson = order.getPickupPerson();
						User user = order.getUser();
						MandrillEmail.sendEmail(deliveryPerson.getEmail(), deliveryPerson.getFirstName() + " " + deliveryPerson.getLastName(), "", 
								"Order No. [" + order.getOrderId() + "] has been deleted.", "Order details : \nOrder Id - " 
								+ order.getOrderId() + "\nItems - " + order.getOrderItems()
								+ "\nPickup Time - " + order.getPickupTime()
								+ "\nDrop Time - " + order.getDropTime()
								+ "\nTotal Amount - Rs. " + order.getBillAmount()
								+ "\n\nUser Details: "
								+ "\nName - " + user.getFirstName() + " " + user.getLastName()
								+ "\nPickup Address - " + order.getAddress()
								+ "\nPhone Number - " + user.getPhoneNumber(), false);
					}
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
		Object response = null;
		Response.Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		List<TimeSlot> timeSlots = new ArrayList<>();
		if(user != null){
			try{
				timeSlots = OrderService.getTimeSlots(dateString, societyId);
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error processing time slots", ex);
				response = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(timeSlots).build();
	}

	@PUT
	@Path("/items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateItems(@HeaderParam("authorization") String authString, @QueryParam("orderId") Long orderId, List<OrderItem> orderItems){
		logger.info("Request recieved for updating order");
		ResponseMessage responseMessage = null;
		Response.Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			try{
				OrderDAO dao = OrderDAO.getInstance();
				dao.updateOrderItems(orderId, orderItems);
				responseMessage = new ResponseMessage("Items for your order have been updated successfully.");
				status = Response.Status.OK;
			}
			catch(Exception ex){
				logger.error("Error while updating order request", ex);
				responseMessage = new ResponseMessage(ex.getMessage());
				status = Response.Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}

	@GET
	@Path("/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrder(@HeaderParam("authorization") String authString, @PathParam("orderId") Long orderId){
		logger.info("Request recieved for fetching order : " + orderId);
		Object response = null;
		Response.Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			OrderDAO dao = OrderDAO.getInstance();
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
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	public static Date getDropTime(Date pickupTime, boolean isExpress){
		Calendar dropTimeCalendar = Calendar.getInstance();
		dropTimeCalendar.setTime(pickupTime);
		int hour = dropTimeCalendar.get(Calendar.HOUR_OF_DAY);
		if(isExpress){
			if(hour < OrderService.CLOSING_TIME - 1){
				dropTimeCalendar.set(Calendar.HOUR_OF_DAY, hour + 1);
			}
			else{
				dropTimeCalendar.set(Calendar.HOUR_OF_DAY, OrderService.OPENING_TIME + 1);
				dropTimeCalendar.add(Calendar.DATE, 1);
			}
		}
		else{
			int workingHours = OrderService.CLOSING_TIME - OrderService.OPENING_TIME;
			int daysToAdd = 30 / workingHours;
			int hoursToAdd = 30 % workingHours;
			if(hour + hoursToAdd > OrderService.CLOSING_TIME){
				daysToAdd++;
				hoursToAdd = OrderService.CLOSING_TIME - hour;
			}
			dropTimeCalendar.add(Calendar.DATE, daysToAdd);
			dropTimeCalendar.add(Calendar.HOUR_OF_DAY, hoursToAdd);
		}
		return dropTimeCalendar.getTime();
	}
	
}