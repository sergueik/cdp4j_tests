### Info
The [Chrome Devtools](https://github.com/ChromeDevTools/awesome-chrome-devtools)
 project
 offers an alternative powerful set of API to manage te browser, and appears currently targeted primarily for Javascript developers.
  * [puppeteer online](https://try-puppeteer.appspot.com/)
  * [GoogleChrome/puppeteer](https://github.com/GoogleChrome/puppeteer)
  * [ChromeDevTools/debugger-protocol-viewer](https://github.com/ChromeDevTools/debugger-protocol-viewer)

This project exercises the [Java client of Chrome DevTools Protocol](https://github.com/webfolderio/cdp4j) trying to (re)construct code patterns familiar to a __Selenium__ developer to reduce the effort of rewriting existing __Selenium__ -based test suite to __CDP__ backend.

  * [cdp4j/javadoc](https://webfolder.io/cdp4j/javadoc/index.html)


A massive test suite developed earlier practicing various publicly available practice pages is uses as a reference.
  * [practice Selenium testing](https://github.com/sergueik/selenium_java/tree/master/java8)
  * [http://suvian.in/selenium](http://suvian.in/selenium)
  * [http://www.way2automation.com](http://www.way2automation.com)


The project is in active development, so please bookmark and check for updates.

### Examples
The following code fragments familiar to a Selenium developer are implemented on top of __CDP__ and demonstrated:

* Iterate over and filter the set of elements, perform further action with specific members similar to Selenium `findElements`:
```java

		Launcher launcher = new Launcher();
		SessionFactory factory = launcher.launch();
		session = factory.create();
		// install extensions
		session.installSizzle();
		session.useSizzle();
		session.clearCookies();
		session.clearCache();
		session.setUserAgent(
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.7 Safari/534.34");
		session.navigate("https://webfolder.io");

    // examine navbar links
		String cssSelector = "#nav a";

		// Arrange
		session.waitUntil(o -> o.getObjectIds(cssSelector).size() > 0, 1000, 100);
		// Act
		String id = session.getObjectIds(cssSelector).stream().filter(_id ->
		((String) session.getPropertyByObjectId(_id, "href")).matches(".*about.html$")).collect(Collectors.toList()).get(0);
		// Assert
		Assert.assertEquals(session.getPropertyByObjectId(id, "href"),
				"https://webfolder.io/about.html");
		Assert.assertEquals(session.getPropertyByObjectId(id, "innerHTML"),
				"About");
```
* Highlight the element of interest by applying a border style:
```java
	protected void highlight(String selectorOfElement, Session session,
			long interval) {
		String objectId = session.getObjectId(selectorOfElement);
		Integer nodeId = session.getNodeId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		executeScript("function() { this.style.border='3px solid yellow'; }",
				selectorOfElement);
		sleep(interval);
		executeScript("function() { this.style.border=''; }", selectorOfElement);
	}

	protected Object executeScript(Session session, String script,
			String selectorOfElement) {
		if (!session.matches(selectorOfElement)) {
			return null;
		}
		String objectId = session.getObjectId(selectorOfElement);
		Integer nodeId = session.getNodeId(selectorOfElement);
		CallFunctionOnResult functionResult = null;
		RemoteObject result = null;
		Object value = null;
		try {
			functionResult = session.getCommand().getRuntime().callFunctionOn(script,
					objectId, null, null, null, null, null, null, nodeId, null);
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

```
* Wait until certain element is [visible](https://stackoverflow.com/questions/1343237/how-to-check-elements-visibility-via-javascript) (similar to Selenium `ExpectedConditions`):
```java
	protected boolean isVisible(String selectorOfElement) {
		return (boolean) (session.matches(selectorOfElement)
				&& (boolean) executeScript(
						"function() { return(this.offsetWidth > 0 || this.offsetHeight > 0); }",
						selectorOfElement));
	}

    session.navigate("https://www.google.com/gmail/about/#")
		session.waitUntil(o -> isVisible(selector), 1000, 100);

```
* [Click on element](https://www.w3schools.com/jsref/met_html_click.asp)- the [Session click(String selector) method](https://webfolder.io/cdp4j/javadoc/io/webfolder/cdp/session/Mouse.html#click-java.lang.String-) appears to be unreliable)
```java
	protected void click(String selector) {
		executeScript(session, "function() { this.click(); }", selector);
	}
```

* Compute the XPath / Css Selector or other DOM attributes by executing some Javascript (possibly recursively) in the broser:
```javascript
cssSelectorOfElement = function(element) {
	if (!(element instanceof Element))
		return;
	var path = [];
	while (element.nodeType === Node.ELEMENT_NODE) {
		var selector = element.nodeName.toLowerCase();
		if (element.id) {
			if (element.id.indexOf('-') > -1) {
				selector += '[id="' + element.id + '"]';
			} else {
				selector += '#' + element.id;
			}
			path.unshift(selector);
			break;
		} else if (element.className) {
			selector += '.' + element.className.replace(/^\s+/,'').replace(/\s+$/,'').replace(/\s+/g, '.');
		} else {
			var element_sibling = element;
			var sibling_cnt = 1;
			while (element_sibling = element_sibling.previousElementSibling) {
				if (element_sibling.nodeName.toLowerCase() == selector)
					sibling_cnt++;
			}
			if (sibling_cnt != 1)
				selector += ':nth-of-type(' + sibling_cnt + ')';
		}
		path.unshift(selector);
		element = element.parentNode;
	}
	return path.join(' > ');
}
```
```java
	protected String cssSelectorOfElement(String selectorOfElement) {
		session.evaluate(getScriptContent("cssSelectorOfElement.js"));
		return (String) executeScript("function() { return cssSelectorOfElement(this); }",
				selectorOfElement);
	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = BaseTest.class.getClassLoader()
					.getResourceAsStream(scriptName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			// System.err.println("Loaded:\n" + new String(bytes, "UTF-8"));
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Cannot load file: " + scriptName);
		}
	}

		String xpath = "//*[@id='nav']//a[contains(@href, 'support.html')]";
		// Arrange
		session.waitUntil(o -> isVisible(xpath), 1000, 100);
		// Act
		highlight(xpath, 1000);
		String computedXPath = xpathOfElement(xpath);
		String computedCssSelector = cssSelectorOfElement(xpath);
		String computedText = textOfElement(xpath);
		// Assert
		Assert.assertEquals(computedXPath, "//nav[@id=\"nav\"]/ul/li[2]/a");
		Assert.assertEquals(computedCssSelector,
				"nav#nav > ul > li:nth-of-type(2) > a");
		Assert.assertEquals(computedText, "Support");
```
* Finding the target element by applying `findElement(s)` to a certain element found earlier.
```java
		// Arrange
		session.navigate("http://suvian.in/selenium/1.5married_radio.html");

		String formLocator = ".intro-header .container .row .col-lg-12 .intro-message form";
    // locate the form on the page
		session.waitUntil(o -> o.matches(formLocator), 1000, 100);
		assertThat(session.getObjectId(formLocator), notNullValue());
		highlight(formLocator, 1000);
		sleep(1000);

    // get the HTML of the form element
		elementContents = (String) executeScript(
				"function() { return this.outerHTML; }", formLocator);

		// Parse the HTML looking for "yes" or "no" - depends on desired married
		// status
		String label = "no";

		String line = new ArrayList<String>(
				Arrays.asList(elementContents.split("<br/?>"))).stream()
						.filter(o -> o.toLowerCase().indexOf(label) > -1).findFirst().get();
		Matcher matcher = Pattern.compile("value=\\\"([^\"]*)\\\"").matcher(line);
		String checkboxValue = null;
		if (matcher.find()) {
			checkboxValue = matcher.group(1);
			System.err.println("checkbox value = " + checkboxValue);
		} else {
			System.err.println("checkbox value not found");
		}
		String checkBoxElementId = null;
    
		// Act
    // combine the selectors
		String checkBoxElementSelector = String.format(
				"%s input[name='married'][value='%s']", formLocator, checkboxValue);
    checkBoxElementId = session.getObjectId(checkBoxElementSelector);
		// Assert the checkbox is found
		assertThat(checkBoxElementId, notNullValue());
		highlight(checkBoxElementSelector);
    // Act
		click(checkBoxElementSelector);
		// Assert that the checkbox gets into checked state (passes)
		assertThat(
				session.getObjectId(
						String.format("%s%s", checkBoxElementSelector, ":checked")),
				notNullValue());
		sleep(500);
		// Assert evaluate the checked semi attribute (fails, probably wrong sizzle)
		assertTrue(Boolean.parseBoolean(
				session.getAttribute(checkBoxElementSelector, ":checked")));
		sleep(500);
	}
```
* Collect page timings (also possible to have the same at page element level):
```javascript
// based on: https://github.com/addyosmani/timing.js/blob/master/timing.js
// NOTE: not computing timings.loadTime, timings.domReadyTime  etc.
(function(window) {
  'use strict';
  window.timing = window.timing || {
    getTimes: function(opt) {
      var performance = window.performance ||
        window.webkitPerformance || window.msPerformance ||
        window.mozPerformance;

      if (performance === undefined) {
        return '';
      }
      var timings = performance.timing || {};
        return JSON.stringify(timings);
    },

    getNetwork: function(opt) {
      var network = performance.getEntries() || {};
        return JSON.stringify(network);
    }
  }
})(typeof window !== 'undefined' ? window : {});
```
then
```java
	private static String baseURL = "https://www.priceline.com/";
	private static int pageLoadTimeout = 5000;

	@Test(enabled = true)
	public void timingTest() throws Exception {

		session.navigate(baseURL);
		session.waitDocumentReady(pageLoadTimeout);

		session.evaluate(getScriptContent("timing.js"));

		String result = session.callFunction("window.timing.getTimes",
				String.class);
		if (result != null) {
			System.err.println("result: " + result);
		} else {
			throw new RuntimeException("result is null");
		}
	}
```
### TODO

The following code fragments are yet unclear how to implement:

* Alerts

### See also
  * [stackoverflow](https://stackoverflow.com/questions/tagged/google-chrome-devtools)
### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
