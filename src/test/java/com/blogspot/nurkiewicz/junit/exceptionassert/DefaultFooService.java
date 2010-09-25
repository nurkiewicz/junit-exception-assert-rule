package com.blogspot.nurkiewicz.junit.exceptionassert;

import java.io.IOException;

/**
 * @author Tomasz Nurkiewicz
 * @since 2010-09-24, 21:38:16
 */
public class DefaultFooService implements FooService {

	public static final String EX_NAME = "John";


	@Override
	public String echo(String name) {
		if(name.equals(EX_NAME))
			throw new IllegalArgumentException("Name: '" + EX_NAME + "' is not allowed");
		return "Hello, " + name + "!";
	}

	@Override
	public int sum(int x, int y) throws IOException{
		if(x == y)
			throw new IOException("Arguments (" + x + ") are equal");
		return x + y;
	}

	@Override
	public void ping(Object any) {
		if(any == null)
			throw new IllegalStateException("ping(null)");
	}
}
