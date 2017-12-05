package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PageLoadTest extends BaseTest {

	private static String baseURL = "https://coderanch.com/forums";
	private static int pageLoadTimeout = 10000;
	private static boolean debug = false;

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		session.navigate(baseURL).waitDocumentReady(pageLoadTimeout);
		session.evaluate(getScriptContent("timing.js"));
	}

	@Test(enabled = true)
	public void timingTest() throws Exception {
		// TODO: process result in native performance.timing object format
		String result = session.callFunction("window.timing.getTimes",
				String.class);
		if (result == null) {
			throw new RuntimeException("result is null");
		}
		try {
			Map<String, Double> data = getTimingMap(result);
			Set<String> keys = data.keySet();
			for (String key : keys) {
				System.err.println(key + " = " + data.get(key));
			}
		} catch (Exception e) {
			throw new RuntimeException("result is not parseable: " + e.toString());
		}
	}

	@Test(enabled = true)
	public void networkTest() throws Exception {
		String result = session.callFunction("window.timing.getNetwork",
				String.class);
		if (result == null) {
			throw new RuntimeException("result is null");
		}
		try {
			Map<String, Double> data = getNetworkTimingMap(result);
			Set<String> keys = data.keySet();
			for (String key : keys) {
				System.err.println("key: " + getTruncated(key, 70));
				System.err.println("value: " + data.get(key));
			}
		} catch (Exception e) {
			throw new RuntimeException("result is not parseable: " + e.toString());
		}
	}

	private Map<String, Double> getTimingMap(String payload)
			throws JSONException {
		if (debug) {
			System.err.println("payload: " + payload);
		}
		Map<String, Double> result = new HashMap<>();
		// names of columns to collect
		List<String> pickColumns = new ArrayList<>(
				Arrays.asList(new String[] { "navigationStart", "unloadEventStart",
						"unloadEventEnd", "redirectStart", "redirectEnd", "fetchStart",
						"domainLookupStart", "domainLookupEnd", "connectStart",
						"connectEnd", "secureConnectionStart", "requestStart",
						"responseStart", "responseEnd", "domLoading", "domInteractive",
						"domContentLoadedEventStart", "domContentLoadedEventEnd",
						"domComplete", "loadEventStart", "loadEventEnd" }));
		Pattern columnSelectionattern = Pattern
				.compile(String.format("(?:%s)", String.join("|", pickColumns)));
		JSONObject jsonObj = new JSONObject(payload);
		assertThat(jsonObj, notNullValue());
		@SuppressWarnings("unchecked")
		Iterator<String> dataKeys = jsonObj.keys();
		while (dataKeys.hasNext()) {
			String dataKey = dataKeys.next();
			if (columnSelectionattern.matcher(dataKey).find()) {
				result.put(dataKey,
						Double.parseDouble(jsonObj.get(dataKey).toString()));
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

	private Map<String, Double> getNetworkTimingMap(String payload)
			throws JSONException {

		if (debug) {
			System.err.println("payload: " + payload);
		}
		List<Map<String, String>> result = new ArrayList<>();
		// select columns to collect
		Pattern columnSelectionattern = Pattern.compile("(?:name|duration)");
		// ignore page events
		List<String> events = new ArrayList<>(Arrays.asList(new String[] {
				"first-contentful-paint", "first-paint", "intentmedia.all.end",
				"intentmedia.all.start", "intentmedia.core.fetch.page.request",
				"intentmedia.core.fetch.page.response", "intentmedia.core.init.end",
				"intentmedia.core.init.start", "intentmedia.core.newPage.end",
				"intentmedia.core.newPage.start", "intentmedia.core.scriptLoader.end",
				"intentmedia.core.scriptLoader.start",
				"intentmedia.sca.fetch.config.request",
				"intentmedia.sca.fetch.config.response" }));
		Pattern nameSelectionPattern = Pattern
				.compile(String.format("(?:%s)", String.join("|", events)));
		JSONArray jsonData = new JSONArray(payload);
		for (int row = 0; row < jsonData.length(); row++) {
			JSONObject jsonObj = new JSONObject(jsonData.get(row).toString());
			// assertThat(jsonObj, notNullValue());
			@SuppressWarnings("unchecked")
			Iterator<String> dataKeys = jsonObj.keys();
			Map<String, String> dataRow = new HashMap<>();
			while (dataKeys.hasNext()) {
				String dataKey = dataKeys.next();
				if (columnSelectionattern.matcher(dataKey).find()) {
					dataRow.put(dataKey, jsonObj.get(dataKey).toString());
				}
			}
			// only collect page elements, skip events
			if (!nameSelectionPattern.matcher(dataRow.get("name")).find()) {
				result.add(dataRow);
			}
		}
		assertTrue(result.size() > 0);
		if (debug) {
			System.err.println(String.format("Added %d rows", result.size()));
		}
		if (debug) {
			for (Map<String, String> resultRow : result) {
				Set<String> dataKeys = resultRow.keySet();
				for (String dataKey : dataKeys) {
					System.err.println(dataKey + " = " + resultRow.get(dataKey));
				}
			}
		}
		Map<String, Double> pageObjectTimers = new HashMap<>();

		for (Map<String, String> row : result) {
			try {
				pageObjectTimers.put(row.get("name"),
						Double.parseDouble(row.get("duration")) / 1000.0);
			} catch (NumberFormatException e) {
				pageObjectTimers.put(row.get("name"), 0.0);
			}
		}

		if (debug) {
			Set<String> names = pageObjectTimers.keySet();
			for (String name : names) {
				System.err.println(name + " = " + pageObjectTimers.get(name));
			}
		}
		return pageObjectTimers;
	}

	private static String getTruncated(String input, int length) {
		return (input.length() > length) ? input.substring(0, length - 10) + "..."
				+ input.substring(input.length() - 11) : input;
	}
}
