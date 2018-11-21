package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import org.hamcrest.CoreMatchers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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