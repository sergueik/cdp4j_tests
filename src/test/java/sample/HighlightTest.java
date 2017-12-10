package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
// http://tutorials.jenkov.com/java-util-concurrent/countdownlatch.html

public class HighlightTest extends BaseTest {

	private String baseURL = "https://webfolder.io";

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		assertThat(session, notNullValue());
		session.navigate(baseURL).waitDocumentReady();
		session.waitDocumentReady(5000);
		System.err.println("url: " + session.getLocation());
	}

	@Test(enabled = true)
	public void basicXPathTest() {
		String xpath = "/html/head/title";
		// Arrange
		session.waitUntil(o -> o.matches(xpath), 1000, 100);
		// Act
		String pageTitle = session.getText(xpath);

		// Assert
		sleep(20000);
		Assert.assertEquals(pageTitle, "WebFolder");
	}

	// this test uses session method to locate the link (verifies through text
	// check)
	// and then uses baseTest method to click on the target using Javascript
	// snippet.
	// The click is performed on the correct element - verified by checking the
	// header of the destination page
	@Test(enabled = true)
	public void workingClickTest() {
		String linkSelector = "#nav > ul > li:nth-child(2) > a";
		String pageHeaderSelector = "body > article > h2";
		// Arrange
		session.waitUntil(o -> isVisible(linkSelector), 1000, 100);
		// Act
		Assert.assertEquals(session.getText(linkSelector), "Support");
		highlight(linkSelector, 1000);
		super.click(linkSelector);
		session.waitUntil(o -> o.matches(pageHeaderSelector), 5000, 100);
		// Assert
		highlight(pageHeaderSelector, 1000);
		sleep(1000);
		highlight(pageHeaderSelector, 1000);
		Assert.assertEquals(session.getText(pageHeaderSelector), "Support");
	}

	// this test uses session method to locate the link (verifies through text
	// check)
	// and then to click. The click is performed on the wrong element
	// would fail: expected [Support] but found [Products]
	@Test(enabled = false)
	public void failingClickTest() {
		String linkSelector = "#nav > ul > li:nth-child(2) > a";
		String pageHeaderSelector = "body > article > h2";
		// Arrange
		session.waitUntil(o -> isVisible(linkSelector), 1000, 100);
		// Act
		Assert.assertEquals(session.getText(linkSelector), "Support");
		highlight(linkSelector, 1000);
		session.click(linkSelector);
		session.waitUntil(o -> o.matches(pageHeaderSelector), 1000, 100);
		// Assert
		highlight(pageHeaderSelector, 1000);
		sleep(1000);
		Assert.assertEquals(session.getText(pageHeaderSelector), "Support");
	}

	@Test(enabled = true)
	public void basicCSSTest() {
		String cssSelector = "head > title";
		// Arrange
		session.waitUntil(o -> o.matches(cssSelector), 1000, 100);
		// Act
		String pageTitle = session.getText(cssSelector);
		// Assert
		Assert.assertEquals(pageTitle, "WebFolder");

	}

	@Test(enabled = true)
	public void xPathContainsTest() {

		String xpath = "//*[@id='nav']//a[contains(@href, 'support.html')]";
		// Arrange
		session.waitUntil(o -> isVisible(xpath), 1000, 100);
		// Act
		String text = session.getText(xpath);
		highlight(xpath, 1000);
		// Assert
		Assert.assertEquals(text, "Support");

		/*
		// Act	
		String linkSupportComputedXPath = xpathOfElement(linkSupportXPath);
		// Assert
		Assert.assertEquals(linkSupportComputedXPath, "...");
		*/
	}

	@Test(enabled = true)
	public void computedSelectorTest() {

		String xpath = "//*[@id='nav']//a[contains(@href, 'support.html')]";
		// Arrange
		session.waitUntil(o -> isVisible(xpath), 1000, 100);
		// Act
		String text = session.getText(xpath);
		highlight(xpath, 1000);
		String computedText = textOfElement(xpath);
		// Assert
		String computedXPath = xpathOfElement(xpath);
		Assert.assertEquals(computedXPath, "//nav[@id=\"nav\"]/ul/li[2]/a");
		System.err.println("xpath: " + xpathOfElement(xpath));
		// Assert
		String computedCSS = cssSelectorOfElement(xpath);
		Assert.assertEquals(computedCSS, "nav#nav > ul > li:nth-of-type(2) > a");
		System.err.println("css: " + cssSelectorOfElement(xpath));

		// Assert
		Assert.assertEquals(text, "Support");
		Assert.assertEquals(computedText, "Support");
		System.err.println("text: " + computedText);
	}

	@Test(enabled = true)
	public void elementIteratorTest() {

		String cssSelector = "#nav a";
		// Arrange
		session.waitUntil(o -> o.getObjectIds(cssSelector).size() > 0, 1000, 100);
		// Act
		String id = session.getObjectIds(cssSelector).stream().filter(_id -> {
			System.err.println("object id: " + _id);
			String _href = (String) session.getPropertyByObjectId(_id, "href");
			System.err.println("href attribute: " + _href);
			return _href.matches(".*about.html$");
		}).collect(Collectors.toList()).get(0);
		// Assert
		Assert.assertEquals(session.getPropertyByObjectId(id, "href"),
				"https://webfolder.io/about.html");
		// Assert
		Assert.assertEquals(session.getPropertyByObjectId(id, "innerHTML"),
				"About");
	}

	@Test(enabled = true)
	public void siblingXPathTest() {

		String xpath = "//*[@id='nav']//a[contains(@href, 'support.html')]/../following-sibling::li/a";
		// Arrange
		session.waitUntil(o -> isVisible(xpath), 1000, 100);

		// Act
		String text = session.getText(xpath);
		highlight(xpath);
		// Assert
		Assert.assertEquals(text, "About");
		// System.err.println("xpath: " + session.getPathname());
	}

}
