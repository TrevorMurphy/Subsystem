package com.textserv.framework.subsystem.test;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.receiving.SubsystemImpl;

public class SimpleSubsystemImpl extends SubsystemImpl {

	public SimpleSubsystemImpl() {
		super("Simple");
		registerFunction("TestA");
		registerFunction("TestB");
	}
	@Override
	public DataObject handleFuncCall(String functionName, DataObject params) throws Exception {
		DataObject returnObject = null;
		if ( functionName.equals("TestA")) {
			System.out.println("TestA called");
			returnObject = new DataObject();
			returnObject.setString("Message", "Hello World");
		} else if ( functionName.equals("TestB")) {
			System.out.println("TestB called");
			throw new Exception("Throw an Exception for kicks");
		}
		return returnObject;
	}

	public DataObject registerObjectChangeListener(String objectTypeName, String objectListenerId, DataObject params) throws Exception {
		DataObject returnObject = null;
		return returnObject;
	}

	public void unRegisterObjectChangeListener(String objectTypeName, String objectListenerId) {
	}	
}
