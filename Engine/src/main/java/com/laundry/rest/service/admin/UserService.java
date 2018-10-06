package com.laundry.rest.service.admin;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
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

import com.laundry.dao.LaundryDAO;
import com.laundry.dao.UserDAO;
import com.laundry.pojo.Address;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.util.Util;
 
@Path("/admin/user")
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private static final String MASTER_PASSWORD = "changeit";
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@HeaderParam("authorization") String authString) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			UserDAO dao = UserDAO.getInstance();
			response = dao.getAllUsers();
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFlatNumbersForUser(@HeaderParam("authorization") String authString, @QueryParam("�serId") Long userId, 
			@QueryParam("societyId") Long societyId) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			UserDAO dao = UserDAO.getInstance();
			response = dao.getAddressesForUserAndSociety(userId, societyId);
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@HeaderParam("authorization") String authString, User user) {
		logger.info("Request recieved to add user from admin");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			UserDAO dao = UserDAO.getInstance();
			ResponseMessage rm = user.validateForAdmin();
			if(user.getEmail() != null && !user.getEmail().trim().isEmpty()){
			Long userId = dao.userExists(user.getEmail());
				if(userId != null){
					rm = new ResponseMessage("User with email - " + user.getEmail() + " - already exists.");
				}
			}
			if(rm == null){
				for(Address address : user.getAddresses().values()){
					if(address != null && address.getSociety() != null){
						user.setDefaultAddress(address);
						break;
					}
				}
				boolean userAdded = dao.addUser(user);
				Map<String,Address> addresses = new HashMap<>();
				if(user.getDefaultAddress()!=null){
					addresses.put(user.getDefaultAddress().getLabel(), user.getDefaultAddress());
				}
				user.setAddresses(addresses);
				if(userAdded){
					response.put("message", "User added successfully.");
					response.put("user", user);
					status = Status.OK;
				}
				else{
					response.put("message", "Error adding user. Please try again after some time.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", rm.getMessage());
				status = Status.BAD_REQUEST;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Path("/address")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAddressForUser(@HeaderParam("authorization") String authString, Address address) {
		logger.info("Request recieved for adding address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			ResponseMessage rm = address.validate();
			if(rm == null || address.getUserId() == null){
				UserDAO dao = UserDAO.getInstance();
				boolean addressAdded = dao.addAddress(address);
				if(addressAdded){
					address = dao.getAddress(address.getAddressId());
					response.put("message", "Address added successfully.");
					status = Status.OK;
					response.put("address", address);
				}
				else{
					response.put("message", "Error adding address.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", rm.getMessage());
				status = Status.BAD_REQUEST;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Path("/addCredit")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addCredit(@HeaderParam("authorization") String authString, Map<String,String> input) {
		String userId = input.get("userId");
		String amount = input.get("amount");
		String masterPassword = input.get("masterPassword");
		logger.info("Request recieved for adding credit: " + amount + " for user " +  userId);
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser loggedInUser = Util.getAdminUser(authString);
		LaundryDAO dao = LaundryDAO.getInstance();
		if(loggedInUser != null && MASTER_PASSWORD.equals(masterPassword)){
			try{
				dao.addTransaction(null, -2L, Long.parseLong(userId), new BigDecimal(amount));
				response.put("message", "User updated successfully.");
				status = Status.OK;
			}
			catch(NumberFormatException ex){
				response.put("message", "Error adding credit. Please check the format of the amount.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "User is unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, User user) {
		logger.info("Request recieved for updating user");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser loggedInUser = Util.getAdminUser(authString);
		UserDAO dao = UserDAO.getInstance();
		if(loggedInUser != null){
			boolean userUpdated = dao.updateUser(user);
			if(userUpdated){
				response.put("message", "User updated successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error updating user. Please try again after some time.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			response.put("message", "User is unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
}