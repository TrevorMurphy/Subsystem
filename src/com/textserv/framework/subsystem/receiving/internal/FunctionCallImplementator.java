package com.textserv.framework.subsystem.receiving.internal;

import com.textserv.framework.DataObject;

public interface FunctionCallImplementator {
	public DataObject innerHandleFuncCall(String functionName, DataObject params) throws Exception;
	public DataObject handleFuncCall(String functionName, DataObject params) throws Exception;
	public String describeFuncCall( String functionName);
}
