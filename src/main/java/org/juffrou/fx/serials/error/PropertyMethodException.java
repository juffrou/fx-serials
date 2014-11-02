package org.juffrou.fx.serials.error;

public class PropertyMethodException extends RuntimeException {

	private static final long serialVersionUID = -1381933927488883421L;

	public PropertyMethodException() {
		super();
	}

	public PropertyMethodException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PropertyMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyMethodException(String message) {
		super(message);
	}

	public PropertyMethodException(Throwable cause) {
		super(cause);
	}

	
}
