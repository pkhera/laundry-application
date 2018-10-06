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

import com.laundry.dao.SocietyDAO;
import com.laundry.dao.StoreDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.Society;
import com.laundry.util.Util;
 
@Path("/admin/society")
public class SocietyService {
	
	private static final Logger logger = LogManager.getLogger(SocietyService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Society society) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = society.validate();
			if(rm == null){
				boolean societyExists = StoreDAO.getInstance().exists(society.getName());
				if(societyExists){
					response.put("message", "Society with name " + society.getName() + " already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					boolean societyAdded = SocietyDAO.getInstance().add(society);
					if(societyAdded){
						response.put("message", "Society added successfully.");
						status = Status.OK;
						response.put("society", society);
					}
					else{
						response.put("message", "Error adding society.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
			}
			else{
				response.put("message", "Invalid society details. Please verify.");
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
	public Response update(@HeaderParam("authorization") String authString, Society society) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = society.validate();
			if(rm == null){
				boolean societyAdded = SocietyDAO.getInstance().update(society);
				if(societyAdded){
					response.put("message", "Society updated successfully.");
					status = Status.OK;
					response.put("society", society);
				}
				else{
					response.put("message", "Error updating society.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", "Invalid society details. Please verify.");
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
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("societyId") Long societyId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean societyDeleted = SocietyDAO.getInstance().delete(societyId);
			if(societyDeleted){
				response.put("message", "Society deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting Society.");
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
			if(storeId == null){
				response = SocietyDAO.getInstance().getAll();
			}
			else{
				response = SocietyDAO.getInstance().getSocietyForStore(storeId);
			}
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized to perform this operation");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}