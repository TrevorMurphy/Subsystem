package com.textserv.framework.subsystem.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)

public @interface AttributeDescription {
	public enum DataType { DATAOBJECT, STRING, INT, LONG, DOUBLE, FLOAT, SHORT, CHAR, BYTE, BINARY_STREAM, STRINGL_LIST, DATAOBJECT_LIST };
	public enum Policy { MANDATORY, OPTIONAL };
	
	String 		name();
	String 		description() default "";
	DataType 	type();	
	Policy		policy() default Policy.OPTIONAL;
}
