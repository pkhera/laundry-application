package com.laundry.rest.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
@Provider
public class ResponseCorsFilter implements ContainerResponseFilter {
	
	private static final Logger logger = LogManager.getLogger(ResponseCorsFilter.class);
 
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext contResp) {
 
//        ResponseBuilder resp = Response.fromResponse(contResp.getResponse());
        contResp.getHeaders().add("Access-Control-Allow-Origin", "*");
        contResp.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
 
        String reqHead = req.getHeaderString("Access-Control-Request-Headers");
 
        if(null != reqHead && !reqHead.equals("")){
        	contResp.getHeaders().add("Access-Control-Allow-Headers", reqHead);
        }
 
//        contResp.setResponse(resp.build());
//            return contResp;
    }
 
}
