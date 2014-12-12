package com.textserv.framework.subsystem.common;

public class InProcessFunctionCallHanderSingleton {
	protected static FunctionCallHandler inProcessHandler = null;
	
	public static void setInProcessHandler(FunctionCallHandler theHandler ) {
		inProcessHandler = theHandler;
	}
	
	public static FunctionCallHandler getInProcessHandler() {
		return inProcessHandler;
	}
}
