package sample;

import static java.lang.System.err;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.exception.CommandException;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.runtime.CallArgument;
import io.webfolder.cdp.type.runtime.CallFunctionOnResult;
import io.webfolder.cdp.type.runtime.RemoteObject;

/**
 * Selected test scenarios for CDP
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class BaseTest {

	public String baseURL = "about:blank";
	public Session session;
	int waitTimeout = 5000;
	int pollingInterval = 500;
	private static final boolean useChromium = Boolean.parseBoolean(getEnvWithDefault("USE_CHROMIUM", "false"));

	public static String getEnvWithDefault(String name, String defaultValue) {
		String value = System.getenv(name);
		if (value == null || value.length() == 0) {
			value = defaultValue;

		}
		return value;
	}

	@BeforeClass
	public void beforeClass() throws IOException {
		Launcher launcher = new Launcher();
		if (useChromium) {
			// which chromium-browser
			java.lang.System.setProperty("chrome_binary", "/usr/bin/chromium-browser");
		}
		// at io.webfolder.cdp.Launcher.internalLaunch(Launcher.java:211)

		SessionFactory factory = launcher.launch(Arrays.asList("--remote-debugging-port=9222", "--disable-gpu", "--headless", "--window-size=1200x800"));
		sleep(1000);
		try {
			session = factory.create();
			sleep(1000);
			// install extensions
			session.clearCookies();
			session.clearCache();
			session.setUserAgent(
					"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.7 Safari/534.34");
			session.navigate(baseURL);
		} catch (CommandException e) {
			throw new RuntimeException(e);
		}

		System.err.println("Location:" + session.getLocation());
	}

	@AfterClass
	public void afterClass() {
		if (session != null) {
			session.stop();
			session.close();
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeMethod(Method method) {
		String methodName = method.getName();
		System.err.println("Test Name: " + methodName + "\n");
	}

	@AfterMethod
	public void afterMethod() {
		session.navigate("about:blank");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
	}

	@AfterTest(alwaysRun = true)
	public void afterTest() {
	}

	protected void sleep(long timeoutSeconds) {
		try {
			Thread.sleep(timeoutSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void sleep(Integer timeoutSeconds) {
		long timeoutSecondsLong = (long) timeoutSeconds;
		try {
			Thread.sleep(timeoutSecondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void highlight(String selectorOfElement) {
		highlight(selectorOfElement, session, 100);
	}

	protected void highlight(String selectorOfElement, int interval) {
		highlight(selectorOfElement, session, (long) interval);
	}

	protected void highlight(String selectorOfElement, Session session) {
		highlight(selectorOfElement, session, 100);
	}

	protected void highlight(String selectorOfElement, Session session, long interval) {
		executeScript(session, "function() { this.style.border='3px solid yellow'; }", selectorOfElement);
		sleep(interval);
		executeScript(session, "function() { this.style.border=''; }", selectorOfElement);
	}

	protected void clear(String selectorOfElement) {
		executeScript("function() { this.value=''; }", selectorOfElement);
	}

	protected Object executeScript(Session session, String script, String selectorOfElement) {
		if (!session.matches(selectorOfElement)) {
			return null;
		}
		String objectId = session.getObjectId(selectorOfElement);
		Integer nodeId = session.getNodeId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		Object value = null;
		try {
			// NOTE: ObjectId must not be specified together with executionContextId
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
		// System.err.println("value: " + value);
		return value;
	}

	protected Object executeScript(String script, String selectorOfElement) {
		return executeScript(session, script, selectorOfElement);
	}

	protected Object executeScript(Session session, String script, String selectorOfElement, List<String> data) {
		if (!session.matches(selectorOfElement)) {
			return null;
		}
		List<CallArgument> arguments = new ArrayList<>();
		CallArgument argument = new CallArgument();
		argument.setValue(data.get(0));
		arguments.add(argument);
		String objectId = session.getObjectId(selectorOfElement);
		Integer nodeId = session.getNodeId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		Object value = null;
		try {
			// NOTE: ObjectId must not be specified together with executionContextId
			// NOTE: the arguments are not really passed properly yet
			functionResult = session.getCommand().getRuntime().callFunctionOn(script, objectId, arguments, null, null,
					null, null, null, null, null);
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
		// System.err.println("value: " + value);
		return value;
	}

	protected Object executeScript(String script, String selectorOfElement, List<String> data) {
		return executeScript(session, script, selectorOfElement, data);
	}

	// https://stackoverflow.com/questions/1343237/how-to-check-elements-visibility-via-javascript
	protected boolean isVisible(String selectorOfElement) {
		return (boolean) (session.matches(selectorOfElement)
				&& (boolean) executeScript("function() { return(this.offsetWidth > 0 || this.offsetHeight > 0); }",
						selectorOfElement));
	}

	// NOTE: limited testing performed so far.
	protected String xpathOfElement(String selectorOfElement) {
		session.evaluate(getScriptContent("xpathOfElement.js"));
		return (String) executeScript("function() { return xpathOfElement(this); }", selectorOfElement);
	}

	protected String cssSelectorOfElement(String selectorOfElement) {

		String cssSelectorOfElement = getScriptContent("cssSelectorOfElement.js");
		session.evaluate(cssSelectorOfElement);
		return (String) executeScript("function() { return cssSelectorOfElement(this); }", selectorOfElement);
	}

	protected String textOfElement(String selectorOfElement) {
		session.evaluate(getScriptContent("getText.js"));
		return (String) executeScript("function() { return getText(this);}", selectorOfElement);
	}

	protected void fastSetText(String selectorOfElement, String text) {
		session.evaluate(getScriptContent("setValue.js"));

		List<String> data = new ArrayList<>();
		data.add(text);
		try {
			executeScript(String.format("function() { return setValue(this, '%s');}", text), selectorOfElement, data);
		} catch (Exception e) {
			err.println("Ignored: " + e.toString());
		}
	}

	protected void scrollIntoView(String selectorOfElement) {
		session.evaluate(getScriptContent("scrollIntoView.js"));
		executeScript("function() { return scrollIntoView(this);}", selectorOfElement);
	}

	protected void scrollIfOutOfView(String selectorOfElement) {
		session.evaluate(getScriptContent("scrollIfOutOfView.js"));
		executeScript("function() { return scrollIfOutOfView(this, true);}", selectorOfElement);
	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = BaseTest.class.getClassLoader().getResourceAsStream(scriptName);

			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			// System.err.println("Loaded:\n" + new String(bytes, "UTF-8"));
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Cannot load file: " + scriptName);
		}
	}

	protected void click(String selector) {
		executeScript(session, "function() { this.click(); }", selector);
	}

	protected static String getPageContent(String pagename) {
		try {
			URI uri = BaseTest.class.getClassLoader().getResource(pagename).toURI();
			System.err.println("Testing: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
