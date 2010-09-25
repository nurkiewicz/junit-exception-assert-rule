package com.blogspot.nurkiewicz.junit.exceptionassert;

import java.io.IOException;

/**
 * @author Tomasz Nurkiewicz
 * @since 2010-09-24, 21:37:57
 */
public interface FooService {

	public String echo(String name);

	public int sum(int x, int y) throws IOException;

	public void ping(Object any);
}
