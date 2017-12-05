package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import org.hamcrest.CoreMatchers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuvianTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static Pattern pattern;
	private static Matcher matcher;

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			System.err.println(String.format("Error in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		session.navigate("about:blank");
		super.afterMethod();
	}

	@Test(enabled = false)
	public void test1() {
		// Arrange
		session.navigate("http://suvian.in/selenium/1.1link.html");

		// Wait for intro message to show
		session.waitUntil(o -> o.matches(".container .row .intro-message h3 a"),
				1000, 100);
		String xpath = "//div[1]/div/div/div/div/h3[2]/a";
		List<String> elementIds = session.getObjectIds(xpath);
		// Act
		assertTrue(elementIds.size() > 0);
		// Assert
		assertTrue(
				((String) session.getPropertyByObjectId(elementIds.get(0), "innerHTML"))
						.equalsIgnoreCase("Click Here"));
		// Act
		// Act
		executeScript("function() { this.click(); }", xpath);
		// Assert
		// Wait for validation page to load
		assertTrue(session.waitUntil(
				o -> o.getLocation().matches(".*/1.1link_validate.html$"), 1000, 100));
		// Confirm the message is shown
		assertTrue(session.waitUntil(o -> o.getText(".intro-message")
				.contains("Link Successfully clicked")));
	}

	@Test(enabled = false)
	public void test5_2() {
		// Arrange
		session.navigate("http://suvian.in/selenium/1.5married_radio.html");

		session.waitUntil(o -> o.matches(".container .row .intro-message h3 a"),
				10000, 1000);
		String formLocator = ".intro-header .container .row .col-lg-12 .intro-message form";
		session.waitUntil(o -> o.matches(formLocator), 1000, 100);
		assertThat(session.getObjectId(formLocator), notNullValue());
		highlight(formLocator, 1000);
		sleep(1000);
		// get form element contents broken ?
		// https://developer.mozilla.org/en-US/docs/Web/API/Element/outerHTML
		String elementContents = session.getAttribute(formLocator, "outerHTML");
		System.err.println("Form element contents: " + elementContents);
		assertThat(elementContents, nullValue());

		elementContents = (String) executeScript(
				"function() { return this.outerHTML; }", formLocator);
		System.err.println("Form element contents: " + elementContents);

		// Parse the HTML looking for "yes" or "no" - depends on desired married
		// status
		String label = "no";

		String line = new ArrayList<String>(
				Arrays.asList(elementContents.split("<br/?>"))).stream()
						.filter(o -> o.toLowerCase().indexOf(label) > -1).findFirst().get();
		Matcher matcher = Pattern.compile("value=\\\"([^\"]*)\\\"").matcher(line);
		String checkboxValue = null;
		if (matcher.find()) {
			checkboxValue = matcher.group(1);
			System.err.println("checkbox value = " + checkboxValue);
		} else {
			System.err.println("checkbox value not found");
		}
		String checkBoxElementId = null;
		String checkBoxElementSelector = String.format(
				"%s input[name='married'][value='%s']", formLocator, checkboxValue);
		if (checkboxValue != null) {
			checkBoxElementId = session.getObjectId(checkBoxElementSelector);
		}
		// Act
		assertThat(checkBoxElementId, notNullValue());
		highlight(checkBoxElementSelector);
		click(checkBoxElementSelector);
		// Assert passes
		assertThat(
				session.getObjectId(
						String.format("%s%s", checkBoxElementSelector, ":checked")),
				notNullValue());
		sleep(500);
		// Assert fails
		assertTrue(Boolean.parseBoolean(
				session.getAttribute(checkBoxElementSelector, ":checked")));
		sleep(500);
	}

	@Test(enabled = true)
	public void test6_1() {
		// Arrange
		List<String> hobbies = new ArrayList<>(Arrays.asList("Singing", "Dancing"));
		session.navigate("http://suvian.in/selenium/1.6checkbox.html")
				.waitDocumentReady();

		session.waitUntil(_session -> {
			return (boolean) (_session
					.getObjectIds("div.container div.row div.intro-message h3").stream()
					.filter(
							_id -> ((String) _session.getPropertyByObjectId(_id, "innerHTML"))
									.toLowerCase().indexOf("select your hobbies") > -1)
					.findFirst().get() != null);

		});
		// css selector
		assertThat(session.getObjectId("input[id]"), notNullValue());
		// Act
		// convert to xpath
		String formElementXPath = String.format("%s/..",
				xpathOfElement("input[id]"));
		assertThat(session.getObjectId(formElementXPath), notNullValue());
		// convert to css selector
		String labelSelector = String.format("%s label[for]",
				cssSelectorOfElement(formElementXPath));

		Pattern pattern = Pattern.compile(".*for=\"(.+)\".*",
				Pattern.CASE_INSENSITIVE);
		String propertyName = "for";

		List<String> inputElementIDs = session.getObjectIds(labelSelector).stream()
				.filter(o -> {
					System.err.println(
							"text: " + session.getPropertyByObjectId(o, "innerHTML"));
					System.err.println(
							"HTML: " + session.getPropertyByObjectId(o, "outerHTML"));
					return (boolean) hobbies
							.contains(session.getPropertyByObjectId(o, "innerHTML"));
				}).collect(Collectors.toList());
		assertTrue(inputElementIDs.size() == hobbies.size());
		Map<String, String> inputMap = inputElementIDs.stream().filter(objectId -> {
			System.err.println("input element id: " + objectId);
			System.err.println("input element text: "
					+ session.getPropertyByObjectId(objectId, "innerHTML"));
			// NOTE: the "for" attribute is not getting returned in a
			// getPropertyByObjectId API
			String propertyValue1 = (String) session.getPropertyByObjectId(objectId,
					propertyName);
			System.err.println(String.format("input element \"%s\" attribute: \"%s\"",
					propertyName, propertyValue1));
			// cannot use objectId with execScript("function() { return
			// this.getAttribute('for')", <input>)
			String elementHML = (String) session.getPropertyByObjectId(objectId,
					"outerHTML");
			System.err.println("input element HTML: " + elementHML);
			String propertyValue2 = finAttributeValue(elementHML, propertyName);
			int inputIndex = Integer.parseInt(propertyValue2);
			assertTrue(inputIndex > 0);
			/*
			Matcher matcher = pattern.matcher(elementHML);
			int inputIndex = 0;
			assertTrue(matcher.find());
			inputIndex = Integer.parseInt(matcher.group(1).toString());
			assertTrue(inputIndex > 0);
			System.err.println(
					String.format("input element \"%s\" attribute (computed): \"%d\"",
							propertyName, inputIndex));
			*/
			return true;
		}).collect(Collectors.toMap(
				objectId -> (String) session.getPropertyByObjectId(objectId,
						"innerHTML"),
				objectId -> finAttributeValue(
						(String) session.getPropertyByObjectId(objectId, "outerHTML"),
						propertyName)));
		List<String> checkboxIDs = new ArrayList<>();
		for (String hobby : hobbies) {
			String inpuID = inputMap.get(hobby);
			System.err.println("finding: " + inpuID);
			checkboxIDs.add(session.getObjectId(String.format("input#%s", inpuID)));
		}

	}

	// TODO: port .net findMatch..
	String finAttributeValue(String elementHML, String elementAttribute) {
		String result = null;
		Pattern pattern = Pattern.compile(
				String.format(".*%s=\"(.+)\".*", elementAttribute),
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(elementHML);
		assertTrue(matcher.find());
		result = matcher.group(1).toString();
		return result;

	}
}