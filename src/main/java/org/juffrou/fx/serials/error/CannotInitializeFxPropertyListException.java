package org.juffrou.fx.serials.error;

public class CannotInitializeFxPropertyListException extends RuntimeException {

	private static final long serialVersionUID = -4908328236816185841L;

	public CannotInitializeFxPropertyListException() {
		super();
	}

	public CannotInitializeFxPropertyListException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CannotInitializeFxPropertyListException(String message, Throwable cause) {
		super(message, cause);
	}

	public CannotInitializeFxPropertyListException(String message) {
		super(message);
	}

	public CannotInitializeFxPropertyListException(Throwable cause) {
		super(cause);
	}

	
}
