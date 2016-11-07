package me.choco.locksecurity.api.exception;

/** An exeception thrown by the LockedBlock class to indicate that a block
 * is in an invalid position to be a secondary component
 * @author Parker Hawke - 2008Choco
 */
public class IllegalBlockPositionException extends RuntimeException {
	
	public IllegalBlockPositionException() {
		super();
	}
	
	public IllegalBlockPositionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 8642960326014749376L;
}