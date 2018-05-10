package me.choco.locksecurity.utils.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.choco.locksecurity.LockSecurityPlugin;

/**
 * A utility class to assist in update checks through the use of SpiGet
 * 
 * @author Parker Hawke - 2008Choco
 */
public class UpdateChecker {
	
	private boolean requiresUpdate;
	private final String queryURL;
	
	private final Plugin plugin;
	private final int pluginID;
	
	/**
	 * Construct a new UpdateChecker. The update check will not execute until
	 * {@link #queryUpdateCheck()} is invoked.
	 * 
	 * @param plugin the plugin to check
	 * @param pluginID the plugin id (found on Spigot)
	 */
	public UpdateChecker(Plugin plugin, int pluginID) {
		this.plugin = plugin;
		this.pluginID = pluginID;
		this.queryURL = "https://api.spiget.org/v2/resources/" + pluginID + "/versions?sort=-name";
	}
	
	/**
	 * Construct a new UpdateChecker. The update check will not execute until
	 * {@link #queryUpdateCheck()} is invoked.
	 * 
	 * @param plugin the plugin to check
	 * @param pluginName the plugin id (found on Spigot). Must be an integer
	 */
	public UpdateChecker(Plugin plugin, String pluginID) {
		this(plugin, Integer.parseInt(pluginID));
	}
	
	/** 
	 * Get the identification used to determine this plugins information
	 * 
	 * @return the plugin identification
	 */
	public int getPluginID() {
		return pluginID;
	}
	
	/** 
	 * Query an update to the http://www.spiget.org website to retrieve 
	 * new version information
	 * 
	 * @return true if the query was successful, false otherwise
	 */
	public boolean queryUpdateCheck() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(queryURL).openStream()))) {
			
			JsonObject root = LockSecurityPlugin.GSON.fromJson(reader, JsonArray.class).get(0).getAsJsonObject();
			String newestVersion = root.get("name").getAsString();
			String currentVersion = plugin.getDescription().getVersion();
			
			// Version parser
			String[] newValues = newestVersion.split("\\.");
			String[] currentValues = currentVersion.split("\\.");
			
			for (int i = 0; i < (currentValues.length > newValues.length ? currentValues.length : newValues.length); i++) {
				if (i >= newValues.length) {
					this.requiresUpdate = true;
					break;
				} else if (i >= currentValues.length) break;
				
				int newValue = Integer.parseInt(newValues[i]);
				int currentValue = Integer.parseInt(currentValues[i]);
				
				if (newValue < currentValue) break;
				else if (newValue > currentValue) {
					this.requiresUpdate = true;
					break;
				}
			}
			
			return true;
		} catch (IOException | NumberFormatException e) {
			return false;
		}
	}
	
	/** 
	 * Check whether an update is required or not
	 * 
	 * @return true if an update is required
	 */
	public boolean requiresUpdate() {
		return requiresUpdate;
	}
	
}