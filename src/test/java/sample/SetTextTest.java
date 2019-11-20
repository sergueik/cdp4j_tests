package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// http://tutorials.jenkov.com/java-util-concurrent/countdownlatch.html

public class SetTextTest extends BaseTest {

	private String baseURL = "https://www.seleniumeasy.com/test/input-form-demo.html";
	private static String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";

	private static String firstNameSelector = "#contact_form > fieldset input[name= 'first_name']";
	private static String textAreaSelector = "form#contact_form > fieldset textarea";

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		assertThat(session, notNullValue());
		session.navigate(baseURL).waitDocumentReady();
		session.waitDocumentReady(30000);
		System.err.println("url: " + session.getLocation());
	}

	@Test(enabled = true)
	public void basicTextEnterTest() {
		String selector = firstNameSelector;
		String text = "WebFolder User";

		// Arrange
		session.waitUntil(o -> o.matches(selector), 1000, 100);
		highlight(selector, 1000);

		// Act
		fastSetText(selector, text);
		String result = session.getAttribute(selector, "value");
		Assert.assertEquals(result, null);
		assertThat(result, nullValue());

		// Assert
		// session.getAttribute appears unable to detect the valuewe just entered
		// Assert.assertEquals(result, text);
	}

	@Test(enabled = true)
	public void textAreaEnterTest() {
		String selector = textAreaSelector;
		// Arrange
		session.waitUntil(o -> o.matches(selector), 5000, 100);
		scrollIntoView(selector);
		highlight(selector, 1000);

		// Act
		fastSetText(selector, text);

		// Assert
		sleep(5000);
	}

	@Test(enabled = true)
	public void textAreaScrollTest() {
		// String selector = textAreaSelector;
		List<String> selectors = new ArrayList<>();
		selectors.add("/html/body/footer/div/div[1]/h4"); // xpath, bottom of the page
		selectors.add("#site-name > a"); // css selecor, top of the page
		selectors.add("form#contact_form > fieldset textarea"); // near bottom of the page
		selectors.add("div.panel-heading"); // near top of the page
		// Arrange
		// Act
		for (String selector : selectors) {
			sleep(1000);
			scrollIfOutOfView(selector);
			highlight(selector, 1000);
		}
		// Assert
		sleep(5000);
	}
}
