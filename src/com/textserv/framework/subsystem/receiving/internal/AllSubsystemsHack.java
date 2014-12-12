package com.textserv.framework.subsystem.receiving.internal;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.receiving.SubsystemImpl;

public class AllSubsystemsHack implements FunctionCallImplementator {

	public String describeFuncCall(String functionName) {
		return null;
	}

	public DataObject handleFuncCall(String functionName, DataObject params) throws Exception {
		DataObject returnDataObject = new DataObject();
		returnDataObject.setString("Description", SubsystemImpl.describeAllSubsystems());
		return returnDataObject;
	}

	public DataObject innerHandleFuncCall(String functionName, DataObject params) throws Exception {
		return handleFuncCall(functionName, params);
	}

}
