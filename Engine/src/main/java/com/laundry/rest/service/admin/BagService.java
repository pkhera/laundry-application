package com.laundry.rest.service.admin;
import java.util.HashMap;
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

import com.laundry.dao.StoreBagDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.Order;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.StoreBag;
import com.laundry.util.Util;
 
@Path("/admin/bag")
public class BagService {
	
	private static final Logger logger = LogManager.getLogger(BagService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, StoreBag storeBag) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = storeBag.validate();
			if(rm == null){
				boolean storeBagExists = StoreBagDAO.getInstance().exists(storeBag.getBagNumber());
				if(storeBagExists){
					response.put("message", "Bag with number " + storeBag.getBagNumber() + " already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					boolean storeBagAdded = StoreBagDAO.getInstance().add(storeBag);
					if(storeBagAdded){
						response.put("message", "Bag added successfully.");
						status = Status.OK;
						response.put("bag", storeBag);
					}
					else{
						response.put("message", "Error adding bag.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				response.put("message", "Invalid bag details. Please verify.");
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
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("storeId") Long storeId, 
			@QueryParam("bagId") Long bagId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean storeBagDeleted = StoreBagDAO.getInstance().delete(storeId, bagId);
			if(storeBagDeleted){
				response.put("message", "Bag deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting bag.");
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
	public Response get(@HeaderParam("authorization") String authString, @QueryParam("storeId") Long storeId) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = StoreBagDAO.getInstance().getAll(storeId);
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized to perform this operation");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

	@PUT
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assign(@HeaderParam("authorization") String authString, @QueryParam("orderId") Long orderId, 
			@QueryParam("bagId") Long bagId){
		Object response = null;
		Response.Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(orderId == null || bagId == null ){
				response = new ResponseMessage("orderId and bagId is required.");
				status = Response.Status.BAD_REQUEST;
			}
			else{
				Order order = StoreBagDAO.getInstance().setBagForOrder(orderId, bagId);
				if(order != null){
					response = new ResponseMessage("Order updated successfully.");
					status = Response.Status.OK;
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