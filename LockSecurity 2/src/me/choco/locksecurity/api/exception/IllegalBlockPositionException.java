package me.choco.locksecurity.api.exception;

import me.choco.locksecurity.api.LockedBlock;

/** 
 * An exeception thrown by {@link LockedBlock}s to indicate that a block
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