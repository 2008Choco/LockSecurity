package me.choco.locksecurity.utils;

import org.bukkit.ChatColor;

import me.choco.locksecurity.LockSecurity;

public enum LSMessage {
	
	COMMANDS_FORGEKEY_NOIDSPECIFIED("Commands.Forgekey.NoIdSpecified"),
	COMMANDS_FORGEKEY_GIVENKEY("Commands.Forgekey.GivenKey"),
	
	COMMANDS_GIVEKEY_RECEIVEDUNSMITHEDKEY("Commands.Givekey.ReceivedUnsmithedKey"),
	COMMANDS_GIVEKEY_TARGETPLAYERRECEIVEDKEY("Commands.Givekey.TargetPlayerReceivedKey"),
	COMMANDS_GIVEKEY_PLAYERSENTKEY("Commands.Givekey.PlayerSentKey"),

	COMMANDS_IGNORELOCKS_IGNORINGLOCKS("Commands.Ignorelocks.IgnoringLocks"),
	COMMANDS_IGNORELOCKS_NOLONGERIGNORINGLOCKS("Commands.Ignorelocks.NoLongerIgnoringLocks"),

	COMMANDS_LOCKINSPECT_LOCKINSPECTENABLED("Commands.Lockinspect.LockInspectEnabled"),
	COMMANDS_LOCKINSPECT_LOCKINSPECTDISABLED("Commands.Lockinspect.LockInspectDisabled"),
	COMMANDS_LOCKINSPECT_BLOCKNOTLOCKED("Commands.Lockinspect.BlockNotLocked"),

	COMMANDS_LOCKLIST_LISTIDENTIFIER("Commands.Locklist.ListIdentifier"),

	COMMANDS_LOCKSECURITY_SUCCESSFULLYRELOADED("Commands.Locksecurity.SuccessfullyReloaded"),

	COMMANDS_UNLOCK_UNLOCKMODEENABLED("Commands.Unlock.UnlockModeEnabled"),
	COMMANDS_UNLOCK_UNLOCKMODEDISABLED("Commands.Unlock.UnlockModeDisabled"),
	COMMANDS_UNLOCK_BLOCKUNLOCKED("Commands.Unlock.BlockUnlocked"),
	COMMANDS_UNLOCK_NOTOWNER("Commands.Unlock.NotOwner"),
	COMMANDS_UNLOCK_BLOCKNOTLOCKED("Commands.Unlock.BlockNotLocked"),

	COMMANDS_TRANSFERLOCK_TRANSFERMODEENABLED("Commands.Transferlock.TransferModeEnabled"),
	COMMANDS_TRANSFERLOCK_TRANSFERMODEDISABLED("Commands.Transferlock.TransferModeDisabled"),
	COMMANDS_TRANSFERLOCK_TRANSFERREDBLOCK("Commands.Transferlock.TransferredBlock"),

	COMMANDS_LOCKNOTIFY_LOCKNOTIFYENABLED("Commands.Locknotify.LockNotifyEnabled"),
	COMMANDS_LOCKNOTIFY_LOCKNOTIFYDISABLED("Commands.Locknotify.LockNotifyDisabled"),
	COMMANDS_LOCKNOTIFY_LOCKNOTIFICATION("Commands.Locknotify.LockNotification"),

	COMMANDS_GENERAL_ONLYPLAYERS("Commands.General.OnlyPlayers"),
	COMMANDS_GENERAL_NOPERMISSION("Commands.General.NoPermission"),
	COMMANDS_GENERAL_PLAYEROFFLINE("Commands.General.PlayerOffline"),
	COMMANDS_GENERAL_MUSTSPECIFYPLAYER("Commands.General.MustSpecifyPlayer"),
	COMMANDS_GENERAL_INVALIDINTEGER("Commands.General.InvalidInteger"),
	COMMANDS_GENERAL_PLAYERNEVERPLAYEDBEFORE("Commands.General.PlayerNeverPlayedBefore"),
	
	EVENTS_BLOCKUNREGISTERED("Events.BlockUnregistered"),
	EVENTS_CANNOTOPEN("Events.CannotOpen"),
	EVENTS_SUCCESSFULLYLOCKEDBLOCK("Events.SuccessfullyLockedBlock"),
	EVENTS_NOPERMISSIONTOLOCK("Events.NoPermissionToLock"),
	EVENTS_NOKEY("Events.NoKey"),
	EVENTS_LOCKPICKATTEMPT("Events.LockPickAttempt"),
	EVENTS_CANNOTBREAK("Events.CannotBreak"),
	EVENTS_REACHEDLOCKMAXIMUM("Events.ReachedLockMaximum"),
	EVENTS_NOTENOUGHMONEY("Events.NotEnoughMoney"),
	EVENTS_BALANCEWITHDRAWN("Events.BalanceWithdrawn"),
	EVENTS_BALANCEDEPOSITED("Events.BalanceDeposited"),
	EVENTS_DISALLOWEDACTION("Events.DisallowedAction");
	
	private static final LockSecurity plugin = LockSecurity.getPlugin();
	
	private final String path;
	private final String[] replaceable;
	private LSMessage(String path, String... replaceable) {
		this.path = path;
		this.replaceable = replaceable;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getMessage(){
		return plugin.messages.getConfig().getString(path, ChatColor.RED + "%invalid message path, \"" + path + "\"%");
	}
	
	public String withValues(String... replacements){
		String message = getMessage();
		if (this.replaceable.length == 0) return message;
		
		int replacementLength = replacements.length;
		for (int i = 0; i < this.replaceable.length; i++){
			if (i >= replacementLength) break;
			message = message.replace(this.replaceable[i], replacements[i]);
		}
		
		return message;
	}
}