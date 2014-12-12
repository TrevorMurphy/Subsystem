package com.textserv.framework.subsystem.calling.internal.async;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.common.FunctionCallHandler;
import com.textserv.framework.subsystem.common.InProcessFunctionCallHanderSingleton;

//This class used to simulate asyn behaviour in process
//just used for testing really
public class InProcessSubsystemAsyncMessager implements SubsystemAsyncMessager {

	protected FunctionCallHandler fuctionCallHandler = null;
	private HashMap<String, SubsystemAsyncMessagerListener> listeners = new HashMap<String, SubsystemAsyncMessagerListener>();
	private static final Log logger = LogFactory.getLog(InProcessSubsystemAsyncMessager.class.getName());
	
	
	public InProcessSubsystemAsyncMessager() {
		fuctionCallHandler = InProcessFunctionCallHanderSingleton.getInProcessHandler();		
	}
	
	public void registerListener(String uniqueName,SubsystemAsyncMessagerListener listener) {
		listeners.put(uniqueName, listener);
	}

	public void sendMessage(String uniqueName, DataObject message) {
		logger.debug("InProcessSubsystemAsyncMessager.sendMessage sending message: " + message);				
		new Thread(new MessageDeliverer(uniqueName, message)).start();
	}

	public class MessageDeliverer implements Runnable {

		protected String uniqueName;
		protected DataObject message;
		
		public MessageDeliverer(String uniqueName, DataObject message ) {
			this.uniqueName = uniqueName;
			this.message = message;
		}
		
		public void run() {
			DataObject result = fuctionCallHandler.handleFuncCall(message);
			SubsystemAsyncMessagerListener listener = listeners.get(uniqueName);
			if ( listener != null ) {
				listener.onAsyncMessage(result);
			}
		}		
	}
}
