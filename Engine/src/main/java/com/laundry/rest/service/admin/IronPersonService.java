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
import com.laundry.pojo.IronPerson;
import com.laundry.pojo.Order;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.util.Util;
 
@Path("/admin/ironPerson")
public class IronPersonService {
	
	private static final Logger logger = LogManager.getLogger(IronPersonService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, IronPerson ironPerson) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(ironPerson.isValid()){
				boolean ironPersonExists = AdminDAO.getInstance().ironPersonExists(ironPerson.getEmail());
				if(ironPersonExists){
					response.put("message", "Iron person with email " + ironPerson.getEmail() + " already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					boolean ironPersonAdded = AdminDAO.getInstance().addIronPerson(ironPerson);
					if(ironPersonAdded){
						response.put("message", "Iron person added successfully.");
						status = Status.OK;
						response.put("ironPerson", ironPerson);
					}
					else{
						response.put("message", "Error adding iron person.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				response.put("message", "Invalid iron person details. Please verify.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, IronPerson ironPerson) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(ironPerson.isValid()){
				boolean ironPersonAdded = AdminDAO.getInstance().updateIronPerson(ironPerson);
				if(ironPersonAdded){
					response.put("message", "Iron person updated successfully.");
					status = Status.OK;
					response.put("ironPerson", ironPerson);
				}
				else{
					response.put("message", "Error updating iron person.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", "Invalid iron person details. Please verify.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
		}
		return Response.status(status).entity(response).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("ironPersonId") Long ironPersonId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean ironPersonDeleted = AdminDAO.getInstance().deleteIronPerson(ironPersonId);
			if(ironPersonDeleted){
				response.put("message", "Iron person deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting iron person.");
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assign(@HeaderParam("authorization") String authString, @QueryParam("orderId") Long orderId, 
			@QueryParam("ironPersonId") Long ironPersonId){
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(orderId == null || ironPersonId == null ){
				response = new ResponseMessage("orderId and ironPersonId is required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				Order order = AdminDAO.getInstance().setIronPerson(orderId, ironPersonId);
				if(order != null){
					response = new ResponseMessage("Order updated successfully.");
					status = Response.Status.OK;
					
					User user = order.getUser();
					IronPerson ironPerson = order.getIronPerson();
					
					MandrillEmail.sendEmail(ironPerson.getEmail(), ironPerson.getFirstName() + " " + ironPerson.getLastName(), 
							"Order No. [" + order.getOrderId() + "] has been assigned to you for drop.", "", order.toString(), true);
					
					if(order.isStatusUpdated()){
						String message = "Your clothes for order [" + order.getOrderId() + "] have been received.";
						MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), 
								message, "", order.toString(), false);
						
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
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("authorization") String authString, @QueryParam("storeId") Long storeId) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = AdminDAO.getInstance().getIronPersons(storeId);
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("User is unauthorized");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("queue")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQueue(@HeaderParam("authorization") String authString, @QueryParam("ironPersonId") Long ironPersonId, 
			@QueryParam("storeId") Long storeId) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = AdminDAO.getInstance().getIronPersonQueue(ironPersonId, storeId);
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("User is unauthorized");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}