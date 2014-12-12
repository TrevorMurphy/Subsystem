package com.textserv.framework.subsystem.common;

public class SubsystemHostProcessState {
	private static boolean hostProcessRunning = true;

	public static boolean isHostProcessRunning() {
		return hostProcessRunning;
	}

	public static void setHostProcessRunning(boolean hostProcessRunning) {
		SubsystemHostProcessState.hostProcessRunning = hostProcessRunning;
	}	
}
