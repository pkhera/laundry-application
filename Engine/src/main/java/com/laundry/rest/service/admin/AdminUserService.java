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

import com.laundry.dao.AdminDAO;
import com.laundry.dao.AdminUserDAO;
import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.NewPassword;
import com.laundry.pojo.ResponseMessage;
import com.laundry.util.Util;
 
@Path("/admin")
public class AdminUserService {
	
	private static final Logger logger = LogManager.getLogger(AdminUserService.class);
	
	@POST
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@HeaderParam("authorization") String authString, NewPassword changePassword){
		boolean passwordChanged = false;
		ResponseMessage responseMessage = null;
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(adminUser.getPassword().equals(changePassword.getPassword())){
				if(changePassword.isValid()){
					passwordChanged = AdminDAO.getInstance().changeAdminPassword(adminUser.getEmail(), changePassword.getNewPassword()); 
					if(passwordChanged){
						responseMessage = new ResponseMessage("Password changed successfully");
						status = Status.OK;
					}
					else{
						responseMessage = new ResponseMessage("Email does not exist.");
						status = Status.BAD_REQUEST;
					}
				}
				else{
					responseMessage = new ResponseMessage("Passwords do not match.");
					status = Status.BAD_REQUEST;
				}
			}
			else{
				responseMessage = new ResponseMessage("Current password is invalid.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(AdminUser user) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		if(user.isValid()){
			boolean userExists = AdminDAO.getInstance().adminUserExists(user.getEmail());
			if(userExists){
				response.put("message", user.getEmail() + " is already registered.");
				status = Status.BAD_REQUEST;
			}
			else{
				boolean userAdded = AdminDAO.getInstance().addAdminUser(user);
				if(userAdded){
					response.put("message", "User added successfully.");
					status = Status.OK;
					response.put("user", user);
				}
				else{
					response.put("message", "Error adding user.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
		}
		else{
			response.put("message", "Invalid user details. Please verify.");
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, AdminUser user) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser loggedInUser = Util.getAdminUser(authString);
		if(loggedInUser != null){
			if(user.isValid()){
				boolean userUpdated = AdminDAO.getInstance().updateAdminUser(user);
				if(userUpdated){
					user = AdminDAO.getInstance().getAdminUser(user.getEmail());
					response.put("message", "User updated successfully.");
					status = Status.OK;
					response.put("user", user);
				}
				else{
					response.put("message", "Error updating user.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", "Invalid user details. Please verify.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "You are not authorized for this operation");
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
			response = user;
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("User is unauthorized");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/adminusers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAdminUsers(@HeaderParam("authorization") String authString) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = AdminUserDAO.getInstance().getAll();
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("recoverPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response forgotPassword(@QueryParam("email") String email) {
		ResponseMessage responseMessage = null;
		Status status = null;
		AdminUser user = AdminDAO.getInstance().getAdminUser(email);
		if( user != null){
			MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + user.getLastName(), "Password Recovery" 
					, null, "Login Credentials for Laundry : \nEmail - " + user.getEmail() + "\nPassword - " + user.getPassword(), false);
				responseMessage = new ResponseMessage("Password has been sent to " + user.getEmail());
				status = Status.OK;
		}
		else{
			responseMessage = new ResponseMessage("User does not exist");
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean userDeleted = AdminUserDAO.getInstance().delete(userId);
			if(userDeleted){
				response.put("message", "Admin user deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting admin user.");
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
}