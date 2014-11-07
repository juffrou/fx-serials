package org.juffrou.fx.serials.error;

public class ObjectIsNotFxProxyException extends RuntimeException {

	private static final long serialVersionUID = 1543141700114753208L;

	public ObjectIsNotFxProxyException() {
		super();
	}

	public ObjectIsNotFxProxyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ObjectIsNotFxProxyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectIsNotFxProxyException(String message) {
		super(message);
	}

	public ObjectIsNotFxProxyException(Throwable cause) {
		super(cause);
	}

	
}
