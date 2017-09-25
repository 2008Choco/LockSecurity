package me.choco.locksecurity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import me.choco.locksecurity.api.LockedBlock;
import me.choco.locksecurity.utils.LSPlayer;

/**
 * Contains a few methods to assist in the transferring of information from one
 * data source to the new JSON data source in LockSecurity 2.0.0+
 * 
 * @author Parker Hawke - 2008Choco
 */
public final class TransferUtils {
	
	protected static final void fromDatabase(LockSecurity plugin) {
		plugin.getLogger().info("Commencing transfer process for Data Support of LockSecurity 1.7.0 - 1.8.2");
		
		int nextLockID = 1, nextKeyID = 1;

		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:plugins/LockSecurity/lockinfo.db");
			Statement retrievalStatement = connection.createStatement();
			
			ResultSet result = retrievalStatement.executeQuery("SELECT * FROM LockedBlocks");
			if (result == null) return;
			
			while (result.next()) {
				int lockID = result.getInt("LockID");
				int keyID = result.getInt("KeyID");
				UUID ownerUUID = UUID.fromString(result.getString("OwnerUUID"));
				
				World world = Bukkit.getWorld(result.getString("LocationWorld"));
				int x = result.getInt("LocationX");
				int y = result.getInt("LocationY");
				int z = result.getInt("LocationZ");
				Location location = new Location(world, x, y, z);
				
				if (!saveNewData(plugin, ownerUUID, location, lockID, keyID)) continue;
				
				// lockID and keyID were stored as highest values in the database... I know.. I'm awful
				nextLockID = Math.max(nextLockID, lockID);
				nextKeyID = Math.max(nextKeyID, keyID);
			}
			
			result.close();
			retrievalStatement.close();
			connection.close();
		} catch (ClassNotFoundException | SQLException e) { e.printStackTrace(); }
		
		try {
			plugin.infoFile.createNewFile();
			FileUtils.write(plugin.infoFile, 
					"nextLockID=" + nextLockID +
					"\nnextKeyID=" + nextKeyID, 
				Charset.defaultCharset());
		} catch (IOException e) {
			plugin.getLogger().info("Could not load key/lock ID to file");
			plugin.infoFile.delete();
		}
		
		plugin.getLogger().info("Transfer process completed! You may now delete the \"lockinfo.db\", or (recommended) keep as a backup"
				+ "in case anything had went awry during the transfer process (i.e. missing data).");
		plugin.getLogger().info("Thank you for using LockSecurity " + plugin.getDescription().getVersion() + ". Enjoy!");
	}
	
	protected static final void fromFile(LockSecurity plugin) {
		plugin.getLogger().info("Commencing transfer process for Data Support of LockSecurity 1.0.0 - 1.6.3");
		
		YamlConfiguration lockedFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "locked.yml"));
		
		if (!plugin.infoFile.exists()) {
			try {
				plugin.infoFile.createNewFile();
				FileUtils.write(plugin.infoFile, 
						"nextLockID=" + lockedFile.getInt("NextLockID") +
						"\nnextKeyID=" + lockedFile.getInt("NextKeyID"), 
					Charset.defaultCharset());
			} catch (IOException e) {
				plugin.getLogger().info("Could not load key/lock ID to file");
				plugin.infoFile.delete();
			}
		}
		
		Set<String> keys = lockedFile.getKeys(false);
		keys.remove("NextLockID"); keys.remove("NextKeyID");
		
		for (String key : keys) {
			int lockID = Integer.parseInt(key);
			int keyID = lockedFile.getInt(key + ".KeyID");
			UUID ownerUUID = UUID.fromString(lockedFile.getString(key + ".OwnerUUID"));
			
			World world = Bukkit.getWorld(lockedFile.getString(key + ".Location.World"));
			double x = lockedFile.getDouble(key + ".Location.X");
			double y = lockedFile.getDouble(key + ".Location.Y");
			double z = lockedFile.getDouble(key + ".Location.Z");
			Location location = new Location(world, x, y, z);
			
			if (!saveNewData(plugin, ownerUUID, location, lockID, keyID)) continue;
		}
		
		plugin.getLogger().info("Transfer process completed! You may now delete the \"locked.yml\", or (recommended) keep as a backup"
				+ "in case anything had went awry during the transfer process (i.e. missing data).");
		plugin.getLogger().info("Thank you for using LockSecurity " + plugin.getDescription().getVersion() + ". Enjoy!");
	}
	
	private static boolean saveNewData(LockSecurity plugin, UUID ownerUUID, Location location, int lockID, int keyID) {
		LSPlayer player = plugin.getPlayerRegistry().registerPlayer(Bukkit.getOfflinePlayer(ownerUUID));
		if (player == null) { // Player has never existed on the server
			plugin.getLogger().warning("Missing player with UUID \"" + ownerUUID + "\". Ignoring...");
			return false;
		}
		
		LockedBlock block = new LockedBlock(player, location, lockID, keyID);
		player.addBlockToOwnership(block);
		
		plugin.getLogger().info("Loaded block at " + location.getWorld().getName() + " " 
				+ location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()
				+ " for user " + player.getPlayer().getName() + " with LockID " + lockID + " and KeyID " + keyID);
		return true;
	}
}