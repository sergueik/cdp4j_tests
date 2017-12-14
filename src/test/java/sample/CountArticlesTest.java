package sample;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertTrue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
// http://tutorials.jenkov.com/java-util-concurrent/countdownlatch.html

public class CountArticlesTest extends BaseTest {

	private String baseUrl = "http://habrahabr.ru/search/?";
	private String searchInputSelector = "form[id='inner_search_form'] div[class='search-field__wrap'] input[name='q']";
	private String searchInputXPath = "//form[@id='inner_search_form']/div[@class='search-field__wrap']/input[@name='q']";
	// XPath does not work well with CDP
	private String pubsCountSelector = "span[class*='tabs-menu__item-counter'][class*='tabs-menu__item-counter_total']";
	private int searchTimeout = 10000;

	@BeforeMethod
	public void beforeMethod(Method method) {
		super.beforeMethod(method);
		sleep(1000);
		assertThat(session, notNullValue());
		session.navigate(baseURL);
		session.waitDocumentReady(10000);
		System.err.println("url: " + session.getLocation());
	}

	// NOTE: unstable
	@Test(enabled = true)
	private void test1() {
		Object[][] inputs = new Object[][] { { "junit", 100.0 },
				// { "testng", 30.0 },
				// { "spock", 10.0 },
		};
		for (Object[] input : inputs) {
			parseSearchResult(input[0].toString(),
					Double.valueOf(input[1].toString()));
			session.navigate("about:blank");
			sleep(5000);

		}
	}

	@Test(enabled = false, singleThreaded = true, dataProvider = "Inline")
	private void test2(Object... input) {
		parseSearchResult(input[0].toString(), Double.valueOf(input[1].toString()));
		/*
			Object[][] inputs = new Object[][] { { "junit", 100.0 }, { "testng", 30.0 },
					{ "spock", 10.0 }, };
			for (Object[] input : inputs) {
				parseSearchResult(input[0].toString(),
						Double.valueOf(input[1].toString()));
			}
			*/
	}

	// static disconnected data provider
	@DataProvider(parallel = true)
	public static Object[][] Inline() {
		return new Object[][] { { "testng", 50.0 }, { "junit", 250.0 },
				// { "spock", 10.0 },
		};
	}

	private void parseSearchResult(String search_keyword, double expected_count) {
		session.navigate(baseUrl);

		System.err.println(String.format("Search keyword:'%s'\tLink #:%d",
				search_keyword, (int) expected_count));
		session.waitDocumentReady(searchTimeout);
		session.waitUntil(o -> o.matches("#inner_search_form"), 1000, 100);

		/*
		session.waitUntil(o -> o.matches(".search-field__input"), 1000, 100);
		assertThat(session.getObjectId(".search-field__input"), notNullValue());
		highlight(".search-field__input");
		*/
		session.waitUntil(o -> o.matches(searchInputSelector), searchTimeout,
				searchTimeout / 10);
		assertThat(session.getObjectId(searchInputSelector), notNullValue());
		highlight(searchInputSelector);
		// TODO: investigate
		// session.sendKeys(session.SPECIAL_KEYS...)
		super.clear(searchInputXPath);
		session.focus(searchInputSelector);
		session.sendKeys(search_keyword);
		session.sendEnter();

		session.waitUntil(o -> o.matches(pubsCountSelector), searchTimeout,
				searchTimeout / 10);
		highlight(pubsCountSelector, 1000);
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(session.getText(pubsCountSelector));
		int publicationsFound = 0;
		if (matcher.find()) {
			publicationsFound = Integer.parseInt(matcher.group(1));
			System.err.println("# of publications " + publicationsFound);
		} else {
			System.err.println("No publications");
		}
		assertTrue(publicationsFound >= expected_count);
		sleep(1000);
	}
}
