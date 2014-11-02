package org.juffrou.fx.serials.error;

public class FxPropertyCreationException extends RuntimeException {

	private static final long serialVersionUID = -4908328236816185841L;

	public FxPropertyCreationException() {
		super();
	}

	public FxPropertyCreationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FxPropertyCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FxPropertyCreationException(String message) {
		super(message);
	}

	public FxPropertyCreationException(Throwable cause) {
		super(cause);
	}

	
}
