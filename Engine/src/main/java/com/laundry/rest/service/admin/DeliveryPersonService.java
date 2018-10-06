package com.laundry.rest.service.admin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.bean.UserDeviceTokenBeanKey;
import com.laundry.dao.AdminDAO;
import com.laundry.dao.UserDAO;
import com.laundry.google.GCM;
import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.DeliveryPerson;
import com.laundry.pojo.Order;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.util.Util;
 
@Path("/admin/deliveryPerson")
public class DeliveryPersonService {
	
	private static final Logger logger = LogManager.getLogger(DeliveryPersonService.class);
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, DeliveryPerson deliveryPerson) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(deliveryPerson.isValid()){
				boolean deliveryPersonExists = AdminDAO.getInstance().deliveryPersonExists(deliveryPerson.getEmail());
				if(deliveryPersonExists){
					response.put("message", "Delivery person with email " + deliveryPerson.getEmail() + " already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					boolean deliveryPersonAdded = AdminDAO.getInstance().addDeliveryPerson(deliveryPerson);
					if(deliveryPersonAdded){
						response.put("message", "Delivery person added successfully.");
						status = Status.OK;
						response.put("deliveryPerson", deliveryPerson);
					}
					else{
						response.put("message", "Error adding delivery person.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				response.put("message", "Invalid delivery person details. Please verify.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, DeliveryPerson deliveryPerson) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(deliveryPerson.isValid()){
				boolean deliveryPersonAdded = AdminDAO.getInstance().updateDeliveryPerson(deliveryPerson);
				if(deliveryPersonAdded){
					response.put("message", "Delivery person updated successfully.");
					status = Status.OK;
					response.put("deliveryPerson", deliveryPerson);
				}
				else{
					response.put("message", "Error updating delivery person.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", "Invalid delivery person details. Please verify.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, @QueryParam("deliveryPersonId") Long deliveryPersonId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean deliveryPersonDeleted = AdminDAO.getInstance().deleteDeliveryPerson(deliveryPersonId);
			if(deliveryPersonDeleted){
				response.put("message", "Delivery person deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting delivery person.");
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("authorization") String authString, @QueryParam("forOrder") Long orderId, @QueryParam("storeId") Long storeId) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			if(orderId != null){
				response = AdminDAO.getInstance().getDeliveryPersonsForOrder(orderId, storeId);
			}
			else{
				response = AdminDAO.getInstance().getDeliveryPersons(storeId);
			}
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("User is unauthorized");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assign(@HeaderParam("authorization") String authString, @QueryParam("orderId") Long orderId, 
			@QueryParam("pickupPersonId") Long pickupPersonId, @QueryParam("dropPersonId") Long dropPersonId){
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(orderId == null || (pickupPersonId == null && dropPersonId == null)){
				response = new ResponseMessage("orderId and pickupPersonId or dropPersonId is required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				Order order = AdminDAO.getInstance().setOrderDeliveryPerson(orderId, pickupPersonId, dropPersonId);
				if(order != null){
					response = new ResponseMessage("Order updated successfully.");
					status = Response.Status.OK;
					
					User user = order.getUser();
					DeliveryPerson pickupPerson = order.getPickupPerson();
					DeliveryPerson dropPerson = order.getDropPerson();
					
					String message = null;
					if(pickupPersonId != null){
						MandrillEmail.sendEmail(pickupPerson.getEmail(), pickupPerson.getFirstName() + " " + pickupPerson.getLastName(), 
								"Order No. [" + order.getOrderId() + "] has been assigned to you for pickup.", "", order.toString(), true);
						
						message = "Your order [" + order.getOrderId() + "] will be picked up by " + pickupPerson.getFirstName() + " " + pickupPerson.getLastName();
//						MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
//								message, "", order.toString());
					}
					if(dropPersonId != null){
						MandrillEmail.sendEmail(pickupPerson.getEmail(), pickupPerson.getFirstName() + " " + pickupPerson.getLastName(), 
								"Order No. [" + order.getOrderId() + "] has been assigned to you for drop.", "", order.toString(), true);
						
						message = "Your order [" + order.getOrderId() + "] will be delivered by " + dropPerson.getFirstName() + " " + dropPerson.getLastName();
//						MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), 
//								message, "", order.toString());
					}
					if(message != null){
						List<UserDeviceTokenBeanKey> deviceTokens = AdminDAO.getInstance().getUserTokens(user.getUserId());
						for(UserDeviceTokenBeanKey udtbk : deviceTokens){
							if(udtbk.getDeviceType() == 1){
								String token = GCM.sendMessage(udtbk.getDeviceToken(), "STATUS_" + order.getOrderId(), message, GCM.ACTION_STATUS_CHANGE,
										GCM.URL_STATUS_CHANGE, order.getOrderId());
								UserDAO dao = UserDAO.getInstance();
								dao.deleteDeviceToken(udtbk);
								if(token != null && !token.equals(udtbk.getDeviceToken())){
									
									dao.addUserDeviceToken(udtbk.getUserId(), udtbk.getDeviceType(), token);
								}
							}
						}
					}
				}
				else{
					response = new ResponseMessage("Error updating order.");
					status = Response.Status.INTERNAL_SERVER_ERROR;
				}
			}
		}
		else{
			status = Response.Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}