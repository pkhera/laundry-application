package com.laundry.rest.service.admin;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.mandrill.MandrillEmail;
import com.laundry.pojo.AdminUser;
import com.laundry.rest.util.SMSUtil;
import com.laundry.util.Util;
 
@Path("/adminservice/")
public class AdminService {
	
	private static final Logger logger = LogManager.getLogger(AdminService.class);
	
	@POST
	@Path("/sms")
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, @QueryParam("enable") boolean enable) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			if(enable){
				SMSUtil.enableSMS();
			}
			else{
				SMSUtil.disableSMS();
			}
			response.put("message", "SMS service has been " + (enable ? "enabled" : "disabled"));
			response.put("enabled", enable);
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/sms")
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			response.put("enabled", SMSUtil.getSMSServiceStatus());
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Path("/deliveryManager")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deliveryManager(@HeaderParam("authorization") String authString, @QueryParam("enable") boolean enable) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			Util.setDeliveryManager(enable);
			response.put("message", "Delivery Manager has been " + (enable ? "enabled" : "disabled"));
			response.put("enabled", enable);
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/deliveryManager")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDeliveryManager(@HeaderParam("authorization") String authString) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			response.put("enabled", Util.isDeliveryManagerEnabled());
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Path("/email")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setEmailEnabled(@HeaderParam("authorization") String authString, @QueryParam("enable") boolean enable) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			MandrillEmail.setEnableEmail(enable);
			response.put("message", "Email service has been " + (enable ? "enabled" : "disabled"));
			response.put("enabled", enable);
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/email")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmailEnabled(@HeaderParam("authorization") String authString) {
		Map<String, Object> response = new HashMap<>();
		Status status = Status.BAD_REQUEST;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			response.put("enabled", MandrillEmail.isEnableEmail());
			status = Status.OK;
		}
		else{
			response.put("message", "You are unauthorized to perform this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}