package com.textserv.framework.subsystem.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionDescription {
	String name();
	String description() default "";
	DataObjectDescription params();
	DataObjectDescription returns() default @DataObjectDescription();
}
