package sample;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.testng.ITestResult;

import io.webfolder.cdp.exception.CommandException;

public class SeleniumEasyTest extends BaseTest {

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static final String baseURL = "https://www.seleniumeasy.com/test/table-sort-search-demo.html";
	private static Map<String, String> employees = new HashMap<>();
	static {
		employees.put("Bradley Greer", "Software Engineer");
		employees.put("Charde Marshall", "Regional Director");
	}
	private static boolean status = false;

	@BeforeMethod
	public void beforeMethod() {
		System.err.println("Navigate to URL: " + baseURL);
		session.navigate(baseURL);
		session.waitDocumentReady(10000);
		session.waitUntil(s -> s.getLocation().equals(baseURL));
		session.waitUntil(o -> isVisible("#site-name > a"), 1000, 100);
		// Exception in thread "cdp4j-15" io.webfolder.cdp.exception.CdpException:
		// Unable to acquire lock
	}

	@AfterMethod
	public void AfterMethod(ITestResult result) {
		if (verificationErrors.length() != 0) {
			System.err.println(String.format("Error in the method %s : %s",
					result.getMethod().getMethodName(), verificationErrors.toString()));
		}
		session.navigate("about:blank");
		super.afterMethod();

	}

	// This test will run 2 times
	@Test(dataProvider = "xpaths")
	public void testXpathChecker(String xpathTemplate) {
		for (String name : employees.keySet()) {
			String xpath = String.format(xpathTemplate, name);
			status = false;
			System.err.println("Testing xpath: " + xpath);
			try {
				status = session.matches(xpathTemplate, name);
			} catch (CommandException e) {
				verificationErrors.append(e.toString());
				System.err.println("Failed with XPath: " + xpathTemplate + "," + name
						+ "\n" + e.toString());
			}
			try {
				status = session.matches(xpath);
			} catch (CommandException e) {
				verificationErrors.append(e.toString());
				System.err.println("Failed with XPath: " + xpath + "\n" + e.toString());
			}
			String position = (String) session.getProperty(xpath, "innerHTML");
			assertThat(position, is(employees.get(name)));
			System.err.println(
					String.format("Successfully verifies: %s: %s", name, position));
		}
	}

	@DataProvider(name = "xpaths", parallel = false)
	public Object[][] dataProviderInline() {
		return new Object[][] {
				// http://zvon.org/xxl/XPathTutorial/Output/example15.html
				{ "//*[@id=\"example\"]/tbody/tr/td[@data-search=\"%s\"]/following-sibling::td[1]" },
				// https://stackoverflow.com/questions/10247978/xpath-with-multiple-conditions
				{ "//*[@id=\"example\"]/tbody/tr[./td[@data-search=\"%s\"]]/td[2]" }, };
	}
}