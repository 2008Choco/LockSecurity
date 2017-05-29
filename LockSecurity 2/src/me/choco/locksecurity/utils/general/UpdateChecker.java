package me.choco.locksecurity.utils.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UpdateChecker {
	
	private boolean requiresUpdate;
	private final String queryURL;

	private final Plugin plugin;
	private final String pluginID;
	
	public UpdateChecker(Plugin plugin, String pluginName) {
		this.plugin = plugin;
		this.pluginID = pluginName;
		this.queryURL = "https://api.spiget.org/v2/resources/" + pluginID + "/versions?sort=-name";
	}
	
	public UpdateChecker(Plugin plugin, int pluginID) {
		this(plugin, String.valueOf(pluginID));
	}
	
	/** 
	 * Get the identification used to determine this plugins information
	 * 
	 * @return the plugin identification
	 */
	public String getPluginID() {
		return pluginID;
	}
	
	/** 
	 * Query an update to the http://www.spiget.org website to retrieve 
	 * new version information
	 * 
	 * @return true if the query was successfully
	 */
	public boolean queryUpdateCheck() {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(queryURL).openStream()))) {
			
			StringBuilder jsonRaw = new StringBuilder();
			String currentLine;
			while ((currentLine = reader.readLine()) != null)
				jsonRaw.append(currentLine);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) ((JSONArray) jsonParser.parse(jsonRaw.toString())).get(0);
			
			String newestVersion = (String) json.get("name");
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
		} catch (IOException | ParseException | NumberFormatException e) { return false; }
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