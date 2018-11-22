package sample;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LocalFileTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final String page = "ElementSearch.html";

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			System.err.println(String.format("Error in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		session.navigate("about:blank");
		super.afterMethod();
	}

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(session, notNullValue());
	}

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to file: " + page);
		session.navigate(super.getPageContent(page));
		session.waitDocumentReady(10000);
		session.waitUntil(s -> s.getLocation()
				.equals(String.format("%s", super.getPageContent(page).toString())));
		System.err.println("Verified location: " + session.getLocation());

	}

	@Test(enabled = true)
	public void test() {
		session.waitUntil(o -> isVisible("body > h1"), 1000, 100);
		assertThat(session.getText("body > h1"), containsString("Element Search"));
		System.err.println("Verified text:" + session.getText("body > h1"));
	}
}