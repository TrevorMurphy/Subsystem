package com.textserv.framework.subsystem.common.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.textserv.framework.DataObject;
import com.textserv.framework.DataObjectException;
import com.textserv.framework.subsystem.common.SubsystemException;

public class SubsystemExceptionUtils {
	private static final Log logger = LogFactory.getLog(SubsystemExceptionUtils.class.getName());
	
	public static void throwExceptionIfNeeded( DataObject dataObject ) throws SubsystemException {
		DataObject exception = getException(dataObject);
		if ( exception != null ) {
			throw dataObjectToException(exception);			
		}
	}
	
	public static DataObject getException(DataObject dataObject) {
		DataObject theException = null;
		try {
			if ( dataObject != null ) {
				theException = dataObject.getDataObject(Constants.Exception);
			}
		} catch ( DataObjectException e ) {
			logger.debug("SubsystemExceptionUtils.getException Exception in handling subsystem result",e );				
		}		
		return theException;
	}
	
	public static SubsystemException dataObjectToException( DataObject theException) {
		SubsystemException exception = null;
		if ( theException != null ) {
			try {
				String message = theException.getString(Constants.ExceptionMessage);
				String stackTrace = theException.getString(Constants.StackTrace);
				String exceptionText = theException.getString(Constants.ExceptionText);
				exception = new SubsystemException(message, stackTrace, exceptionText);
			} catch( Exception e ) {
				exception = new SubsystemException("Error converting exception", e);
			}
		} else {
			exception = new SubsystemException("unknown exception");			
		}
		return exception;
	}

	public static DataObject exceptionToDataObject( Throwable theException) {
		SubsystemException exception = new SubsystemException("SubsystemException", theException);
		DataObject doException = new DataObject();
		doException.setString(Constants.ExceptionMessage, exception.getMessage());
		doException.setString(Constants.StackTrace, exception.getNestedStackTrace());
		doException.setString(Constants.ExceptionText, exception.getNestedException());
		
		return doException;
	}
}
