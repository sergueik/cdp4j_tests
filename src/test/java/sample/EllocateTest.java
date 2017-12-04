
package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class EllocateTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static String baseURL = "https://jqueryui.com/demos/";
	private static int pageLoadTimeout = 10000;
	private static String selector = "#logo-events > h2 > a";
	private static boolean debug = false;

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			System.err.println(String.format("Error in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		session.navigate("about:blank");
		super.afterMethod();
	}

	@Test(enabled = true)
	public void ellocateTest() throws Exception {
		// Arrange
		session.navigate(baseURL);
		sleep(5000);
		session.waitUntil(o -> o.matches(selector), pageLoadTimeout, 100);
		// session.waitDocumentReady(pageLoadTimeout);
		highlight(selector, 1000);

		assertThat(session.getObjectId(selector), notNullValue());

		session.evaluate(getScriptContent("ellocate.js"));
		session.evaluate(
				"var locators = function(selector) { return JSON.stringify(jQuery(selector).ellocate()); }");

		String result = session.callFunction("locators", String.class, selector);
		if (result != null) {
			if (debug) {
				System.err.println("result : " + result);
			}
			Map<String, String> data = CreateDataFromJSON(result);
			for (String key : data.keySet()) {
				System.err.println(key + ": " + data.get(key));
			}
		} else {
			throw new RuntimeException("result is null");

		}
	}

	private Map<String, String> CreateDataFromJSON(String payload)
			throws JSONException {

		if (debug) {
			System.err.println("payload: " + payload);
		}
		Map<String, String> result = new HashMap<>();
		// select columns to collect
		Pattern columnSelectionattern = Pattern.compile("(?:xpath|css)");
		JSONObject jsonObj = new JSONObject(payload);
		assertThat(jsonObj, notNullValue());
		@SuppressWarnings("unchecked")
		Iterator<String> dataKeys = jsonObj.keys();
		while (dataKeys.hasNext()) {
			String dataKey = dataKeys.next();
			if (columnSelectionattern.matcher(dataKey).find()) {
				result.put(dataKey, jsonObj.get(dataKey).toString());
			}
		}
		if (debug) {
			Set<String> keys = result.keySet();
			for (String key : keys) {
				System.err.println(key + " = " + result.get(key));
			}
		}
		return result;
	}
}
