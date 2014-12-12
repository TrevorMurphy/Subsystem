package com.textserv.framework.subsystem.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SubsystemException extends Exception {

	private static final long serialVersionUID = 1L;
	private String nestedStackTrace = null;
	private String nestedException = null;
	
	public SubsystemException() {
		// TODO Auto-generated constructor stub
	}

	public SubsystemException(String message) {
		super(message);
	}

	public SubsystemException(Throwable cause) {
		super(cause);
		nestedStackTrace = getStackTrace(cause);
		nestedException = cause.toString();
	}

	public SubsystemException(String message, Throwable cause) {
		super(message, cause);
		nestedStackTrace = getStackTrace(cause);
		nestedException = cause.toString();
	}

	public SubsystemException(String message, String stackTrace, String exceptionText) {
		super(message);
		nestedStackTrace = stackTrace;
		nestedException = exceptionText;
	}

	public String getStackTrace(Throwable throwable) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		throwable.printStackTrace(new PrintStream(byteArrayOutputStream));
		return byteArrayOutputStream.toString();
	}
	
	public void printStackTrace(PrintStream printStream) {
		if ( nestedStackTrace != null ) {
			printStream.println(nestedStackTrace);
			printStream.println("-----------------within------------------");
		}
		super.printStackTrace(printStream);

	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		if(nestedException != null) {
			result.append("Nested:").append(System.getProperty("line.separator")).append(nestedException);
		}

		return result.toString();
	}

	public String getNestedException() {
		return nestedException;
	}

	public String getNestedStackTrace() {
		return nestedStackTrace;
	}
	
}
