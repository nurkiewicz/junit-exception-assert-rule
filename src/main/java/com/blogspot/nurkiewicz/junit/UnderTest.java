package com.blogspot.nurkiewicz.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks <i>class under test</i> in test case.
 *
 * @author Tomasz Nurkiewicz
 * @since 2010-09-24, 20:48:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnderTest {
}
