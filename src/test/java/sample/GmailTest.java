package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.exception.CommandException;
import io.webfolder.cdp.exception.ElementNotFoundException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class GmailTest extends BaseTest {

	private String signOutButton = String.format(
			"//a[contains(text(), '%s')][contains(@href, '%s')]", "Sign out",
			"https://mail.google.com/mail/logout");
	private String signOutButtonFallback = "#gb_71";
	private String baseURL = "https://www.google.com/gmail/about/#";
	private String accountsURL = "https://accounts.google.com/signin/v2/identifier\\?continue=";
	private String signingURL = "https://accounts.google.com/signin/";

	// Alternatives (do not work)
	// "//*[contains(text(),'Sign in')]"; // xpath
	// "body > nav > div > a.gmail-nav__nav-link.gmail-nav__nav-link__sign-in"
	private String signInLink = "a[class *= 'gmail-nav__nav-link__sign-in']"; // css

	private String identifier = "#identifierId"; // css
	private String identifierNextButton = "//*[@id='identifierNext']/content/span[contains(text(),'Next')]"; // xpath
	private String passwordInput = "#password input"; // css
	private String passwordNextButton = "//*[@id='passwordNext']/content/span[contains(text(),'Next')]"; // xpath
	private String profileImage = "//a[contains(@href,'https://accounts.google.com/SignOutOptions')]"; // xpath

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(session, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to URL: " + baseURL);
		session.navigate(baseURL);

		// Wait for page url to change

		Predicate<Session> urlChange = session -> session.getLocation()
				.matches(String.format("^%s.*", baseURL));
		session.waitUntil(urlChange, 1000, 100);
		session.waitUntil(new Predicate<Session>() {
			public boolean test(Session session) {
				return session.getLocation().matches(String.format("^%s.*", baseURL));
			}
		}, 1000, 100);

	}

	@Test(priority = 1, enabled = false)
	public void invalidUsernameTest() throws InterruptedException, IOException {

		// Arrange
		// Sign in
		highlight(signInLink);
		click(signInLink);
		// NOTE: session.click does not work
		// session.click(signInLink);

		Predicate<Session> urlChange = session -> session.getLocation()
				.matches(String.format("^(?:%s|%s).*", accountsURL, signingURL));
		session.waitUntil(urlChange, 1000, 100);

		assertTrue(
				// String.format("Unexpected title '%s'", row.getAttribute("role")),
				session.getLocation().matches(String.format("^%s.*", accountsURL)));

		// enter a non-existing account
		enterData(identifier, "InvalidUser_UVW");

		// Click on next button
		clickNextButton(identifierNextButton);

		// Inspect error messages
		List<String> errMsg = session.getObjectIds(
				"//*[contains (text(), \"Couldn't find your Google Account\")]");
		highlight("//*[contains (text(), \"Couldn't find your Google Account\")]",
				2000);
		assertTrue(errMsg.size() > 0);
	}

	@Test(priority = 2, enabled = false)
	public void invalidPasswordTest() {

		// Arrange
		// Sign in
		highlight(signInLink);
		click(signInLink);

		// NOTE: session.click does not work
		// session.click(signInLink);

		// if we are on sign up page
		if (session.getLocation()
				.matches("https://accounts.google.com/SignUp.*$")) {
			// if we are on sign up page, there is a 'Sign in' link, click it again
			if (session.matches("#link-signin")) {
				session.focus("#link-signin");
				highlight("#link-signin");
				session.click("#link-signin");
			}
		} else {
			// we are on sign in page
		}

		Predicate<Session> urlChange = session -> session.getLocation()
				.matches(String.format("^%s.*", accountsURL));
		session.waitUntil(urlChange, 1000, 100);
		// String.format("Unexpected title '%s'", row.getAttribute("role")),
		assertTrue(session.getLocation()
				.matches(String.format("^(?:%s|%s).*", accountsURL, signingURL)));

		// Enter existing email id
		enterData(identifier, "automationnewuser24@gmail.com");

		// Click on next button
		clickNextButton(identifierNextButton);

		// Enter invalid password
		enterData(passwordInput, "InvalidPwd");

		// Click on next button
		clickNextButton(passwordNextButton);

		// Inspect error messages
		sleep(10000);

		List<String> errMsg = session
				.getObjectIds("//*[contains (text(),'Wrong password')]");
		assertTrue(errMsg.size() > 0);
	}

	@Test(priority = 4, enabled = true)
	public void loginTest() throws InterruptedException, IOException {

		// Arrange
		// Sign in
		highlight(signInLink);
		click(signInLink);

		// origin:
		// https://github.com/TsvetomirSlavov/JavaScriptForSeleniumMyCollection

		// Wait for page url to change
		session.waitUntil(
				s -> s.getLocation()
						.matches(String.format("^(?:%s|%s).*", accountsURL, signingURL)),
				1000, 100);
		System.err.println("loginTest() Locatiion: " + session.getLocation());
		assertTrue(session.getLocation()
				.matches(String.format("^(?:%s|%s).*", accountsURL, signingURL)));

		// TODO: examine it landed on https://accounts.google.com/AccountChooser

		// Enter existing email id
		enterData(identifier, "automationnewuser24@gmail.com");

		// Click on next button
		clickNextButton(identifierNextButton);

		// Enter the valid password
		enterData(passwordInput, "automationnewuser2410");

		// Click on next button
		clickNextButton(passwordNextButton);

		// Wait for page url to change
		/*
		urlChange = driver -> {
			String url = driver.getCurrentUrl();
			System.err.println("The url is: " + url);
			return (Boolean) url.matches("^https://mail.google.com/mail.*");
		};
		wait.until(urlChange);
		 */
		session.waitUntil(o -> {
			System.out.println("Checking if mail page is loaded...");
			return checkPage();
		}, 1000, 100);

		// Wait until form is rendered
		session.waitUntil(
				o -> ((String) o.evaluate("document.readyState")).matches("complete"),
				1000, 100);

		System.err.println("Click on profile image");
		// Click on profile image
		if (session.waitUntil(o -> o.matches(profileImage), 1000, 10)) {
			System.err.println("Select profile image");
			click(profileImage);
			// Wait until form is rendered
			session.waitUntil(o -> {
				return (boolean) o.evaluate("return document.readyState == 'complete'");
			}, 1000, 100);
		} else {
			System.err.println("Profile image not found");
		}
		// Sign out
		System.err.println("Sign out");

		System.err
				.println("Sign out button text: " + session.getText(signOutButton));
		// TODO: testOfElement does not work
		// System.err.println("Sign out button text: " +
		// textOfElement(signOutButton));
		highlight(signOutButton, 1000);
		click(signOutButton);
		try {
			session.click(signOutButton);
			sleep(60000);
		} catch (ElementNotFoundException e) {
			session.click(signOutButtonFallback);
			sleep(60000);
		}
	}

	private Boolean checkPage() {
		return session.getLocation().matches("^https://mail.google.com/mail.*");
	}

	private void enterData(String selector, String data) {
		session.waitUntil(o -> isVisible(selector), 1000, 100);
		try {
			session.focus(selector);
			session.sendKeys(data);
		} catch (CommandException e) {
			// Element is not focusable ?
			System.err.println("Exception in enerData: " + e.getMessage());
		}
	}

	private void clickNextButton(String selector) {
		session.waitUntil(o -> isVisible(selector), 1000, 100);
		try {
			session.focus(selector);
		} catch (CommandException e) {
			// Element is not focusable ?
			System.err.println("Exception in clickNextButton: " + e.getMessage());
		}
		highlight(selector);
		executeScript(session, "function() { this.click(); }", selector);
		// session.click is not too reliable
		// session.click(selector);
	}

}
