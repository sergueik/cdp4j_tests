
package sample;

import org.testng.annotations.Test;

/**
 * Selected test scenarios for CDP
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PageLoadTest extends BaseTest {

	private static String baseURL = "https://www.priceline.com/";
	private static int pageLoadTimeout = 5000;

	@Test(enabled = true)
	public void networkTimingTest() throws Exception {

		session.navigate(baseURL);
		session.waitDocumentReady(pageLoadTimeout);

		session.evaluate(getScriptContent("timing.js"));

		// TODO: collect result in native performance.timing object format
		// io.webfolder.cdp.internal.gson.JsonSyntaxException:
		// java.lang.IllegalStateException: Expected STRING but was BEGIN_OBJECT at
		// path $
		String result = session.callFunction("window.timing.getTimes",
				String.class);
		if (result != null) {
			System.err.println("result: " + result);
		} else {
			throw new RuntimeException("result is null");
		}
	}
}
