package com.laundry.rest.service;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.dao.SocietyDAO;
import com.laundry.dao.UserDAO;
import com.laundry.pojo.Address;
import com.laundry.pojo.ResponseMessage;
import com.laundry.pojo.User;
import com.laundry.util.Util;
 
@Path("/address")
public class AddressService {
	
	private static final Logger logger = LogManager.getLogger(AddressService.class);
	
	/**
	 * 
	 * @api {post} /address Add Address
	 * @apiName Add Address
	 * @apiGroup Address
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {Number} userId  User ID for which to add the address.
	 * @apiParam {Number} society.societyId  Society Id.
	 * @apiParam {String} flatNumber  Flat Number.
	 * @apiParam {String} label  Address Label.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 * @apiSuccess {Number} address.addressId  Address Id.
	 * @apiSuccess {Number} address.label  Address Label .
	 * @apiSuccess {Number} address.userId  User Id.
	 * @apiSuccess {String} address.flatNumber  Flat Number.
	 * @apiSuccess {String} address.society.societyId  Society Id.
	 * @apiSuccess {String} address.society.name  Society Name.
	 * @apiSuccess {String} address.society.landmark  Society's Landmark.
	 * @apiSuccess {String} address.society.city  Society's City.
	 * @apiSuccess {String} address.society.state Society's State.
	 * @apiSuccess {String} address.society.country Society's Country.
	 * @apiSuccess {String} address.society.pincode  Society's Pincode.
	 * @apiSuccess {String} address.society.active  Is Society Active.
	 *
	 * @apiExample Sample Request Payload
	 * 	{
   	 *		"userId": "37",
   	 *		"society": {
     * 			"societyId": "1"
   	 *		},
   	 *		"flatNumber": "C-123",
   	 *		"label": "Home"
	 *	}
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *	   "address": {
	 *	      "addressId": 74,
	 *	      "userId": 37,
	 *	      "flatNumber": "C-123",
	 *	      "society": {
	 *	         "societyId": 1,
	 *	         "name": "Society 1",
	 *	         "landmark": "Whitefield",
	 *	         "city": "Bangalore",
	 *	         "pincode": "560005",
	 *	         "state": "Karnataka",
	 *	         "country": "India",
	 *	         "active": true
	 *	      },
	 *	      "label": "Home"
	 *	   },
	 *	   "message": "Address added successfully."
	 *	}
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "Please provide a label for the address"
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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(@HeaderParam("authorization") String authString, Address address) {
		logger.info("Request recieved for adding address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null && user.getUserId().equals(address.getUserId())){
			ResponseMessage rm = address.validate();
			if(rm == null){
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
	
	/**
	 * 
	 * @api {put} /address Update Address
	 * @apiName Update Address
	 * @apiGroup Address
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {Number} addressId  Address ID to update.
	 * @apiParam {Number} userId  User ID for which to update the address.
	 * @apiParam {Number} society.societyId  Society Id.
	 * @apiParam {String} flatNumber  Flat Number.
	 * @apiParam {String} label  Address Label.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 * @apiSuccess {Number} address.addressId  Address Id.
	 * @apiSuccess {Number} address.label  Address Label .
	 * @apiSuccess {Number} address.userId  User Id.
	 * @apiSuccess {String} address.flatNumber  Flat Number.
	 * @apiSuccess {String} address.society.societyId  Society Id.
	 * @apiSuccess {String} address.society.name  Society Name.
	 * @apiSuccess {String} address.society.landmark  Society's Landmark.
	 * @apiSuccess {String} address.society.city  Society's City.
	 * @apiSuccess {String} address.society.state Society's State.
	 * @apiSuccess {String} address.society.country Society's Country.
	 * @apiSuccess {String} address.society.pincode  Society's Pincode.
	 * @apiSuccess {String} address.society.active  Is Society Active.
	 *
	 * @apiExample Sample Request Payload
	 * 	{
	 * 		"addressId": "19",
   	 *		"userId": "37",
   	 *		"society": {
     * 			"societyId": "1"
   	 *		},
   	 *		"flatNumber": "C-123",
   	 *		"label": "Home"
	 *	}
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *	   "address": {
	 *	      "addressId": 74,
	 *	      "userId": 37,
	 *	      "flatNumber": "C-123",
	 *	      "society": {
	 *	         "societyId": 1,
	 *	         "name": "Society 1",
	 *	         "landmark": "Whitefield",
	 *	         "city": "Bangalore",
	 *	         "pincode": "560005",
	 *	         "state": "Karnataka",
	 *	         "country": "India",
	 *	         "active": true
	 *	      },
	 *	      "label": "Home"
	 *	   },
	 *	   "message": "Address added successfully."
	 *	}
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "Error updating address."
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
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("authorization") String authString, Address address) {
		logger.info("Request recieved for updating address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null && user.getUserId().equals(address.getUserId())){
			ResponseMessage rm = address.validate();
			if(rm == null){
				UserDAO dao = UserDAO.getInstance();
				boolean addressAdded = dao.updateAddress(address);
				if(addressAdded){
					response.put("message", "Address updated successfully.");
					status = Status.OK;
					response.put("address", address);
				}
				else{
					response.put("message", "Error updating address.");
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
	
	/**
	 * 
	 * @api {delete} /address/:addressId Delete Address
	 * @apiName Delete Address
	 * @apiGroup Address
	 *
	 * @apiHeader {String} authorization HTTP Basic Authorization value.
	 * 
	 * @apiParam {Path} addressId  Address ID to delete.
	 *
	 * @apiSuccess {String} message  Response message if any.
	 * 
	 * @apiSampleRequest /address/74
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * {
	 *	   "message": "Address deleted successfully."
	 *	}
	 *    
	 * @apiErrorExample Error-Response:
	 * HTTP/1.1 400 Bad Request
	 * {
	 *       "message": "At least one address should be present."
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
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{addressId}")
	public Response delete(@HeaderParam("authorization") String authString, @PathParam("addressId") Long addressId) {
		logger.info("Request recieved for deleting address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null && user.hasAddress(addressId)){
			if(user.getDefaultAddress().getAddressId().equals(addressId)){
				status = Status.BAD_REQUEST;
				response.put("message", "Cannot delete the default address.");
			}
			else{
				if(user.getAddresses().size() > 1){
					UserDAO dao = UserDAO.getInstance();
					boolean hasPendingOrders = dao.hasPendingOrdersForAddress(addressId);
					if(!hasPendingOrders){
						boolean addressDeleted = dao.deleteAddress(addressId);
						if(addressDeleted){
							response.put("message", "Address deleted successfully.");
							status = Status.OK;
						}
						else{
							response.put("message", "Error deleting address.");
							status = Status.INTERNAL_SERVER_ERROR;
						}
					}
					else{
						response.put("message", "Cannot delete because there are pending orders for this address.");
						status = Status.INTERNAL_SERVER_ERROR;
					}
				}
				else{
					status = Status.BAD_REQUEST;
					response.put("message", "At least one address should be present.");
				}
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	/**
	 * @api {get} /address/society Get Societies
	 * @apiName Get Societies
	 * @apiGroup Address
	 *
	 * 
	 * @apiSuccess {String} societyId  Society Id.
	 * @apiSuccess {String} name  Society email.
	 * @apiSuccess {String} landmark  Landmark.
	 * @apiSuccess {String} city  City.
	 * @apiSuccess {String} pincode  Pincode.
	 * @apiSuccess {String} state  State.
	 * @apiSuccess {String} country Country.
	 * 
	 *
	 * @apiSuccessExample Success-Response:
	 * HTTP/1.1 200 OK
	 * [
     *	{
     *	   "societyId": 1,
     *	   "name": "Society 1",
     *	   "landmark": "Whitefield",
     *	   "city": "Bangalore",
     *	   "pincode": "560005",
     *	   "state": "Karnataka",
     *	   "country": "India"
     *	},
     *	{
     *	   "societyId": 2,
     *	   "name": "Sardar Center",
     *	   "landmark": "Vastrapur",
     *	   "city": "Ahmedabad",
     *	   "pincode": "380004",
     *	   "state": "Gujarat",
     *	   "country": "India"
     *	}
     * ]
	 *    
	 * 
	 * @return
	 */
	@GET
	@Path("/society")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSocieties() {
		Object response = null;
		Status status = null;
		logger.info("Fetching society list.");
		response = SocietyDAO.getInstance().getSocieties();
		logger.info("Society list retrieved.");
		status = Status.OK;
		return Response.status(status).entity(response).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{addressId}/default")
	public Response setDefault(@HeaderParam("authorization") String authString, @PathParam("addressId") Long addressId) {
		logger.info("Request recieved for setting default address");
		Map<String, Object> response = new HashMap<>();
		Status status = null;
		User user = Util.getUser(authString);
		if(user != null && user.hasAddress(addressId)){
			if(user.getDefaultAddress().getAddressId().equals(addressId)){
				status = Status.BAD_REQUEST;
				response.put("message", "The address is already set to default.");
			}
			else{
				UserDAO dao = UserDAO.getInstance();
				boolean defaultAddressChanged = dao.setDefaultAddress(user.getUserId(),addressId);
				if(defaultAddressChanged){
						response.put("message", "Default address changed successfully.");
						status = Status.OK;
				}
				else{
					response.put("message", "Error changing default address");
					status = Status.INTERNAL_SERVER_ERROR;
				}
			}
		}
		else{
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
}