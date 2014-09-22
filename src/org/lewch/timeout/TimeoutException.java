package org.lewch.timeout;

public class TimeoutException extends RuntimeException {
	/*** 序列化号 */
	private static final long serialVersionUID = -8078853655388692688L;

	public TimeoutException(String errMessage) {
		super(errMessage);
	}
}