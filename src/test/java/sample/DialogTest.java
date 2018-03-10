package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// import org.testng.Assert;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Page;
import io.webfolder.cdp.exception.CommandException;
import io.webfolder.cdp.exception.ElementNotFoundException;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.type.runtime.CallFunctionOnResult;
import io.webfolder.cdp.type.runtime.RemoteObject;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class DialogTest extends BaseTest {

	int waitTimeout = 5000;
	int pollingInterval = 500;
	final String baseURL = "http://www.seleniumeasy.com/test/javascript-alert-box-demo.html";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(session, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod() {
		// navigate to Alert test page
		System.err.println("Navigate to URL: " + baseURL);
		session.navigate(baseURL);

		// Wait for page url to change
		Predicate<Session> urlChange = session -> session.getLocation()
				.matches(String.format("^%s.*", baseURL));
		session.waitUntil(urlChange, 1000, 100);
	}

	// @Ignore
	@Test
	public void alertTest() throws InterruptedException, IOException {
		// Arrange
		// wait until button is visible
		String buttonSelector = String.format(
				"//*[@id='easycont']//div[@class='panel-heading'][contains(normalize-space(.), '%s')]/..//button[contains(normalize-space(.), '%s')]",
				"Java Script Alert Box", "Click me!");
		List<String> results = new ArrayList<>();
		// NOTE: waitUntil is not generic
		session.waitUntil(s -> {
			System.err.println("Locating " + buttonSelector);
			List<String> elementIds = s.getObjectIds(buttonSelector);
			System.err.println("Located: " + elementIds.size() + " elements.");
			String elementText = session.getText(buttonSelector);
			System.err.println("in filter: Text = " + elementText);
			String cssSelector = super.cssSelectorOfElement(buttonSelector);
			System.err.println("Acting on: " + cssSelector + " => "
					+ session.getOuterHtml(cssSelector));
			results.add(cssSelector);
			return (Boolean) (elementIds.size() > 0);
		}, waitTimeout, pollingInterval);
		assertThat(results.size(), is(equalTo(1)));

		// prepare to handle alert
		Page page = session.getCommand().getPage();

		// Act
		highlight(buttonSelector, 1000);
		// click on button
		try {
			session.focus(buttonSelector);
			session.click(buttonSelector);
		} catch (ElementNotFoundException e) {
			System.err.println("Exception (ignored) :" + e.toString());
		}
		super.click(buttonSelector);
		sleep(1000);
		try {
			page.handleJavaScriptDialog(true);
		} catch (CommandException e) {
			System.err.println("Exception (ignored) :" + e.toString());
			// No dialog is showing
			// ignore
		}
	}
}
