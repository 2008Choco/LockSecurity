package wtf.choco.locksecurity.api.exception;

import wtf.choco.locksecurity.data.LockedBlock;

/**
 * An exception thrown by {@link LockedBlock}s to indicate that a block
 * is in an invalid position to be a secondary component
 * 
 * @author Parker Hawke - 2008Choco
 */
public class IllegalBlockPositionException extends RuntimeException {
	
	private static final long serialVersionUID = 8642960326014749376L;
	
	public IllegalBlockPositionException(String message) {
		super(message);
	}
	
}