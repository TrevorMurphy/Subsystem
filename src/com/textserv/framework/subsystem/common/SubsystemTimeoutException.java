package com.textserv.framework.subsystem.common;

public class SubsystemTimeoutException extends SubsystemException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5739546687543679569L;

	public SubsystemTimeoutException() {
		super("Timed out waiting for return");
	}
}
