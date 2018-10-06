package com.laundry.rest.service;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.dao.LaundryDAO;
import com.laundry.pojo.FAQ;
import com.laundry.pojo.Support;
 
@Path("/help")
public class HelpService {
	
	private static final Logger logger = LogManager.getLogger(HelpService.class);
	
	@GET
	@Path("/support")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportDetails() {
		logger.info("Request recieved for fetching supprt details");
		Support support = LaundryDAO.getInstance().getSupportDetails();
		return Response.status(Status.OK).entity(support).build();
	}
	
	@GET
	@Path("/faq")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFAQs() {
		logger.info("Request recieved for fetching FAQs");
		List<FAQ> faqs = LaundryDAO.getInstance().getFAQs();
		return Response.status(Status.OK).entity(faqs).build();
	}
}