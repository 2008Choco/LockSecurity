package me.choco.LSaddon.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigAccessor {
	
	private final String fileName;
	private final JavaPlugin plugin;
	
	private File configFile;
	private FileConfiguration fileConfiguration;
	
	public ConfigAccessor(JavaPlugin plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.configFile = new File(plugin.getDataFolder(), fileName);
	}
	
	public ConfigAccessor(JavaPlugin plugin, String path, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.configFile = new File(path, fileName);
	}
	
	public void reloadConfig() {
		this.fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
		
		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource(fileName);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
			this.fileConfiguration.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		if (fileConfiguration == null)
			this.reloadConfig();
		return fileConfiguration;
	}
	
	public void saveConfig() {
		if (fileConfiguration == null || configFile == null) {
			return;
		}
		
		try {
			this.getConfig().save(configFile);
		} catch (IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}
	
	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			this.plugin.saveResource(fileName, false);
		}
	}
	
	public void loadConfig() {
		this.fileConfiguration = getConfig();
		this.fileConfiguration.options().copyDefaults(true);
		
		if (new File(plugin.getDataFolder() + "/" + fileName).exists()) {
			System.out.println("[" + plugin.getName() + "] " + fileName + " loaded.");
		} else {
			this.saveDefaultConfig();
			System.out.println("[" + plugin.getName() + "] " + fileName + " created and loaded.");
		}
	}
	
}