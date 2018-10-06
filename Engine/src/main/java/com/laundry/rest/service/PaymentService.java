package com.laundry.rest.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.laundry.dao.LaundryDAO;
import com.laundry.dao.PromotionDAO;
import com.laundry.dao.UserDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.PaymentTransaction;
import com.laundry.pojo.Promotion;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.rest.util.SMSUtil;
import com.laundry.util.Util;
import com.paytm.merchant.CheckSumServiceHelper;
 
@Path("/payment")
public class PaymentService {
	
	private static final Logger logger = LogManager.getLogger(PaymentService.class);
	
	private static final String MID = "MID";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String CUST_ID = "CUST_ID";
	private static final String TXN_AMOUNT = "TXN_AMOUNT";
	private static final String CHANNEL_ID = "CHANNEL_ID";
	private static final String INDUSTRY_TYPE_ID = "INDUSTRY_TYPE_ID";
	private static final String WEBSITE = "WEBSITE";
	private static final String MOBILE_NO = "MOBILE_NO";
	private static final String EMAIL = "EMAIL";
	private static final String CHECKSUMHASH = "CHECKSUMHASH";
	private static final String CALLBACK_URL = "CALLBACK_URL";
	
	private static final String URL = "url";
	
	public static final String PAYTM_STATUS_URL = Util.getApplicationProperty("paytm.status");
	public static final String PAYTM_TRANSACTION_URL = Util.getApplicationProperty("paytm.transaction");
	public static final String PAYTM_REDIRECT_URL = Util.getApplicationProperty("paytm.redirect");
	
