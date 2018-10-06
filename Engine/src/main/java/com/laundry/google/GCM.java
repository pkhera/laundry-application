package com.laundry.google;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCM {
	
	private static final Logger logger = LogManager.getLogger(GCM.class);

	private static final String SENDER_ID = "<GCM_API_KEY>";

	public static final String ACTION_STATUS_CHANGE = "STATUS_CHANGE";
	public static final String URL_STATUS_CHANGE = "http://www.laundry.com/order";
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GCM() {

	}

	public static String sendMessage(String deviceToken, String collapseKey, String userMessage, String action, String url,
			Long orderId) {

		logger.info("Sending android notification to : " + deviceToken +  " , for order : " + orderId + " , with message : " + userMessage);
		
		// Instance of com.android.gcm.server.Sender, that does the
		// transmission of a Message to the Google Cloud Messaging service.
		Sender sender = new Sender(SENDER_ID);

		// This Message object will hold the data that is being transmitted
		// to the Android client devices. For this demo, it is a simple text
		// string, but could certainly be a JSON object.
		Message message = new Message.Builder()

				// If multiple messages are sent using the same .collapseKey()
				// the android target device, if it was offline during earlier
				// message
				// transmissions, will only receive the latest message for that
				// key when
				// it goes back on-line.
				.collapseKey(collapseKey).timeToLive(30).delayWhileIdle(true)
				.addData("message", userMessage).addData("action", action).addData("url", url).addData("orderId", orderId.toString()).build();
		System.out.println(message);
		
		try {
			// use this for multicast messages. The second parameter
			// of sender.send() will need to be an array of register ids.
			Result result = sender.send(message, deviceToken, 3);
			System.out.println(result);
			if(result!=null){
				if (result.getMessageId() != null) {
					String canonicalRegId = result.getCanonicalRegistrationId();
					if (canonicalRegId != null) {
						System.out.println("Update Device Token - " + canonicalRegId);
						deviceToken = canonicalRegId;
					}
				} else {
					String error = result.getErrorCodeName();
					System.out.println("Send failure: " + error);
					if(error.equals(Constants.ERROR_NOT_REGISTERED)){
						System.out.println("Device Token Unregistered - " + deviceToken);
						deviceToken = null;
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error sending notification", e);
		}
		return deviceToken;
	}
	
}
