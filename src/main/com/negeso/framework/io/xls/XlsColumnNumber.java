package com.negeso.framework.io.xls;

import com.negeso.framework.util.Encryption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XlsColumnNumber {
	int value() default 0;
	int[] values() default {};
	boolean required() default false;
	Encryption encription() default Encryption.NO;
}
