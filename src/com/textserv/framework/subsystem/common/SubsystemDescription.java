package com.textserv.framework.subsystem.common;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SubsystemDescription {
	String subsysytemName();
	FunctionDescription[] functions();
}
