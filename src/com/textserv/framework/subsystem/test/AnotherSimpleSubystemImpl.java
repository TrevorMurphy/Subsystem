package com.textserv.framework.subsystem.test;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.receiving.SubsystemImpl;

public class AnotherSimpleSubystemImpl extends SubsystemImpl {

	public AnotherSimpleSubystemImpl() {
		super("AnotherSimple");
		registerFunction("TestX");
		registerFunction("TestY");
	}
	@Override
	public DataObject handleFuncCall(String functionName, DataObject params) throws Exception {
		DataObject returnObject = null;
		if ( functionName.equals("TestX")) {
			System.out.println("TestX called");
			returnObject = new DataObject();
			returnObject.setString("Message", "Hello World");
		} else if ( functionName.equals("TestY")) {
			System.out.println("TestY called");
			throw new Exception("Throw an Exception for kicks");
		}
		return returnObject;
	}
}
