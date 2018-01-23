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

	@Ignore
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
		session.navigate("https://www.google.com/gmail/about/#");

		// click sign-in link
		executeScript(session, "function() { this.click(); }",
				"a[class *= 'gmail-nav__nav-link__sign-in']");

		// wait till switched to sign in page
		session.waitUntil(
				s -> s.getLocation().matches("https://accounts.google.com/signin/"),
				1000, 100);
		session.waitUntil(s -> isVisible(s, "#identifierId"), 1000, 100);

		// enter a non existing user id

		session.focus("#identifierId");
		session.sendKeys("non existing user");

		// Click on next button

		executeScript(session, "function() { this.click(); }",
				"//*[@id='identifierNext']/content/span[contains(text(),'Next')]");

		// confirm the error message is displayed
		List<String> errMsg = session.getObjectIds(
				"//*[contains (text(), \"Couldn't find your Google Account\")]");
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

}
