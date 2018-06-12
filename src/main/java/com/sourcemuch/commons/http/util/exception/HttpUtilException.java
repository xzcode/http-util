package com.sourcemuch.commons.http.util.exception;

/**
 * 通用业务异常
 * 
 * @author zai
 * 2017-03-25 03:37:27
 */
public class HttpUtilException extends RuntimeException {
	private static final long serialVersionUID = -1715907038757557299L;

	
	
	
	public HttpUtilException() {
		super();
	}

	public HttpUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpUtilException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpUtilException(Throwable cause) {
		super(cause);
	}

	public HttpUtilException(String message) {
		super(message);
	}
	
	/**
	 * 修改 fillInStackTrace 不填充线程栈 提高业务异常抛出性能
	 * @return
	 * @author zai
	 * 2017-03-25 03:54:14
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
	public static HttpUtilException throwz(String message) {
		return new HttpUtilException(message);
	}
	
	
	
}
