package me.choco.locksecurity.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.choco.locksecurity.LockSecurity;

public class LSConfig {
	
	private static final LockSecurity plugin = LockSecurity.getPlugin();
	
	public static int RECIPE_YIELDS = 1;
	public static boolean ENABLE_NOTIFY_ON_LOGIN = true;
	
	// Category: Griefing
	public static boolean PREVENT_LOCK_EXPLOSIONS = true;
	public static boolean IGNORELOCKS_CAN_BREAK_LOCKS = true;
	public static boolean OWNER_REQUIRES_KEY = true;
	
	// Category: Aesthetics
	public static boolean DISPLAY_LOCKED_SMOKE_PARTICLE = true;
	
	// Category: Vault
	public static boolean IS_VAULT_ENABLED = false; // Special case configuration option
	public static double COST_TO_LOCK = 100.0;
	public static double UNLOCK_REWARD = 75.0;
	public static boolean DISPLAY_WITHDRAW_MSG = true;
	public static boolean DISPLAY_DEPOSIT_MSG = true;
	
	public static void loadValues(){
		FileConfiguration config = plugin.getConfig();
		
		RECIPE_YIELDS = config.getInt("RecipeYields", 1);
		ENABLE_NOTIFY_ON_LOGIN = config.getBoolean("EnableNotifyOnLogin", true);
		
		PREVENT_LOCK_EXPLOSIONS = config.getBoolean("Griefing.PreventLockExplosions", true);
		IGNORELOCKS_CAN_BREAK_LOCKS = config.getBoolean("Griefing.IgnorelocksCanBreakLocks", true);
		OWNER_REQUIRES_KEY = config.getBoolean("Griefing.OwnerRequiresKey", true);
		
		DISPLAY_LOCKED_SMOKE_PARTICLE = config.getBoolean("Aesthetics.DisplayLockedSmokeParticle", true);
		
		IS_VAULT_ENABLED = Bukkit.getPluginManager().getPlugin("Vault") != null;
		COST_TO_LOCK = config.getDouble("Vault.CostToLock");
		UNLOCK_REWARD = config.getDouble("Vault.UnlockReward");
		DISPLAY_WITHDRAW_MSG = config.getBoolean("Vault.DisplayWithdrawMsg");
		DISPLAY_DEPOSIT_MSG = config.getBoolean("Vault.DisplayDepositMsg");
	}
	
	public static int getMaxLocksForWorld(World world){
		return plugin.getConfig().getInt("MaximumLocks." + world.getName(), -1);
	}
}