	private static final String WEBSITE_VALUE = Util.getApplicationProperty("paytm.website");
	private static final String MERCHANT_KEY = Util.getApplicationProperty("paytm.merchantKey");
	public static final String MID_VALUE = Util.getApplicationProperty("paytm.mid");
	private static final String CHANNEL_ID_VALUE = "WEB";
	private static final String INDUSTRY_TYPE_ID_VALUE = Util.getApplicationProperty("paytm.industry"); //"Retail";
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChecksum(@HeaderParam("authorization") String authString, @QueryParam("amount") String amount,
			@QueryParam("promotionId") Long promotionId) {
		logger.info("Request recieved for adding address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			PaymentTransaction payment = new PaymentTransaction();
			payment.setTransactionAmount(new BigDecimal(amount));
			payment.setUserId(user.getUserId());
			payment.setPromotionId(promotionId);
			LaundryDAO dao = LaundryDAO.getInstance();
			
			Long paymentTransactionId = dao.addPaymentTransaction(payment);
			TreeMap<String,String> parameters = new TreeMap<String,String>();
			parameters.put(MID, MID_VALUE); // Merchant ID (MID) provided by Paytm
			parameters.put(ORDER_ID, String.valueOf(paymentTransactionId)); // Merchant�s order id
			parameters.put(CUST_ID, String.valueOf(user.getUserId())); // Customer ID registered with merchant
			parameters.put(TXN_AMOUNT, amount);
			parameters.put(CHANNEL_ID, CHANNEL_ID_VALUE);
			parameters.put(INDUSTRY_TYPE_ID, INDUSTRY_TYPE_ID_VALUE); //Provided by Paytm
			parameters.put(WEBSITE, WEBSITE_VALUE); //Provided by Paytm
			parameters.put(MOBILE_NO, user.getPhoneNumber());
			parameters.put(EMAIL, user.getEmail());
			parameters.put(CALLBACK_URL, PAYTM_REDIRECT_URL + "#/confirmOrder/" + paymentTransactionId);
			
			CheckSumServiceHelper checkSumServiceHelper = CheckSumServiceHelper.getCheckSumServiceHelper();
			String checkSum;
			try {
				checkSum = checkSumServiceHelper.genrateCheckSum(MERCHANT_KEY, parameters);
				logger.debug("Checksum " + checkSum);
				status = Status.OK;
				response.putAll(parameters);
				response.put(CHECKSUMHASH, checkSum);
				response.put(URL, PAYTM_TRANSACTION_URL);
			} catch (Exception e) {
				response.put("message", e.getMessage());
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getStatus(@HeaderParam("authorization") String authString, @QueryParam("transactionId") Long transactionId) {
		logger.info("Request recieved for adding address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			status = getPaymentStatus(transactionId, user, response);
		}
		else{
			status = Status.UNAUTHORIZED;
			response.put("message", "You are not authorised to view this transaction.");
		}
		return Response.status(status).entity(response).build();
	}
	
	public static Status getPaymentStatus(Long transactionId, User user, Map<String,Object> response){
		Status status = null;
		LaundryDAO dao = LaundryDAO.getInstance();
		UserDAO userDAO = UserDAO.getInstance();
		PaymentTransaction payment = dao.getPaymentTransaction(transactionId);
		Long userId = payment.getUserId();
		Long promotionId = payment.getPromotionId();
		if(user.getUserId().equals(userId)){
			if(payment == null || payment.getStatus() == null){
				Client client = null;
				Response res = null;
				try{
					StringBuilder params = new StringBuilder("JsonData=");
					params.append(URLEncoder.encode("{\"MID\"","UTF8")).append(":").append(URLEncoder.encode("\""+ MID_VALUE + "\",\"ORDERID\"", "UTF8")).append(":").append(URLEncoder.encode("\"","UTF8")).append(transactionId).append(URLEncoder.encode("\"}", "UTF8"));
					String url = PAYTM_STATUS_URL + "?" + params.toString();
					client = ClientBuilder.newClient();
					logger.info("Getting transaction status from paytm : " + transactionId);
					res = client.target(url).request().get();
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(res.readEntity(String.class));

					payment = new PaymentTransaction(json);
					payment.setPaymentTransactionId(transactionId);
					payment.setUserId(userId);
					payment.setPromotionId(promotionId);
					dao.updatePaymentTransaction(payment);
					if(payment.getStatus().equals(PaymentTransaction.TXN_SUCCESS)){
						SMSUtil.sendWalletRechargeSMS(user.getPhoneNumber(), userDAO.getWalletBalance(user.getUserId()).toString(), payment.getPaymentTransactionId().toString(), payment.getTransactionAmount().toString());
					}
					if(payment.getPromotionId() != null){
						PromotionDAO promotionDAO = PromotionDAO.getInstance();
						Promotion promotion = promotionDAO.getPromotion(payment.getPromotionId(), user.getUserId());
						String message = Util.validatePromotionCode(promotion, payment.getTransactionAmount(), user.getUserId());
						if(message != null){
							logger.error(message);
						}
						else{
							BigDecimal cashback = null;
							if(promotion.getPercentageCredit() > 0){
								cashback = payment.getTransactionAmount().multiply(BigDecimal.valueOf(promotion.getPercentageCredit())).divide(BigDecimal.valueOf(100));
								if(cashback.compareTo(promotion.getMaxCredit()) > 0){
									cashback = promotion.getMaxCredit();
								}
								cashback.setScale(2, RoundingMode.HALF_EVEN);
							}
							else if(promotion.getFixedCredit().compareTo(BigDecimal.ZERO) > 0){
								cashback = promotion.getFixedCredit();
							}
							if(cashback != null){
								dao.addTransaction(payment.getPaymentTransactionId(), null, user.getUserId(), cashback);
							}
						}
					}
					response.putAll(json);
					status = Status.OK;
				}
				catch(Exception ex){
					logger.error("Error getting transaction status for transaction id : " + transactionId, ex);
					response.put("message", ex.getMessage());
					status = Status.BAD_REQUEST; 
				}
				finally{
					if(res != null){
						res.close();
					}
					if(client != null){
						client.close();
					}
				}
			}
			else{
				response.putAll(payment.toJson());
				status = Status.OK;
			}
		}
		else{
			status = Status.FORBIDDEN;
			response.put("message", "You are not authorised to view this transaction.");
		}
		return status;
	}
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPaymentHistory(@HeaderParam("authorization") String authString) {
		logger.info("Request recieved for adding address");
		Status status = null;
		Object response = null;
		User user = Util.getUser(authString);
		if(user != null){
			LaundryDAO dao = LaundryDAO.getInstance();
			response = dao.getSuccessfulPaymentTransactions(user.getUserId());
			status = Status.OK;
		}
		else{
			status = Status.UNAUTHORIZED;
			response = new ResponseMessage("You are not authorised to view this transaction.");
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/balance")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWalletBalance(@HeaderParam("authorization") String authString) {
		logger.info("Request recieved for wallet balance");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null){
			UserDAO dao = UserDAO.getInstance();
			BigDecimal balance = dao.getWalletBalance(user.getUserId());
			if(balance == null){
				balance = BigDecimal.ZERO;
			}
			response.put("balance", balance);
			status = Status.OK;
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}

	@POST
	@Path("/addCashPayment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addCashPayment(@HeaderParam("authorization") String authString, Map<String,String> input) {
		logger.info("Request recieved for adding cash payment");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if(user != null){
			Long userId = Long.valueOf(input.get("userId"));
			BigDecimal amount = new BigDecimal(input.get("cashPaid"));
			PaymentTransaction paymentTransaction = new PaymentTransaction();
			paymentTransaction.setGatewayName("CASH");
			paymentTransaction.setStatus(PaymentTransaction.TXN_SUCCESS);
			paymentTransaction.setTransactionAmount(amount);
			paymentTransaction.setTransactionDateTime(new Date());
			paymentTransaction.setUserId(userId);
			LaundryDAO dao = LaundryDAO.getInstance();
			Long transactionId = dao.addPaymentTransaction(paymentTransaction);
			if(transactionId != null){
				dao.addTransaction(transactionId, null, userId, amount);
				response.put("transactionId", transactionId);
				status = Status.OK;
			}
			else{
				response.put("message", "Transaction failed. Unable to add cash payment transaction");
				status = Status.INTERNAL_SERVER_ERROR;
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
}