package com.laundry.rest.service.admin;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.laundry.dao.TableMetadataDAO;
import com.laundry.pojo.AdminUser;
import com.laundry.pojo.ResponseMessage;
import com.laundry.util.Util;
 
@Path("/admin/table")
public class TableMetadataService {
	
	private static final Logger logger = LogManager.getLogger(TableMetadataService.class);
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTables(@HeaderParam("authorization") String authString) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			response = TableMetadataDAO.getInstance().getTables();
			status = Status.OK;
		}
		else{
			response = new ResponseMessage("You are unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).entity(response).build();
	}
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTableData(@QueryParam("authorization") String authString, @QueryParam("tableName") String tableName) {
		Object response = null;
		Status status = null;
		AdminUser user = Util.getAdminUser(authString);
		if( user != null){
			java.nio.file.Path p;
			Writer writer = null;
			try {
				status = Status.OK;
				StreamingOutput so = new StreamingOutput() {
					@Override
					public void write(OutputStream output) throws IOException,
							WebApplicationException {
						TableMetadataDAO.getInstance().writeTableData(tableName, output);
					}
				};
				return Response.status(status)
				.header("Content-Disposition","attachment; filename=\"" + tableName + ".csv\"")
				.header("x-filename", tableName + ".csv")
				.type("application/csv")
				.entity(so).build();
			}
			finally{
				if(writer != null){
					try {
						writer.close();
					} catch (IOException e) {
						logger.warn("Error closing temp file.", e);
					}
				}
			}
		}
		else{
			response = new ResponseMessage("You are unauthorized for this operation.");
			status = Status.UNAUTHORIZED;
		}
		return Response.status(status).build();
	}
	
}