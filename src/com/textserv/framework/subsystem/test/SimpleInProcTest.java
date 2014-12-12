package com.textserv.framework.subsystem.test;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.CallingSubsystemService;
import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.receiving.ReceivingSubsystemService;

public class SimpleInProcTest {

	public SimpleInProcTest() {
		//in proc, set up receiver then caller
		
		//The receiving Subsystem service is currently only set up for in process calls
		ReceivingSubsystemService.initialize("SimpleInProcReceiver");

		//The calling Subsystem service is currently only set up for in process calls
		CallingSubsystemService.initialize();
		
		//setup our simple subsystems to receive calls
		new SimpleSubsystemImpl();
		new AnotherSimpleSubystemImpl();
		
		//now make some calls
		DataObject params = new DataObject();
		params.setString("ClientMessage", "Client says Hello");
		try {
			Subsystem.getInstance("Simple").callFunc("TestA", params);
		} catch (Exception e ) {
			e.printStackTrace();
		}		
		try {
			Subsystem.getInstance("AnotherSimple").callFunc("TestX", params);
			Subsystem.getInstance("AnotherSimple").callFunc("TestY", params);
		} catch (Exception e ) {
			e.printStackTrace();
		}		
	}
	public static void main(String[] args) {
		new SimpleInProcTest();
	}
}
