package com.textserv.framework.subsystem.calling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.subsystem.calling.internal.SubsystemFactory;
import com.textserv.framework.subsystem.common.SubsystemException;

public abstract class Subsystem {
	protected static SubsystemFactory factory = null;
	protected String name;
	private static final Log logger = LogFactory.getLog(Subsystem.class.getName());
	
	public static void setFactory(SubsystemFactory theFactory) {
		factory = theFactory;
	}
	
	public static Subsystem getInstance(String subsystemName) {
		Subsystem theSubsystem = null;
		if ( factory != null ) {
			theSubsystem = factory.getSubsystem(subsystemName);
		} else {
			logger.warn("No subsystem factory for " + subsystemName);
		}
		return theSubsystem;
	}
	
	protected Subsystem(String name) {
		this.name = name;
	}
	
	public abstract DataObject callFunc(String functionName, DataObject params) throws SubsystemException;
	public abstract void callFuncAsync(String functionName, DataObject params, FunctionCallback callback) throws SubsystemException;
	public abstract void registerHeartbeatListener(HeartbeatListener listener) throws SubsystemException;
	public abstract void notifyHeartbeatListener(DataObject heartbeat) throws SubsystemException;
}
