package com.laundry.rest.service.admin;
import java.util.HashMap;
import java.util.Map;

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

import com.laundry.dao.StoreDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.Store;
import com.laundry.util.Util;
 
@Path("/admin/store")
public class StoreService {
	
	private static final Logger logger = LogManager.getLogger(StoreService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Store store) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = store.validate();
			if(rm == null){
				boolean storeExists = StoreDAO.getInstance().exists(store.getName());
				if(storeExists){
					response.put("message", "Store with name " + store.getName() + " already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					boolean storeAdded = StoreDAO.getInstance().add(store);
					if(storeAdded){
						response.put("message", "Store added successfully.");
						status = Status.OK;
						response.put("store", store);
					}
					else{
						response.put("message", "Error adding store.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				response.put("message", "Invalid store details. Please verify.");
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
	public Response update(@HeaderParam("authorization") String authString, Store store) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = store.validate();
			if(rm == null){
				boolean storeAdded = StoreDAO.getInstance().update(store);
				if(storeAdded){
					response.put("message", "Store updated successfully.");
					status = Status.OK;
					response.put("store", store);
				}
				else{
					response.put("message", "Error updating store.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", "Invalid store details. Please verify.");
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
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("storeId") Long storeId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean storeDeleted = StoreDAO.getInstance().delete(storeId);
			if(storeDeleted){
				response.put("message", "Store deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting store.");
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
	public Response get(@HeaderParam("authorization") String authString) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = StoreDAO.getInstance().getAll();
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized to perform this operation");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}