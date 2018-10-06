package com.laundry.rest.service;
import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundry.dao.LaundryDAO;
import com.laundry.dao.UserDAO;
import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.Address;
import com.laundry.pojo.NewPassword;
import com.laundry.pojo.Order;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.rest.util.SMSUtil;
import com.laundry.util.Util;
 
@Path("/user")
public class UserService {
	
	private static final Logger logger = LogManager.getLogger(UserService.class);
	
	/**
	 * 
	 * @api {post} /user/changePassword Change Password
	 * @apiName Change Password
	 * @apiGroup User
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {String} password  User's password.
	 * @apiParam {String} newPassword  New password.
	 * @apiParam {String} confirmNewPassword  Confirm new password.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 *
	 * @apiExample Sample Request Payload
	 * 	{
	 *	  "password": "password",
	 *	  "newPassword": "password",
	 *	  "confirmNewPassword": "password"
	 *	}
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 * 		"message": "Password changed successfully"
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "Passwords do not match."
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
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@HeaderParam("authorization") String authString, NewPassword changePassword){
		logger.info("Request recieved for password change");
		boolean passwordChanged = false;
		ResponseMessage responseMessage = null;
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			if(user.getPassword().equals(changePassword.getPassword())){
				if(changePassword.isValid()){
					UserDAO dao = UserDAO.getInstance();
					passwordChanged = dao.changePassword(user.getEmail(), changePassword.getNewPassword()); 
					if(passwordChanged){
						responseMessage = new ResponseMessage("Password changed successfully");
						status = Status.OK;
						
						MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
								"Password Change for BrandName", null, "Your password for BrandName has been changed.\nNew Password : " + changePassword.getNewPassword(), false);
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
				responseMessage = new ResponseMessage("Please provide a valid current password.");
				status = Status.BAD_REQUEST;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	/**
	 * @api {post} /user Add User
	 * @apiName Add User
	 * @apiGroup User
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {String} firstName  User's First Name.
	 * @apiParam {String} lastName  User's Last Name.
	 * @apiParam {String} email  User's Email.
	 * @apiParam {String} password  User's Password.
	 * @apiParam {String} confirmPassword  Confirm Password.
	 * @apiParam {String} phoneNumber  User's Phone Number.
	 * @apiParam {String} defaultAddress.society.societyId  Society Id .
	 * @apiParam {String} defaultAddress.flatNumber  Flat Number.
	 * @apiParam {String} defaultAddress.label  Address Label.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 * @apiSuccess {String} user.userId  User Id.
	 * @apiSuccess {String} user.email  User's email.
	 * @apiSuccess {String} user.firstName  User's first name.
	 * @apiSuccess {String} user.lastName  User's last name.
	 * @apiSuccess {String} user.phoneNumber  User's phone number.
	 * @apiSuccess {String} user.defaultAddress.addressId Address id.
	 * @apiSuccess {String} user.defaultAddress.userId  
	 * @apiSuccess {String} user.defaultAddress.flatNumber  Flat number.
	 * @apiSuccess {String} user.defaultAddress.society.societyId  Society Id.
	 * @apiSuccess {String} user.defaultAddress.label  Label.
	 * 
	 *
	 * @apiExample Sample Request Payload
	 * {
	 * 		"firstName":	"User",
	 * 		"lastName": "1",
	 * 		"email":	"1@u.com",
	 * 		"password":	"changeit",
	 * 		"confirmPassword": "changeit",
	 *   	"phoneNumber":	"9876543210",
	 *   	"defaultAddress": {
	 *    		"society": {
	 *          	"societyId": "1"
	 *          },
	 *          "flatNumber": "C-123",
	 *          "label": "Home"
	 *       }
	 * }
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
     *		"message": "User added successfully.",
     *		"user": {
     *		   "userId": 38,
     *		   "email": "1@u.com",
     *		   "firstName": "User",
     *		   "lastName": "1",
     *		   "phoneNumber": "9876543210",
     *		   "defaultAddress": {
     *		       "addressId": 29,
     *		       "userId": 38,
     *		       "flatNumber": "C-123",
     *		       "society": {
     *		           "societyId": 1
     *		       },
     *		       "label": "Home"
     *		   }
     *		}
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "1@u.com is already registered."
	 * }
	 * 
	 * @param user
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(User user, @QueryParam("deviceType") Integer deviceType, @QueryParam("deviceToken") String deviceToken) {
		logger.info("Request recieved to add user");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		ResponseMessage rm = null;
		boolean isSocialSignup = user.getProviderUserId() != null
				&& !user.getProviderUserId().isEmpty()
				&& user.getProviderName() != null
				&& !user.getProviderName().isEmpty();
		if(!isSocialSignup){
			rm = user.validate();
		}
		if(rm == null){
			UserDAO dao = UserDAO.getInstance();
			Long userId = dao.userExists(user.getEmail());
			if(userId != null){
				boolean userExists = true;
				if(isSocialSignup){
					user.setUserId(userId);
					userExists = dao.setUserProviderDetails(user);
				}
				if(userExists){
					user = dao.getUser(userId);
					System.out.println("Password - " + user.getPassword());
					System.out.println(user);
					if(user.getPassword() == null || user.getPassword().isEmpty()){
						response.put("message", user.getEmail() + " is already registered. <a onclick=\"showResetPassword()\">Click here</a> to get access to your profile.");
						status = Status.CONFLICT;
					}
					else{
						response.put("message", user.getEmail() + " is already registered. <a onclick=\"showForgotPassword()\">Forgot password?</a>");
						status = Status.BAD_REQUEST;
					}
				}
				else{
					response.put("message", "User added successfully.");
					response.put("user", user);
					status = Status.OK;
				}
			}
			else{
				boolean userAdded = dao.addUser(user);
				if(deviceToken != null && deviceType != null){
					dao.addUserDeviceToken(user.getUserId(), deviceType, deviceToken);
				}
				Map<String,Address> addresses = new HashMap<>();
				if(user.getDefaultAddress()!=null){
					addresses.put(user.getDefaultAddress().getLabel(), user.getDefaultAddress());
				}
				user.setAddresses(addresses);
				if(userAdded){
					response.put("message", "User added successfully.");
					response.put("user", user);
					status = Status.OK;
					
					SMSUtil.sendWelcomeSMS(user.getPhoneNumber(), "+91 9876543210", user.getFirstName(), user.getEmail(), user.getPassword());
					MandrillEmail.sendWelcomeEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getPassword(),
							"+91 9876543210", "+91 9876543210", "12345");
				}
				else{
					response.put("message", "Error adding user. Please try again after some time.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
		}
		else{
			response.put("message", rm.getMessage());
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(response).build();
	}
	
	/**
	 * @api {put} /user Update User
	 * @apiName Update User
	 * @apiGroup User
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {String} userId  User Id.
	 * @apiParam {String} firstName  User's First Name.
	 * @apiParam {String} lastName  User's Last Name.
	 * @apiParam {String} phoneNumber  User's Phone Number.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 * @apiSuccess {String} user.userId  User Id.
	 * @apiSuccess {String} user.email  User's email.
	 * @apiSuccess {String} user.firstName  User's first name.
	 * @apiSuccess {String} user.lastName  User's last name.
	 * @apiSuccess {String} user.phoneNumber  User's phone number.
	 * @apiSuccess {String} user.defaultAddress.addressId Address id.
	 * @apiSuccess {String} user.defaultAddress.userId  
	 * @apiSuccess {String} user.defaultAddress.flatNumber  Flat number.
	 * @apiSuccess {String} user.defaultAddress.society.societyId  Society Id.
	 * @apiSuccess {String} user.defaultAddress.label  Label.
	 * 
	 *
	 * @apiExample Sample Request Payload
	 * {
	 * 		"firstName":	"User",
	 * 		"lastName": "1",
	 *   	"phoneNumber":	"9876543210",
	 * }
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
     *		"message": "User updated successfully.",
     *		"user": {
     *		   "userId": 38,
     *		   "email": "1@u.c",
     *		   "firstName": "User",
     *		   "lastName": "1",
     *		   "phoneNumber": "9876543210",
     *		   "defaultAddress": {
     *		       "addressId": 29,
     *		       "userId": 38,
     *		       "flatNumber": "C-123",
     *		       "society": {
     *		           "societyId": 1
     *		       },
     *		       "label": "Home"
     *		   }
     *		}
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "Error updating user. Please try again after some time."
	 * }
	 * 
	 * @param user
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, User user) {
		logger.info("Request recieved for updating user");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User loggedInUser = Util.getUser(authString);
		UserDAO dao = UserDAO.getInstance();
		if(loggedInUser != null && loggedInUser.getUserId().equals(user.getUserId())){
			boolean userUpdated = dao.updateUser(user);
			if(userUpdated){
				user = dao.getUser(user.getUserId());
				response.put("message", "User updated successfully.");
				status = Status.OK;
				response.put("user", user);
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
	
	/**
	 * @api {get} /user Get User
	 * @apiName Get User
	 * @apiGroup User
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiSuccess {String} message  Response message if any.
	 * @apiSuccess {String} user.userId  User Id.
	 * @apiSuccess {String} user.email  User's email.
	 * @apiSuccess {String} user.firstName  User's first name.
	 * @apiSuccess {String} user.lastName  User's last name.
	 * @apiSuccess {String} user.phoneNumber  User's phone number.
	 * @apiSuccess {String} user.registrationTime  User's registration time.
	 * @apiSuccess {String} user.defaultAddress.addressId Address id.
	 * @apiSuccess {String} user.defaultAddress.userId  
	 * @apiSuccess {String} user.defaultAddress.flatNumber  Flat number.
	 * @apiSuccess {String} user.defaultAddress.society.societyId  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.name  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.landmark  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.city  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.pincode  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.state  Society Id.
	 * @apiSuccess {String} user.defaultAddress.society.country  Society Id.
	 * @apiSuccess {String} user.defaultAddress.label  Label.
	 * @apiSuccess {String} user.addresses[].addressId Address id.
	 * @apiSuccess {String} user.addresses[].userId  
	 * @apiSuccess {String} user.addresses[].flatNumber  Flat number.
	 * @apiSuccess {String} user.addresses[].society.societyId  Society Id.
	 * @apiSuccess {String} user.addresses[].society.name  Society Id.
	 * @apiSuccess {String} user.addresses[].society.landmark  Society Id.
	 * @apiSuccess {String} user.addresses[].society.city  Society Id.
	 * @apiSuccess {String} user.addresses[].society.pincode  Society Id.
	 * @apiSuccess {String} user.addresses[].society.state  Society Id.
	 * @apiSuccess {String} user.addresses[].society.country  Society Id.
	 * @apiSuccess {String} user.addresses[].label  Label.
	 * 
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
     *		"message": "User added successfully.",
     *		"user": {
     *		   "userId": 38,
     *		   "email": "1@u.c",
     *		   "firstName": "User",
     *		   "lastName": "1",
     *		   "phoneNumber": "9876543210",
     *		   "registrationTime": "2015-06-30 07:02",
     *		   "defaultAddress": {
     *		       "addressId": 29,
     *		       "userId": 38,
     *		       "flatNumber": "C-123",
     *		       "society": {
     *		          	"societyId": 1,
     *					"name": "Pushkar Residency",
     *			        "landmark": "Paldi",
     *			        "city": "Ahmedabad",
     *			        "pincode": "380006",
     *			        "state": "Gujarat",
     *			        "country": "India"
     *		       },
     *		       "label": "Home"
     *		   },
     *		   "addresses": [
     *		   		{
     *		     		  "addressId": 3,
     *		    		  "userId": 21,
     *		    		  "flatNumber": "C-401",
     *		   			  "society": {
     *		   			       "societyId": 4,
     *		    		       "name": "Pushkar Residency",
     *		   			       "landmark": "Paldi",
     *		       			   "city": "Ahmedabad",
     *		 			       "pincode": "380006",
     *		   			       "state": "Gujarat",
     *		      			   "country": "India"
     *		       			},
     *		       			"label": "Home"
     *		  		}
     *			]
     *		}
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 401 Unauthorized
	 * {
	 * }
	 * 
	 * @param user
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("authorization") String authString, @QueryParam("deviceType") Integer deviceType,
			@QueryParam("deviceToken") String deviceToken) {
		logger.info("Request recieved for fetching user");
		Object response = null;
		Status status = null;
		User user = Util.getUser(authString);
		if( user != null){
			user.setSchedule(getUserSchedule(user.getUserId()));
			if(deviceType != null && deviceToken != null){
				UserDAO dao = UserDAO.getInstance();
				dao.addUserDeviceToken(user.getUserId(), deviceType, deviceToken);
			}
			response = user;
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("Invalid Username or Password");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	/**
	 * @api {get} /user/recoverPassword Forgot Password
	 * @apiName Forgot Password
	 * @apiGroup User
	 *
	 * @apiParam {String} email  User's email.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 * 		"message": "Password has been sent to 1@u.c"
	 * }
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "User does not exist."
	 * }
	 * 
	 *
	 * @param email
	 * @return
	 */
	@GET
	@Path("recoverPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response forgotPassword(@QueryParam("email") String email) {
		logger.info("Request recieved for forgot password");
		ResponseMessage responseMessage = null;
		Status status = null;
		UserDAO dao = UserDAO.getInstance();
		User user = dao.getUser(email);
		if( user != null){
			MandrillEmail.sendEmail(user.getEmail(), user.getFirstName() + user.getLastName(), "Password Recovery"
					, null, "Login Credentials for BrandName : \nEmail - " + user.getEmail() + "\nPassword - " + user.getPassword(), false);
			responseMessage = new ResponseMessage("Your login details have been sent to " + user.getEmail());
			status = Status.OK;
		}
		else{
			responseMessage = new ResponseMessage("The email address is not registered.  <a class=\"hand\" onclick=\"showRegister()\">Register Now</a>");
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	@POST
	@Path("resetPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(Map<String, String> input) {
		logger.info("Request recieved for forgot password");
		ResponseMessage responseMessage = null;
		Status status = null;
		UserDAO dao = UserDAO.getInstance();
		String email = input.get("email");
		String password = input.get("password");
		String confirmPassword = input.get("confirmPassword");		
		User user = dao.getUser(email);
		if(user != null){
			System.out.println(user.getPassword());
			if(user.getPassword() == null || user.getPassword().isEmpty()){
				if(password != null && password.equals(confirmPassword)){
					user.setPassword(password);
					dao.updateUser(user);
					responseMessage = new ResponseMessage("Your password has been reset. Please login to place your orders.");
					status = Status.OK;
				}
				else{
					responseMessage = new ResponseMessage("The passwords do not match. Please try again.");
					status = Status.FORBIDDEN;
				}
			}
			else{
				responseMessage = new ResponseMessage("The password cannot be reset for this user. Use <a class=\"hand\" onclick=\"showForgotPassword()\">Forgot Password</a> or contact support.");
				status = Status.FORBIDDEN;
			}
		}
		else{
			responseMessage = new ResponseMessage("The email address is not registered.  <a class=\"hand\" onclick=\"showRegister()\">Register Now</a>");
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(responseMessage).build();
	}
	
	public static void main(String[] args) {
		UserService us = new UserService();
		System.out.println(us.getUserSchedule(37L));
	}
	public Map<String, Boolean> getUserSchedule(Long userId){
		JsonFactory jf = new JsonFactory();
		MappingIterator<Order[]> orderIterator = null;
		JsonParser jp = null;
		try {
			jp = jf.createParser(OrderService.scheduleFilePath.toFile());
			ObjectMapper om = new ObjectMapper();
			orderIterator = om.readValues(jp, Order[].class);
			if(orderIterator != null && orderIterator.hasNext()){
				Order[] orders = orderIterator.next();
				if(orders != null){
					for(Order o : orders){
						logger.debug(o.getUserId());
						if(userId.equals(o.getUserId())){
							return o.getSchedule();
						}
					}
				}
			}
		}
		catch(IOException e){
			logger.error("Error reading schedules", e);
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
		return null;
	}
}