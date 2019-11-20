package sample;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.runtime.CallFunctionOnResult;
import io.webfolder.cdp.type.runtime.RemoteObject;

/**
 * Selected test scenarios for CDP
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SampleTest {

	@Test(enabled = true)
	public void invalidUsernameTest() throws InterruptedException, IOException {

		int waitTimeout = 5000;
		int pollingInterval = 500;
		Launcher launcher = new Launcher();
		SessionFactory factory = launcher.launch();
		Session session = factory.create();
		session.clearCookies();
		session.clearCache();
		session.setUserAgent(
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.7 Safari/534.34");

		// navigate to start screen
		session.navigate("http://www.store.demoqa.com");

		// go to login page
		session.waitUntil(s -> s.getObjectIds("#account > a").size() > 0, waitTimeout, pollingInterval);
		// session.click("#account > a");
		executeScript(session, "function() { this.click(); }", "#account > a");
		session.waitUntil(s -> s.getLocation().matches("http://store.demoqa.com/products-page/your-account/"),
				waitTimeout, pollingInterval);

		// log in
		session.waitUntil(s -> s.getObjectIds("#log").size() > 0, waitTimeout, pollingInterval);
		session.focus("#log");
		session.sendKeys("testuser_3");
		session.focus("#pwd");
		session.sendKeys("Test@123");
		executeScript(session, "function() { this.click(); }", "#login");

		// confirm the error message is displayed
		session.waitUntil(_session -> _session.getObjectIds("//form[@id='ajax_loginform']/p[@class='response']/text()")
				.size() > 0, waitTimeout, pollingInterval);

		IntStream.rangeClosed(1, session.getObjectIds("//form[@id='ajax_loginform']/p[@class='response']/*").size())
				.forEach(pos -> {
					// TODO: cannot convert from String to int to map to stream of
					// response texts
					String response = session
							.getText(String.format("//form[@id='ajax_loginform']/p[@class='response']/*[%d]", pos));
					System.err.println(response);
				});
		assertTrue(session.getText("//form[@id='ajax_loginform']/p[@class='response']")
				.matches(".*The password you entered for the username testuser_3 is incorrect.*"));
		highlight("//form[@id='ajax_loginform']/p[@class='response']", session, 1000);
		session.stop();
		session.close();

	}

	protected Object executeScript(Session session, String script, String selectorOfElement) {
		if (!session.matches(selectorOfElement)) {
			return null;
		}
		String objectId = session.getObjectId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		Object value = null;
		try {
			functionResult = session.getCommand().getRuntime().callFunctionOn(script, objectId, null, null, null, null,
					null, null, null, null);
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
		return (boolean) (session.matches(selectorOfElement) && (boolean) executeScript(session,
				"function() { return(this.offsetWidth > 0 || this.offsetHeight > 0); }", selectorOfElement));
	}

	public void sleep(Integer seconds) {
		long secondsLong = (long) seconds;
		try {
			Thread.sleep(secondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void highlight(String selectorOfElement, Session session, int interval) {
		executeScript(session, "function() { this.style.border='3px solid yellow'; }", selectorOfElement);
		sleep(interval);
		executeScript(session, "function() { this.style.border=''; }", selectorOfElement);
	}

}
