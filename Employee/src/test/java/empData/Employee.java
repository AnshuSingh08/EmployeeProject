package empData;

import static io.restassured.RestAssured.given;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import junit.framework.Assert;

public class Employee {

	private Response res;
	private String company = "";
	List<String> inputData= new ArrayList<String>();
	

	@BeforeTest
	public void getData() throws IOException {
		Properties prop = new Properties();
		try {
			FileReader reader = new FileReader("src\\main\\java\\resources\\data.properties");
			prop.load(reader);
			inputData.add(prop.getProperty("Status"));
			inputData.add(prop.getProperty("Age"));
			inputData.add(prop.getProperty("Role"));
			inputData.add(prop.getProperty("Dob"));
			inputData.add(prop.getProperty("Message"));
			inputData.add(prop.getProperty("Company"));
			System.out.println("Data read from the file successfully:"+inputData.toString());
			
		} catch (FileNotFoundException e) {
			Assert.fail("Exception in reading file:" + e.getMessage());
		}
	}

	@Test(priority = 1)
	public void statusValidation() {
		RestAssured.baseURI = "http://demo4032024.mockable.io";
		int responseStatus = 0;

		try {
			int StatusFromPropFile= Integer.valueOf(inputData.get(0));
			
			res = given().when().get("/apitest");
			responseStatus = res.getStatusCode();
			Assert.assertEquals(StatusFromPropFile, responseStatus);
			System.out.println("Status code validated successfully:" + responseStatus);
		}

		catch (Exception e) {
			Assert.fail("Exception in calling service:" + e.getMessage());
		}

	}

	@Test(priority = 2)
	public void headerValidation() {
		
		
		// validate headers
		String cont = String.valueOf(res.getHeader("Content-Type"));
		Assert.assertEquals(cont, "application/json; charset=UTF-8");
		System.out.println("Content type validated as :" + cont);

		String proxyCon = String.valueOf(res.getHeader("Proxy-Connection"));
		Assert.assertEquals(proxyCon, "Keep-Alive");
		System.out.println("Proxy connection validated as :" + proxyCon);

		String ContEnc = String.valueOf(res.getHeader("Content-Encoding"));
		Assert.assertEquals(ContEnc, "gzip");
		System.out.println("Content-Encoding validated as :" + ContEnc);

		String TransEnc = String.valueOf(res.getHeader("Transfer-Encoding"));
		Assert.assertEquals(TransEnc, "chunked");
		System.out.println("Transfer-Encoding validated as :" + TransEnc);

		String Vary = String.valueOf(res.getHeader("Vary"));
		Assert.assertEquals(Vary, "Accept-Encoding");
		System.out.println("Vary validated as :" + Vary);

	}

	@Test(priority = 3)
	public void responseBodyValidation() {
		int statusres = 0;
		String messageres = "", roleRes = "", dobString = "";
		JSONArray empData;
		int ageRes = 0;

		try {
			//values from file
			int statusFromPropFile= Integer.valueOf(inputData.get(0));
			int ageFromPropFile= Integer.valueOf(inputData.get(1));
			String roleFromPropFile= inputData.get(2);
			String dobFromPropFile= inputData.get(3);
			String msgFromPropFile= inputData.get(4);
			
			
			//Values from response
			JSONObject jsonRes = new JSONObject(res.asString());
			statusres = jsonRes.getInt("status");
			messageres = jsonRes.getString("message");
			empData = jsonRes.getJSONArray("employeeData");

			Assert.assertEquals(statusFromPropFile, statusres);
			System.out.println("status validated as:" + statusres);

			Assert.assertEquals(msgFromPropFile, messageres);
			System.out.println("message validated as:" + messageres);

			company = empData.getJSONObject(0).getString("company");
			
			ageRes = empData.getJSONObject(0).getInt("age");
			Assert.assertEquals(ageFromPropFile, ageRes);
			System.out.println("age validated as:" + ageRes);

			roleRes = empData.getJSONObject(0).getString("role");
			Assert.assertEquals(roleFromPropFile, roleRes);
			System.out.println("role validated as:" + roleRes);

			dobString = empData.getJSONObject(0).getString("dob");
			Assert.assertEquals(dobFromPropFile, dobString);
			System.out.println("dob validated as:" + dobString);

		} catch (JSONException e) {
			Assert.fail("Exception in reading response body:" + e.getMessage());
		}

	}

	@Test(priority = 4)
	public void companyValidation() {

		try {
			String cmpnyFromPropFile= inputData.get(5);
			System.out.println("company input from prop file:"+cmpnyFromPropFile);
			System.out.println("company name from response:"+company);
			Assert.assertEquals(cmpnyFromPropFile, company);
		}

		catch (Exception e) {
			Assert.fail("Exception in getting company name:" + e.getMessage());
		}
	}
}
