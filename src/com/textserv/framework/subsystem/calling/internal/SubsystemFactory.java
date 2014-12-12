package com.textserv.framework.subsystem.calling.internal;

import com.textserv.framework.subsystem.calling.Subsystem;

public interface SubsystemFactory {
	public Subsystem getSubsystem(String subsystemName);
}
