package com.blogspot.nurkiewicz.junit.exceptionassert;

import com.blogspot.nurkiewicz.junit.UnderTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;

/**
 * @author Tomasz Nurkiewicz
 * @since 2010-09-24, 21:00:55
 */
public class ExceptionAssert implements MethodRule {

	private Object testCase;

	private Matcher<Object> matcher;

	@SuppressWarnings("unchecked")
	public ExceptionAssert expect(Matcher<?> matcher) {
		if (this.matcher == null)
			this.matcher = (Matcher<Object>) matcher;
		else
			this.matcher = both(this.matcher).and(matcher);
		return this;
	}

	public ExceptionAssert expect(Class<? extends Throwable> type) {
		expect(instanceOf(type));
		return this;
	}

	public ExceptionAssert expectMessage(String substring) {
		expectMessage(containsString(substring));
		return this;
	}

	public ExceptionAssert expectMessage(Matcher<String> matcher) {
		expect(hasMessage(matcher));
		return this;
	}

	@Override
	public Statement apply(Statement base, FrameworkMethod method, Object testCase) {
		this.testCase = testCase;
		return new ExceptionAssertStatement(base);
	}

	private class ExceptionAssertStatement extends Statement {

		private final Statement base;

		private Throwable exceptionThrownFromClassUnderTest;
		private Field underTestField;

		public ExceptionAssertStatement(Statement base) {
			this.base = base;
			underTestField = findClassUnderTestField(testCase);
		}

		@Override
		public void evaluate() throws Throwable {
			final Object originalClassUnderTest = wrapClassUnderTest(testCase);
			try {
				base.evaluate();
			} finally {
				setUnderTestField(originalClassUnderTest);
			}
			verify();
		}

		private void verify() throws Throwable {
			if (matcher == null) {
				if (exceptionThrownFromClassUnderTest != null)
					throw exceptionThrownFromClassUnderTest;
			} else if (exceptionThrownFromClassUnderTest == null)
				throw new AssertionError("Expected test to throw " + StringDescription.toString(matcher));
			else
				assertThat(exceptionThrownFromClassUnderTest, matcher);
		}

		private void setUnderTestField(Object originalClassUnderTest) throws IllegalAccessException {
			underTestField.set(testCase, originalClassUnderTest);
		}

		private Object wrapClassUnderTest(Object testCase) {
			try {
				underTestField.setAccessible(true);
				Object classUnderTest = underTestField.get(testCase);
				final Object wrappedClassUnderTest = wrapWithProxy(classUnderTest);
				setUnderTestField(wrappedClassUnderTest);
				return classUnderTest;
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		private Object wrapWithProxy(final Object classUnderTest) {
			verifyClassUnderTest(classUnderTest);
			return Proxy.newProxyInstance(classUnderTest.getClass().getClassLoader(), new Class[]{underTestField.getType()}, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					try {
						return method.invoke(classUnderTest, args);
					} catch (InvocationTargetException e) {
						exceptionThrownFromClassUnderTest = e.getCause();
						return null;
					}
				}
			});
		}

		private void verifyClassUnderTest(Object classUnderTest) {
			if (!underTestField.getType().isInterface())
				throw new IllegalArgumentException("Field marked with @UnderTest must be of interface type");
			if (classUnderTest == null)
				throw new IllegalArgumentException("Field marked with @UnderTest must not be null");
		}

		private Field findClassUnderTestField(Object testCase) {
			final Field[] fields = testCase.getClass().getDeclaredFields();
			Field underTestField = null;
			for (Field field : fields)
				if (field.getAnnotation(UnderTest.class) != null)
					if (underTestField == null)
						underTestField = field;
					else
						throw new IllegalArgumentException("More than one field marked with @UnderTest annotation");
			if(underTestField == null)
				throw new IllegalArgumentException("You must mark exactly one test case field with @UnderTest annotation");
			return underTestField;
		}

	}

	private Matcher<Throwable> hasMessage(final Matcher<String> matcher) {
		return new TypeSafeMatcher<Throwable>() {
			public void describeTo(Description description) {
				description.appendText("exception with message ");
				description.appendDescriptionOf(matcher);
			}

			@Override
			public boolean matchesSafely(Throwable item) {
				return matcher.matches(item.getMessage());
			}
		};
	}

}
