
package com.laundry.rest.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.laundry.dao.PromotionDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.Promotion;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.util.Util;
 
@Path("/promotion")
public class PromotionService {
	
	private static final Logger logger = LogManager.getLogger(PromotionService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("authorization") String authString, @QueryParam("promotionCode") String promotionCode, 
			@QueryParam("amount") BigDecimal amount) {
		logger.info("Request recieved for fetching promotionCode");
		Object response = null;
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			PromotionDAO dao = PromotionDAO.getInstance();
			Promotion promotion = dao.getPromotion(promotionCode, user.getUserId());
			status = Status.BAD_REQUEST;
			String message = Util.validatePromotionCode(promotion, amount, user.getUserId());
			if(message != null){
				response = new ResponseMessage(message);
			}
			else{
				BigDecimal discount = BigDecimal.ZERO;
				if(promotion.getPercentageCredit() > 0){
					Map<String, String> map = new HashMap<>();
					discount = amount.multiply(BigDecimal.valueOf(promotion.getPercentageCredit())).divide(BigDecimal.valueOf(100));
					if(discount.compareTo(promotion.getMaxCredit()) > 0){
						discount = promotion.getMaxCredit();
					}
					discount.setScale(2, RoundingMode.HALF_EVEN);
					map.put("discount", discount.toString());
					map.put("promotionId", promotion.getPromotionId().toString());
					map.put("message", "Congratulations! Rs. " + discount.toString() + " cash back will be added to your wallet.");
					status = Status.OK;
					response = map;
				}
				else if(promotion.getFixedCredit().compareTo(BigDecimal.ZERO) > 0){
					Map<String, String> map = new HashMap<>();
					map.put("discount", promotion.getFixedCredit().toString());
					map.put("promotionId", promotion.getPromotionId().toString());
					map.put("message", "Congratulations! Rs. " + promotion.getFixedCredit().toString() + " cash back will be added to your wallet.");
					status = Status.OK;
					response = map;
				}
				else{
					response = new ResponseMessage("The promo code is invalid.");
				}
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Promotion promotion) {
		logger.info("Request recieved for adding promotion");
		Object response = null;
		Status status = Status.INTERNAL_SERVER_ERROR;;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			PromotionDAO dao = PromotionDAO.getInstance();
			String message = promotion.validate();
			if(message != null){
				response = new ResponseMessage(message);
				status = Status.BAD_REQUEST;
			}
			else{
				if(dao.exists(promotion.getPromotionCode())){
					response = new ResponseMessage("Promotion code already exists.");
					status = Status.BAD_REQUEST;
				}
				else{
					dao.addPromotion(promotion);
					status = Status.OK;
				}
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, Promotion promotion) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			String rm = promotion.validate();
			if(rm == null){
				boolean promotionAdded = PromotionDAO.getInstance().update(promotion);
				if(promotionAdded){
					response.put("message", "Promotion updated successfully.");
					status = Status.OK;
					response.put("promotion", promotion);
				}
				else{
					response.put("message", "Error updating promotion.");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
			else{
				response.put("message", rm);
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
	public Response delete(@HeaderParam("authorization") String authString, @QueryParam("promotionId") Long promotionId) {
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser adminUser = Util.getAdminUser(authString);
		if(adminUser != null){
			boolean deleted = PromotionDAO.getInstance().delete(promotionId);
			if(deleted){
				response.put("message", "Promotion deleted successfully.");
				status = Status.OK;
			}
			else{
				response.put("message", "Error deleting Promotion.");
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
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@HeaderParam("authorization") String authString) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = PromotionDAO.getInstance().getAll();
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized to perform this operation");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

}