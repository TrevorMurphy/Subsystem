package com.textserv.framework.subsystem.calling.internal.base;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.subsystem.calling.Subsystem;
import com.textserv.framework.subsystem.calling.internal.SubsystemFactory;

public class BaseSubsystemFactory implements SubsystemFactory {
	protected static BaseSubsystemFactory theFactory = new BaseSubsystemFactory();
	private static HashMap<String, Subsystem> subsystems = new HashMap<String, Subsystem>();
	private static Object creationLock = new Object();
	private static BaseFunctionCallCaller functionCallCaller = null;
	private static final Log logger = LogFactory.getLog(BaseSubsystemFactory.class.getName());

	public static void setFunctionCallCaller(BaseFunctionCallCaller theFunctionCallCaller) {
		functionCallCaller = theFunctionCallCaller;
	}

	public static BaseSubsystemFactory getInstance() {
		return theFactory;
	}

	public BaseSubsystem createSubsystem(String subsystemName,
			BaseFunctionCallCaller functionCallCaller) {
		return new BaseSubsystem(subsystemName, functionCallCaller);
	}

	public Subsystem getSubsystem(String subsystemName) {
//		logger.debug("BaseSubsystemFactory.getSubsystem"+ subsystemName);
		Subsystem subsystem = subsystems.get(subsystemName);
		if (subsystem == null) {
			synchronized (creationLock) {
				subsystem = subsystems.get(subsystemName);
				if (subsystem == null) {
					subsystem = createSubsystem(subsystemName,
							functionCallCaller);
					subsystems.put(subsystemName, subsystem);
				}
			}
		}
		return subsystem;
	}
}
