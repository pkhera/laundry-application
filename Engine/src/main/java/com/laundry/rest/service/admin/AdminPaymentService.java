package com.laundry.rest.service.admin;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.rest.service.PaymentService;
import com.laundry.util.Util;
 
@Path("/admin/payment")
public class AdminPaymentService {
	
	private static final Logger logger = LogManager.getLogger(AdminPaymentService.class);
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPaymentHistory(@HeaderParam("authorization") String authString) {
		logger.info("Request recieved for listing payment transactions");
		Status status = null;
		Object response = null;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			LaundryDAO dao = LaundryDAO.getInstance();
			response = dao.getPaymentTransactions();
			status = Status.OK;
		}
		else{
			status = Status.UNAUTHORIZED;
			response = new ResponseMessage("You are not authorised to view this transaction.");
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStatus(@HeaderParam("authorization") String authString, @QueryParam("transactionId") Long transactionId, 
			@QueryParam("userId") Long userId) {
		logger.info("Request recieved for adding address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			UserDAO dao = UserDAO.getInstance();
			User u = dao.getUser(userId);
			status = PaymentService.getPaymentStatus(transactionId, u, response);
		}
		else{
			status = Status.UNAUTHORIZED;
			response.put("message", "You are not authorised to view this transaction.");
		}
		return Response.status(status).entity(response).build();
	}
}