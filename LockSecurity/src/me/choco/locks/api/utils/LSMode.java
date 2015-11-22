package me.choco.locks.api.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

/** The enumeration to determine which mode the player is in */
public enum LSMode {
	IGNORE_LOCKS, INSPECT_LOCKS, UNLOCK, TRANSFER_LOCK, DEFAULT;
	
	public static HashMap<String, LSMode> modeHandler = new HashMap<String, LSMode>();
	
	/** Get the mode the player is currently in
	 * @param player - The player to check
	 * @return LSMode - The mode the player is in
	 */
	public static LSMode getMode(Player player){
		if (modeHandler.get(player.getName()) == null)
			modeHandler.put(player.getName(), LSMode.DEFAULT);
		return modeHandler.get(player.getName());
	}
	
	/** Set the Player's mode
	 * @param player - The player to change modes
	 * @param mode - The mode to set the player
	 */
	public static void setMode(Player player, LSMode mode){modeHandler.put(player.getName(), mode);}
	
	/**Used to prevent memory leaks. DO NOT USE*/
	public static void clearAllModes(){modeHandler.clear();}
}