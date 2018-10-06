package com.laundry.orders;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.laundry.dao.AddressDAO;
import com.laundry.dao.OrderDAO;
import com.laundry.dao.UserDAO;
import com.laundry.pojo.Address;
import com.laundry.pojo.Order;
import com.laundry.pojo.Society;
import com.laundry.pojo.User;

public class Util {
	
	private static final Logger logger = LogManager.getLogger(Util.class);
	public static DateFormat H_MM_A = new SimpleDateFormat("h:mm a");
	public static DateFormat DD_MM_YYYY = new SimpleDateFormat("dd-MM-yyyy");
	
	private static Properties applicationProperties = null;
	private static String environment = null;
	
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		H_MM_A.setTimeZone(TimeZone.getTimeZone("IST"));
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		String excelPath = getApplicationProperty("com.laundry.orders.file");
		Long societyId = Long.parseLong(getApplicationProperty("com.laundry.society.id"));
		Society society = new Society();
		society.setSocietyId(societyId);
		try(Workbook workbook = new XSSFWorkbook(excelPath)){
			Sheet sheet = workbook.getSheetAt(0);
			int lastRow = sheet.getLastRowNum();
			int firstRow = sheet.getFirstRowNum();
			
			for(int i = firstRow + 1; i <= lastRow; i++){
				Row row = sheet.getRow(i);
				Order order = new Order();
				String wsOrderId = row.getCell(1).getStringCellValue();
				order.setComments("WS Order Id - " + wsOrderId);
				String flatNumber = row.getCell(2).getStringCellValue();
				List<Address> addresses = AddressDAO.getInstance().getAddressesForFlatNumber(societyId, flatNumber);
				Address address = null;
				if(addresses != null && !addresses.isEmpty()){
					address = addresses.get(0);
				}
				else{
					address = new Address();
					address.setFlatNumber(flatNumber);
					address.setSociety(society);
					address.setLabel("Home");
					
					User user = new User();
					user.setFirstName("WS Order Id");
					user.setLastName(wsOrderId);
					user.setDefaultAddress(address);
					UserDAO.getInstance().addUser(user);
				}
				
				order.setAddress(address);
				Date dropDate = row.getCell(3).getDateCellValue();
				order.setDropTime(dropDate);
				order.setNumberOfItems(Long.parseLong(row.getCell(7).getStringCellValue()));
				order.setBillAmount(new BigDecimal(row.getCell(8).getNumericCellValue()));
	//			List<OrderItem> items = new LinkedList<>();
	//			Double tshirts = row.getCell(11).getNumericCellValue();
	//			if(tshirts > 0){
	//				OrderItem tshirtItem = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				tshirtItem.setQuantity(tshirts.intValue());
	//				items.add(tshirtItem);
	//			}
	//			
	//			Double shirts = row.getCell(12).getNumericCellValue();
	//			if(shirts > 0){
	//				OrderItem shirtItem = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				shirtItem.setQuantity(shirts.intValue());
	//				items.add(shirtItem);
	//			}
	//			
	//			Double trousers = row.getCell(13).getNumericCellValue();
	//			if(trousers > 0){
	//				OrderItem trouserItem = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				trouserItem.setQuantity(trousers.intValue());
	//				items.add(trouserItem);
	//			}
	//			
	//			Double womensKurtas = row.getCell(14).getNumericCellValue();
	//			if(womensKurtas > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(womensKurtas.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double leggings = row.getCell(15).getNumericCellValue();
	//			if(leggings > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(leggings.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double sarees = row.getCell(16).getNumericCellValue();
	//			if(sarees > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(sarees.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double blouses = row.getCell(17).getNumericCellValue();
	//			if(blouses > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(blouses.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double tops = row.getCell(18).getNumericCellValue();
	//			if(tops > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(tops.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double jeans = row.getCell(19).getNumericCellValue();
	//			if(jeans > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(jeans.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double patiyalas = row.getCell(20).getNumericCellValue();
	//			if(patiyalas > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(patiyalas.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double designerKurtas = row.getCell(21).getNumericCellValue();
	//			if(designerKurtas > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(designerKurtas.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double dupattas = row.getCell(22).getNumericCellValue();
	//			if(dupattas > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(dupattas.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double skirts = row.getCell(23).getNumericCellValue();
	//			if(skirts > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(skirts.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double shorts = row.getCell(24).getNumericCellValue();
	//			if(shorts > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(shorts.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double kids = row.getCell(25).getNumericCellValue();
	//			if(kids > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(kids.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double sweaters = row.getCell(26).getNumericCellValue();
	//			if(sweaters > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(sweaters.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double silkShirts = row.getCell(27).getNumericCellValue();
	//			if(silkShirts > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(silkShirts.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double dresses = row.getCell(28).getNumericCellValue();
	//			if(dresses > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(dresses.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double blazers = row.getCell(29).getNumericCellValue();
	//			if(blazers > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(blazers.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double jakets = row.getCell(30).getNumericCellValue();
	//			if(jakets > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(jakets.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double mensKurtas = row.getCell(31).getNumericCellValue();
	//			if(mensKurtas > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(mensKurtas.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double vests = row.getCell(32).getNumericCellValue();
	//			if(vests > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(vests.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double silkSarees = row.getCell(33).getNumericCellValue();
	//			if(silkSarees > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(silkSarees.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double ghaghras = row.getCell(34).getNumericCellValue();
	//			if(ghaghras > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(ghaghras.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double bedsheets = row.getCell(35).getNumericCellValue();
	//			if(bedsheets > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(bedsheets.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double curtains = row.getCell(36).getNumericCellValue();
	//			if(curtains > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(curtains.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double pillowCovers = row.getCell(37).getNumericCellValue();
	//			if(pillowCovers > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(pillowCovers.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double designerLehenge = row.getCell(38).getNumericCellValue();
	//			if(designerLehenge > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(designerLehenge.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double designerBlouse = row.getCell(39).getNumericCellValue();
	//			if(designerBlouse > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(designerBlouse.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double designerDupatta = row.getCell(40).getNumericCellValue();
	//			if(designerDupatta > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(designerDupatta.intValue());
	//				items.add(item);
	//			}
	//			
	//			Double superDesignerKurta = row.getCell(41).getNumericCellValue();
	//			if(superDesignerKurta > 0){
	//				OrderItem item = CategoryDAO.getInstance().getOrderItem(societyId, 2);
	//				item.setQuantity(superDesignerKurta.intValue());
	//				items.add(item);
	//			}
				
				OrderDAO.getInstance().addOrder(order);
				logger.info("Order created for workshop id - " + order.getComments() + " - with order id - " + order.getOrderId());
			}
		}
		
	}
	
	public static String getApplicationProperty(String key) {
		InputStream is = Util.class.getClassLoader().getResourceAsStream("com/laundry/config/application.properties");
		if(applicationProperties == null){
			applicationProperties = new Properties();
			try {
				if(is != null){
					applicationProperties.load(is);
				}
				else{
					logger.error("Unable to load application.properties");
				}
			} catch (IOException e) {
				logger.fatal("Error loading application properties", e);
			}
		}
		if(environment == null){
			environment = applicationProperties.getProperty("environment");
		}
		String value = null;
		if(environment != null && applicationProperties.containsKey(key + "." + environment)){
			value = applicationProperties.getProperty(key + "." + environment);
		}
		else if(applicationProperties.containsKey(key)){
			value = applicationProperties.getProperty(key);
		}
		return value;
	}
	
	
	public static List<Integer> toIntegerList(String string){
		List<Integer> integerList = new ArrayList<>();
		if(string!=null){
			String[] strings = string.split(",");
			for(String s : strings){
				integerList.add(Integer.parseInt(s.trim()));
			}
		}
		return integerList;
	}

	public static byte[] toBytes(String s) throws UnsupportedEncodingException{
		byte[] bytes = null;
		if(s != null){
			bytes = s.getBytes("UTF-8");
		}
		return bytes;
	}
}
