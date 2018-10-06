package com.laundry.rest.util;

import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMSUtil {
	
	private static final Logger logger = LogManager.getLogger(SMSUtil.class);
	
	private static final String PLACEHOLDER = "\\(variable\\)";
	
	private static boolean enableSMS = false;
	private static String key = "SMS_CLIENT_KEY";
	private	static String senderID = "SMS_SENDER_ID";
	private	static String serviceName = "TEMPLATE_BASED";
	
	private static String welcomeMessage = "Hi (variable)\nWelcome to BrandName. We look forward to serving your daily ironing needs. To place your order call/what's app us at +91 9876543210 or visit us at website.in/#home\nYour login details are:\nEmail - (variable)\nPassword - (variable)";
	private static String orderConfirmationMessage = "Thank you for your order! (variable) will arrive to pickup your clothes.\nOrder Details:\nPickup Time - (variable)\n# of Items - (variable)\nDrop Time - (variable)\n(variable)\n(variable)";
	private static String orderRecievedMessage = "Hi (variable),\nYour clothes for order (variable) have reached the workshop. Final amount due is: Rs. (variable). Track your orders at website.in/orderHistory";
	private static String orderCompletionMessage = "Your order has been delivered. The invoice has been mailed to your registered ID. It was a pleasure serving you.\nFor any queries, please call (variable)";
	private static String pickupOrderMessage = "Pickup for order (variable) has been assign to you.\nPickup Time: (variable)\nFlat #: (variable)\nComments: (variable)\nName: (variable)\nPh. No.: (variable)";
	private static String dropOrderMessage = "Drop for order (variable) has been assign to you.\nDrop Time: (variable)\nFlat #: (variable)\nAmount: Rs. (variable)\nName: (variable)\nPh. No.: (variable)";
	private static String walletRechargeMessage = "Your payment transaction has been completed successfully! Your wallet balance is Rs. (variable)\nTransaction Details:\nID - (variable)\nAmount - Rs. (variable)";

	public static void sendSMS(String phoneNumber, String message){
		if(enableSMS){
			Runnable r = new Runnable() {
				@Override
				public void run() {
					Client client = null;
					Response response = null;
//					System.out.println(message);
					try{
						client = ClientBuilder.newClient();
						response = client.target("http://smsapi.24x7sms.com/api_2.0/SendSMS.aspx?APIKEY=" + key 
								+ "&MobileNo=91" +	phoneNumber + "&SenderID=" + senderID + "&Message=" + URLEncoder.encode(message, "UTF-8") 
								+ "&ServiceName=" + serviceName).request().get();
						logger.info("Response for sms to " + phoneNumber + " : " + response.readEntity(String.class));
					}
					catch(Exception ex){
						logger.error("Error sending sms to : " + phoneNumber, ex);
					}
					finally{
						if(response != null){
							response.close();
						}
						if(client != null){
							client.close();
						}
					}
				}
			};
			Thread t = new Thread(r);
			t.start();
		}
	}
	
	public static void enableSMS(){
		enableSMS = true;  
	}
	
	public static void disableSMS(){
		enableSMS = false;
	}
	
	public static boolean getSMSServiceStatus(){
		return enableSMS;
	}
	
	public static void sendWelcomeSMS(String phoneNumber, String contactPhoneNumber, String username, String email, String password){
		String message = welcomeMessage.replaceFirst(PLACEHOLDER, username)
//				.replaceFirst(PLACEHOLDER, contactPhoneNumber)
				.replaceFirst(PLACEHOLDER, email)
				.replaceFirst(PLACEHOLDER, password);
		sendSMS(phoneNumber, message);
	}
	
	public static void sendWalletRechargeSMS(String phoneNumber, String walletBalance, String transactionId, String transactionAmount){
		String message = walletRechargeMessage.replaceFirst(PLACEHOLDER, walletBalance).replaceFirst(PLACEHOLDER, transactionId).replaceFirst(PLACEHOLDER, transactionAmount);
		sendSMS(phoneNumber, message);
	}
	
	public static void sendOrderConfirmationSMS(String phoneNumber, String pickupPersonName, String pickupTime, String numberOfItems,
			String dropTime, boolean clothesOnHanger, boolean expressDelivery){
		String message = orderConfirmationMessage.replaceFirst(PLACEHOLDER, pickupPersonName == null ? "" : pickupPersonName)
				.replaceFirst(PLACEHOLDER, pickupTime == null ? "" : pickupTime)
				.replaceFirst(PLACEHOLDER, numberOfItems == null ? "" : numberOfItems)
				.replaceFirst(PLACEHOLDER, dropTime == null ? "" : dropTime)
				.replaceFirst(PLACEHOLDER, clothesOnHanger ? "Clothes on Hanger - Yes" : "")
				.replaceFirst(PLACEHOLDER, expressDelivery ? "Express Delivery Order!" : "");
		
		sendSMS(phoneNumber, message);
	}
	
	public static void sendOrderRecievedSMS(String phoneNumber, String username, String orderNumber,
			String amount){
		String message = orderRecievedMessage.replaceFirst(PLACEHOLDER, username == null ? "" : username)
				.replaceFirst(PLACEHOLDER, orderNumber == null ? "" : orderNumber)
				.replaceFirst(PLACEHOLDER, amount == null ? "" : amount);
		sendSMS(phoneNumber, message);
	}
	
	public static void sendOrderCompletionSMS(String phoneNumber, String supportPhoneNumber, String amountDeducted, String walletBalance){
		if(amountDeducted != null){
			supportPhoneNumber += ". Rs. " + amountDeducted + " were deducted from your wallet. Your updated wallet balance is Rs. " + walletBalance;
		}
		String message = orderCompletionMessage.replaceFirst(PLACEHOLDER, supportPhoneNumber == null ? "" : supportPhoneNumber);
		sendSMS(phoneNumber, message);
	}
	
	public static void sendPickupOrderSMS(String phoneNumber, String orderNumber, String pickupTime,
			String flatNumber, String comments, String username, String userPhoneNumber){
		String message = pickupOrderMessage.replaceFirst(PLACEHOLDER, orderNumber == null ? "" : orderNumber)
				.replaceFirst(PLACEHOLDER, pickupTime == null ? "" : pickupTime)
				.replaceFirst(PLACEHOLDER, flatNumber == null ? "" : flatNumber)
				.replaceFirst(PLACEHOLDER, comments != null ? comments : "")
				.replaceFirst(PLACEHOLDER, username == null ? "" : username)
				.replaceFirst(PLACEHOLDER, userPhoneNumber == null ? "" : userPhoneNumber);
		sendSMS(phoneNumber, message);
	}
	
	public static void sendDropOrderSMS(String phoneNumber, String orderNumber, String dropTime,
			String flatNumber, String amount, String username, String userPhoneNumber){
		String message = dropOrderMessage.replaceFirst(PLACEHOLDER, orderNumber == null ? "" : orderNumber)
				.replaceFirst(PLACEHOLDER, dropTime == null ? "" : dropTime)
				.replaceFirst(PLACEHOLDER, flatNumber == null ? "" : flatNumber)
				.replaceFirst(PLACEHOLDER, amount == null ? "" : amount)
				.replaceFirst(PLACEHOLDER, username == null ? "" : username)
				.replaceFirst(PLACEHOLDER, userPhoneNumber == null ? "" : userPhoneNumber);
		sendSMS(phoneNumber, message);
	}
}

