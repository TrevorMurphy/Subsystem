package com.textserv.framework.subsystem.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)

public @interface DataObjectDescription {
	String name() default "";
	String description() default "";
	AttributeDescription[] attibutes() default {};
}
