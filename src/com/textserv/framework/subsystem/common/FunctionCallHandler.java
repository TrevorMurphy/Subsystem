package com.textserv.framework.subsystem.common;

import com.textserv.framework.DataObject;

public interface FunctionCallHandler {
	public DataObject handleFuncCall(DataObject message);
}
