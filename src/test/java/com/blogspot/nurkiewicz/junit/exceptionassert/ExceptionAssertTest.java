package com.blogspot.nurkiewicz.junit.exceptionassert;

import com.blogspot.nurkiewicz.junit.UnderTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * @author Tomasz Nurkiewicz
 * @since 2010-09-24, 21:05:38
 */
public class ExceptionAssertTest {

	@UnderTest
	private FooService fooService = new DefaultFooService();

	@Rule
	public ExceptionAssert exception = new ExceptionAssert();

	@Test
	public void shouldReturnHelloString() throws Exception {
		//given
		String name = "Tomek";

		//when
		final String result = fooService.echo(name);

		//then
		assertEquals("Hello, Tomek!", result);
	}

	@Test
	public void shouldThrowNpeWhenNullName() throws Exception {
		//given
		String name = null;

		//when
		final String result = fooService.echo(name);

		//then
		exception.expect(NullPointerException.class);
	}

	@Test
	public void shouldThrowIllegalArgumentWhenNameJohn() throws Exception {
		//given
		String name = "John";

		//when
		final String result = fooService.echo(name);

		//then
		exception.expect(IllegalArgumentException.class)
				.expectMessage("Name: 'John' is not allowed");
	}


	@Test
	public void shouldAllowMultipleMatchers() throws Exception {
		//given
		String name = "John";

		//when
		final String result = fooService.echo(name);

		//then
		exception.expect(IllegalArgumentException.class)
				.expectMessage(containsString("Name:"))
				.expectMessage(containsString("John"))
				.expectMessage(containsString("not allowed"));
	}

	@Test
	public void shouldThrowAssertionErrorWhenExceptionExpectedButNotThrown() throws Exception {
		//given
		String name = "James";

		//when
		final String result = fooService.echo(name);

		//then
		exception.expect(IllegalArgumentException.class);
	}

	@Test
	public void shouldThrowOriginalExceptionIfNotExpected() throws Exception {
		//given
		String name = "John";

		//when
		final String result = fooService.echo(name);

		//then
		//exception not expected
	}

}
