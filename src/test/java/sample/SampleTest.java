package sample;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.runtime.CallFunctionOnResult;
import io.webfolder.cdp.type.runtime.RemoteObject;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SampleTest {

	// @Ignore
	@Test
	public void invalidUsernameTest() throws InterruptedException, IOException {

		Launcher launcher = new Launcher();
		SessionFactory factory = launcher.launch();
		Session session = factory.create();
		session.clearCookies();
		session.clearCache();
		session.setUserAgent(
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.7 Safari/534.34");

		// navigate to start screen
		session.navigate("http://www.store.demoqa.com");

		sleep(5000);
		// go to login page

		// session.click("#account > a");
		executeScript(session, "function() { this.click(); }", "#account > a");
		session.waitUntil(
				s -> s.getLocation()
						.matches("http://store.demoqa.com/products-page/your-account/"),
				1000, 100);

		// log in
		sleep(5000);
		session.focus("#log");
		session.sendKeys("testuser_3");
		session.focus("#pwd");
		session.sendKeys("Test@123");
		executeScript(session, "function() { this.click(); }", "#login");

		sleep(5000);
		// confirm the error message is displayed
		List<String> errMsg = session.getObjectIds(
				"//*[contains (text(), \"The password you entered for the username testuser_3 is incorrect\")]");
		assertTrue(errMsg.size() > 0);

		session.stop();
		session.close();

	}

	protected Object executeScript(Session session, String script,
			String selectorOfElement) {
		if (!session.matches(selectorOfElement)) {
			return null;
		}
		String objectId = session.getObjectId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		Object value = null;
		try {
			functionResult = session.getCommand().getRuntime().callFunctionOn(script,
					objectId, null, null, null, null, null, null, null, null);
			if (functionResult != null) {
				result = functionResult.getResult();
				if (result != null) {
					value = result.getValue();
					session.releaseObject(result.getObjectId());
				}
			}
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.getMessage());
		}
		return value;
	}

	protected boolean isVisible(Session session, String selectorOfElement) {
		return (boolean) (session.matches(selectorOfElement)
				&& (boolean) executeScript(session,
						"function() { return(this.offsetWidth > 0 || this.offsetHeight > 0); }",
						selectorOfElement));
	}

	public void sleep(Integer seconds) {
		long secondsLong = (long) seconds;
		try {
			Thread.sleep(secondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
