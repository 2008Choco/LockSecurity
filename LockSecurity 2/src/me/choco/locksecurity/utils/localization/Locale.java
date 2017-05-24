package me.choco.locksecurity.utils.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/** 
 * Assists in the creation of multiple localizations and languages,
 * as well as the generation of default .lang files
 * 
 * @author Parker Hawke - 2008Choco
 */
public class Locale {

	private static JavaPlugin plugin;
	private static final List<Locale> locales = Lists.newArrayList();
	
	private static final Pattern NODE_PATTERN = Pattern.compile("((?:\\w+\\.{1})*(?:\\w+){1})(?:\\s*=\\s*){1}\"(.*)\"");
	
	private static final String LOCALE_FOLDER_PATH = plugin.getDataFolder().getAbsolutePath() + File.separator + "locales";
	private static final String FILE_EXTENSION = ".lang";
	private static final File LOCALE_FOLDER = new File(LOCALE_FOLDER_PATH);
	
	private final Map<String, String> nodes = new HashMap<>();
	
	private final File file;
	private final String name, region;
	
	private Locale(String name, String region) {
		if (plugin == null)
			throw new IllegalStateException("Cannot generate locales without first initializing the class (Locale#init(JavaPlugin))");
		
		this.name = name.toLowerCase();
		this.region = region.toUpperCase();
		
		String fileName = name + "_" + region + FILE_EXTENSION;
		this.file = new File(LOCALE_FOLDER, fileName);
		
		if (this.reloadMessages()) return;
		
		plugin.getLogger().info("Loaded locale " + fileName);
	}
	
	/**
	 * Get the name of the language that this locale is based on
	 * 
	 * @return the name of the language
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the name of the region that this locale is from
	 * 
	 * @return the name of the region
	 */
	public String getRegion() {
		return region;
	}
	
	/**
	 * Get the file that represents this locale
	 * 
	 * @return the locale file (.lang)
	 */
	public File getFile() {
		return file;
	}
	
	/** 
	 * Get a message set for a specific node
	 * 
	 * @param node the node to get
	 * @return the message for the specified node
	 */
	public String getMessage(String node) {
		return this.getMessageOrDefault(node, node);
	}
	
	/** 
	 * Get a message set for a specific node
	 * 
	 * @param node the node to get
	 * @param defaultValue the default value given that a value for the node was not found
	 * 
	 * @return the message for the specified node. Default if none found
	 */
	public String getMessageOrDefault(String node, String defaultValue) {
		return this.nodes.getOrDefault(node, defaultValue);
	}
	
	/**
	 * Get the key-value map of nodes to messages
	 * 
	 * @return node-message map
	 */
	public Map<String, String> getMessageNodeMap() {
		return nodes;
	}
	
	/** 
	 * Clear the previous message cache, and load new messages directly from file
	 * 
	 * @return reload messages from file
	 */
	public boolean reloadMessages() {
		if (!this.file.exists()){
			plugin.getLogger().warning("Could not find file for locale " + this.name);
			return false;
		}
		
		this.nodes.clear(); // Clear previous data (if any)
		
		try(BufferedReader reader = new BufferedReader(new FileReader(file))){
			String line;
			for (int lineNumber = 0; (line = reader.readLine()) != null; lineNumber++){
				if (line.trim().length() == 0 || line.startsWith("#") /* Comment */) continue;
				
				Matcher matcher = NODE_PATTERN.matcher(line);
				if (!matcher.find()){
					System.err.println("Invalid locale syntax at (line=" + lineNumber + ")");
					continue;
				}
				
				String node = matcher.group(1);
				String value = matcher.group(2);
				nodes.put(node, value);
			}
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/** 
	 * Initialize the locale class to generate information and search for localizations
	 * 
	 * @param plugin the plugin instance
	 */
	public static void init(JavaPlugin plugin) {
		Locale.plugin = plugin;
		
		LOCALE_FOLDER.mkdirs();
		Locale.searchForLocales();
	}
	
	/**
	 * Find all .lang file locales under the "locales" folder
	 */
	public static void searchForLocales() {
		if (!LOCALE_FOLDER.exists()) LOCALE_FOLDER.mkdirs();
		
		for (File file : LOCALE_FOLDER.listFiles()){
			String name = file.getName();
			if (!name.endsWith(".lang")) return;
			
			String fileName = name.substring(0, name.lastIndexOf('.'));
			String[] localeValues = fileName.split("_");
			
			if (localeValues.length != 2) continue;
			if (getLocale(localeValues[0] + "_" + localeValues[1]) != null) continue;
			
			locales.add(new Locale(localeValues[0], localeValues[1]));
		}
	}
	
	/**
	 * Get a locale by its entire proper name (i.e. "en_US")
	 * 
	 * @param name the full name of the locale
	 * @return locale of the specified name
	 */
	public static Locale getLocale(String name) {
		for (Locale locale : locales)
			if ((locale.getName() + "_" + locale.getRegion()).equalsIgnoreCase(name)) return locale;
		return null;
	}
	
	/**
	 * Get a locale from the cache by its name (i.e. "en" from "en_US")
	 * 
	 * @param name the name of the language
	 * @return locale of the specified language. Null if not cached
	 */
	public static Locale getLocaleByName(String name) {
		for (Locale locale : locales)
			if (locale.getName().equalsIgnoreCase(name)) return locale;
		return null;
	}
	
	/**
	 * Get a locale from the cache by its region (i.e. "US" from "en_US")
	 * 
	 * @param region the name of the region
	 * @return locale of the specified region. Null if not cached
	 */
	public static Locale getLocaleByRegion(String region) {
		for (Locale locale : locales)
			if (locale.getRegion().equalsIgnoreCase(region)) return locale;
		return null;
	}
	
	/** 
	 * Get an immutable list of all currently loaded locales
	 * 
	 * @return list of all locales
	 */
	public static List<Locale> getLocales() {
		return ImmutableList.copyOf(locales);
	}
	
	/**
	 * Save a default locale file from the project source directory, to the locale folder
	 * 
	 * @param path the path to the file to save
	 * @param fileName the name of the file to save
	 * 
	 * @return true if the operation was successful, false otherwise
	 */
	public static boolean saveDefaultLocale(String path, String fileName) {
		if (!LOCALE_FOLDER.exists()) LOCALE_FOLDER.mkdirs();
		
		if (!fileName.endsWith(".lang"))
			fileName = (fileName.lastIndexOf(".") == -1 ? fileName : fileName.substring(0, fileName.lastIndexOf('.'))) + FILE_EXTENSION;
		
		File destinationFile = new File(LOCALE_FOLDER, fileName);
		if (destinationFile.exists()) return false;
		
		try(OutputStream outputStream = new FileOutputStream(destinationFile)){
			IOUtils.copy(plugin.getResource(fileName), outputStream);
			
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			String[] localeValues = fileName.split("_");
			
			if (localeValues.length != 2) return false;
			
			locales.add(new Locale(localeValues[0], localeValues[1]));
			return true;
		}catch(IOException e){ return false; }
	}
	
	/**
	 * 
	 * Save a default locale file from the project source directory, to the locale folder
	 * 
	 * @param fileName the name of the file to save
	 * @return true if the operation was successful, false otherwise
	 */
	public static boolean saveDefaultLocale(String fileName) {
		return saveDefaultLocale("", fileName);
	}
	
	/**
	 * Clear all current locale data
	 */
	public static void clearLocaleData() {
		for (Locale locale : locales)
			locale.getMessageNodeMap().clear();
		locales.clear();
	}
}