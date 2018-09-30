package wtf.choco.locksecurity.utils.localization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/** 
 * A utility class intended to centralize messages in .lang files. Using the
 * {@link #getMessage(String)} and {@link #sendMessage(CommandSender, String)} methods,
 * configured messages may be sent to players according to the locale file at use.
 * <p>
 * <b>NOTE:</b> Before any functions may be used in the Locale file, {@link #init(JavaPlugin)}
 * must be invoked
 * 
 * @author Parker Hawke - 2008Choco
 */
public final class Locale {
	
	private static final Set<Locale> LOCALES = new HashSet<>();
	private static final Pattern NODE_PATTERN = Pattern.compile("(\\w+(?:\\.{1}\\w+)*)\\s*=\\s*\"(.*)\"");
	private static final String FILE_EXTENSION = ".lang";
	
	private static JavaPlugin plugin;
	private static File localeFolder;
	
	private static String defaultLocale;
	private static BiConsumer<CommandSender, String> defaultMessageFunction = CommandSender::sendMessage;
	
	private final Map<String, String> nodes = new HashMap<>();
	
	private final File file;
	private final String language, region;
	
	private Locale(String language, String region) {
		if (plugin == null)
			throw new IllegalStateException("Cannot generate locales without first initializing the class (Locale#init(JavaPlugin))");
		
		this.language = language.toLowerCase();
		this.region = region.toUpperCase();
		
		String fileName = language + "_" + region + FILE_EXTENSION;
		this.file = new File(localeFolder, fileName);
		
		if (reloadMessages()) return;
		plugin.getLogger().info("Loaded locale " + fileName);
	}
	
	/**
	 * Get the shortened language String identifying this locale (i.e. "en" for English or
	 * "fr" for French)
	 * 
	 * @return the shortened language String
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Get the shortened region String from which this locale is used. (i.e. "US" for
	 * United States or "CA" for Canada)
	 * 
	 * @return the shortened region String
	 */
	public String getRegion() {
		return region;
	}
	
	/**
	 * Return the entire name of the locale (i.e. "en_US")
	 * 
	 * @return the locale's name
	 */
	public String getName() {
		return language + "_" + region;
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
	 * Get a raw message assigned to the specified node. If the provided node has no assigned
	 * value, the default will be returned instead. The returned string will not be formatted
	 * with colours or have any parameters replaced. For formatting, see the
	 * {@link #getMessage(String)} method
	 * 
	 * @param node the node to get
	 * @param defaultValue the default to return if the node had no assigned value
	 * 
	 * @return the raw string message
	 */
	public String getMessageRaw(String node, String defaultValue) {
		return nodes.getOrDefault(node, defaultValue);
	}
	
	/**
	 * Get a raw message assigned to the specified node. The returned string will not be
	 * formatted with colours or have any parameters replaced. For formatting, see the
	 * {@link #getMessage(String)} method
	 * 
	 * @param node the node to get
	 * @return the raw string message
	 */
	public String getMessageRaw(String node) {
		return nodes.get(node);
	}
	
	/** 
	 * Get the localized message assigned to the specified node in the form of a builder
	 * object. If the provided node is not assigned to a value, the default value will be
	 * used instead. It is with this object that parameters may be replaced
	 * ({@link LocalizedMessageBuilder#param(String, String)}) or given colour codes
	 * ({@link LocalizedMessageBuilder#colourize()}) before being returned using the
	 * {@link LocalizedMessageBuilder#get()} method to retrieve the final value
	 * 
	 * @param node the node to get
	 * @param defaultValue the default to return if the node had no assigned value
	 * 
	 * @return the message builder instance
	 */
	public LocalizedMessageBuilder getMessage(String node, String defaultValue) {
		return new LocalizedMessageBuilder(nodes.getOrDefault(node, defaultValue));
	}
	
	/** 
	 * Get the localized message assigned to the specified node in the form of a builder
	 * object. It is with this object that parameters may be replaced
	 * ({@link LocalizedMessageBuilder#param(String, String)}) or given colour codes
	 * ({@link LocalizedMessageBuilder#colourize()}) before being returned using the
	 * {@link LocalizedMessageBuilder#get()} method to retrieve the final value
	 * 
	 * @param node the node to get
	 * @return the message builder instance
	 */
	public LocalizedMessageBuilder getMessage(String node) {
		return getMessage(node, node);
	}
	
	/**
	 * Get the localized message assigned to the specified node in the form of a builder
	 * and provide the option to invoke {@link LocalizedMessageBuilder#send()} and send
	 * the message directly to a CommandSender. If the provided node is not assigned to
	 * a value, the default value will be used instead
	 * 
	 * @param receiver the user to which the message should be sent
	 * @param node the node to get
	 * @param defaultValue the default to return if the node had no assigned value
	 * 
	 * @return the parameterized message builder instance
	 * 
	 * @see #getMessage(String)
	 */
	public LocalizedMessageBuilder getMessage(CommandSender receiver, String node, String defaultValue) {
		return new LocalizedMessageBuilder(getMessageRaw(node, defaultValue), receiver);
	}
	
	/**
	 * Get the localized message assigned to the specified node in the form of a builder
	 * and provide the option to invoke {@link LocalizedMessageBuilder#send()} and send
	 * the message directly to a CommandSender
	 * 
	 * @param receiver the user to which the message should be sent
	 * @param node the node to get
	 * 
	 * @return the parameterized message builder instance
	 * 
	 * @see #getMessage(String)
	 */
	public LocalizedMessageBuilder getMessage(CommandSender receiver, String node) {
		return new LocalizedMessageBuilder(getMessageRaw(node), receiver);
	}
	
	/**
	 * Send a localized message directly to a command sender using the provided message
	 * function. The message associated with the provided node will be retrieved using
	 * {@link #getMessageRaw(String, String)}. If the provided node is not assigned to
	 * a value, the default value will be used instead
	 * <p>
	 * Message functions are used to determine how the message will be sent to the receiver.
	 * Generally {@code CommandSender::sendMessage} is used, though if a custom message
	 * sending function is available (i.e. a method to send messages with a prefix), it
	 * may be specified instead
	 * 
	 * @param receiver the user to which the localized message should be sent
	 * @param node the message node to send
	 * @param defaultValue the default to send if the node had no assigned value
	 * @param messageFunction the function used to send the message to the player
	 */
	public void sendMessage(CommandSender receiver, String node, String defaultValue, BiConsumer<CommandSender, String> messageFunction) {
		Preconditions.checkNotNull(receiver, "Cannot send a message to a null receiver");
		
		String message = getMessageRaw(node, defaultValue);
		(messageFunction != null ? messageFunction : defaultMessageFunction).accept(receiver, message);
	}
	
	/**
	 * Send a localized message directly to a command sender using the provided message
	 * function. The message associated with the provided node will be retrieved using
	 * {@link #getMessageRaw(String, String)}. If the provided node is not assigned to
	 * a value, the default value will be used instead
	 * <p>
	 * The default message function will be used to send the message to the player. To
	 * change the default function, see {@link #setDefaultMessageFunction(BiConsumer)},
	 * or use the {@link #sendMessage(CommandSender, String, String, BiConsumer)} method
	 * to specify which message function should be used
	 * 
	 * @param receiver the user to which the localized message should be sent
	 * @param node the message node to send
	 * @param defaultValue the default to send if the node had no assigned value
	 */
	public void sendMessage(CommandSender receiver, String node, String defaultValue) {
		this.sendMessage(receiver, node, defaultValue, defaultMessageFunction);
	}
	
	/**
	 * Send a localized message directly to a command sender using the provided message
	 * function. The message associated with the provided node will be retrieved using
	 * {@link #getMessageRaw(String, String)}
	 * <p>
	 * Message functions are used to determine how the message will be sent to the receiver.
	 * Generally {@code CommandSender::sendMessage} is used, though if a custom message
	 * sending function is available (i.e. a method to send messages with a prefix), it
	 * may be specified instead
	 * 
	 * @param receiver the user to which the localized message should be sent
	 * @param node the message node to send
	 * @param messageFunction the function used to send the message to the player
	 */
	public void sendMessage(CommandSender receiver, String node, BiConsumer<CommandSender, String> messageFunction) {
		this.sendMessage(receiver, node, node, messageFunction);
	}
	
	/**
	 * Send a localized message directly to a command sender using the provided message
	 * function. The message associated with the provided node will be retrieved using
	 * {@link #getMessageRaw(String, String)}
	 * <p>
	 * The default message function will be used to send the message to the player. To
	 * change the default function, see {@link #setDefaultMessageFunction(BiConsumer)},
	 * or use the {@link #sendMessage(CommandSender, String, String, BiConsumer)} method
	 * to specify which message function should be used
	 * 
	 * @param receiver the user to which the localized message should be sent
	 * @param node the message node to send
	 */
	public void sendMessage(CommandSender receiver, String node) {
		this.sendMessage(receiver, node, node, defaultMessageFunction);
	}
	
	/**
	 * Get the key-value map of nodes to messages
	 * 
	 * @return node-message map
	 */
	public Map<String, String> getMessageNodeMap() {
		return Collections.unmodifiableMap(nodes);
	}
	
	/** 
	 * Clear the previous message cache and load new messages directly from file
	 * 
	 * @return reload messages from file
	 */
	public boolean reloadMessages() {
		if (!file.exists()) {
			plugin.getLogger().warning("Could not find file for locale " + language);
			return false;
		}
		
		this.nodes.clear(); // Clear previous data (if any)
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			for (int lineNumber = 0; (line = reader.readLine()) != null; lineNumber++) {
				if (line.trim().isEmpty() || line.startsWith("#") /* Comment */) continue;
				
				Matcher matcher = NODE_PATTERN.matcher(line);
				if (!matcher.find()) {
					plugin.getLogger().warning("Invalid locale syntax at (line: " + lineNumber + ")");
					continue;
				}
				
				this.nodes.put(matcher.group(1), matcher.group(2));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 31 * language.hashCode();
		result = 31 * result + region.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Locale)) return false;
		
		Locale other = (Locale) object;
		return language.equals(other.language) && region.equals(other.region);
	}
	
	/** 
	 * Initialize the locale class, generate files and folders and optionally search for
	 * localizations. This must be called before any other methods in the Locale class
	 * may be invoked.
	 * 
	 * @param plugin the plugin instance
	 * @param searchForLocales whether or not to search for locales after initialization
	 */
	public static void init(JavaPlugin plugin, boolean searchForLocales) {
		Preconditions.checkNotNull(plugin, "Cannot initialize Locale class with null plugin instance");
		
		Locale.plugin = plugin;
		if (localeFolder == null) {
			localeFolder = new File(plugin.getDataFolder(), "locales/");
		}
		
		localeFolder.mkdirs();
		Locale.searchForLocales();
	}
	
	/**
	 * Initialize the locale class, generate files and folders and search for localizations.
	 * This must be called before any other methods in the Locale class may be invoked. Note
	 * that this will also call {@link #searchForLocales()}, so there is no need to invoke
	 * it for yourself after the initialization
	 * 
	 * @param plugin the plugin instance
	 */
	public static void init(JavaPlugin plugin) {
		init(plugin, true);
	}
	
	/**
	 * Search for all files with the {@code .lang} file extension under the "locales" folder
	 */
	public static void searchForLocales() {
		if (!localeFolder.exists()) localeFolder.mkdirs();
		
		for (File file : localeFolder.listFiles()) {
			String fileName = file.getName();
			if (!fileName.endsWith(".lang")) continue;
			
			String localeName = fileName.substring(0, fileName.lastIndexOf('.'));
			String[] localeValues = localeName.split("_");
			
			if (localeValues.length != 2) {
				plugin.getLogger().warning("Invalid lang file found: \"" + localeName + "\". Expected file name: \"language_COUNTRY\" (i.e. en_US)");
				continue;
			}
			
			if (localeExists(localeValues[0] + "_" + localeValues[1])) continue;
			
			LOCALES.add(new Locale(localeValues[0], localeValues[1]));
			plugin.getLogger().info("Found and loaded locale \"" + localeName + "\"");
		}
	}
	
	/**
	 * Get a locale by its entire proper name (i.e. "en_US")
	 * 
	 * @param name the full name of the locale
	 * @return locale of the specified name
	 */
	public static Locale getLocale(String name) {
		for (Locale locale : LOCALES)
			if (locale.getName().equalsIgnoreCase(name)) return locale;
		return null;
	}
	
	/**
	 * Get an array of locales from the cache whose language are equal to that specified
	 * (i.e. "en" from "en_US")
	 * 
	 * @param language the shortened name of the language
	 * @return locales for the specified language. null if none
	 */
	public static Locale[] getLocaleByLanguage(String language) {
		return LOCALES.stream()
			.filter(l -> l.getLanguage().equalsIgnoreCase(language))
			.toArray(Locale[]::new);
	}
	
	/**
	 * Get an array of locales from the cache whose region are equal to that specified
	 * (i.e. "US" from "en_US")
	 * 
	 * @param region the shortened name of the region
	 * @return locales for the specified region. null if none
	 */
	public static Locale[] getLocaleByRegion(String region) {
		return LOCALES.stream()
			.filter(l -> l.getRegion().equalsIgnoreCase(region))
			.toArray(Locale[]::new);
	}
	
	/**
	 * Check whether a locale exists and is registered or not
	 * 
	 * @param name the whole language tag (i.e. "en_US")
	 * @return true if it exists
	 */
	public static boolean localeExists(String name) {
		for (Locale locale : LOCALES)
			if (locale.getName().equalsIgnoreCase(name)) return true;
		return false;
	}
	
	/** 
	 * Get an immutable list of all currently loaded locales
	 * 
	 * @return list of all locales
	 */
	public static Set<Locale> getLocales() {
		return Collections.unmodifiableSet(LOCALES);
	}
	
	/**
	 * Save a default locale file from the project source directory, to the locale folder
	 * 
	 * @param path the path of the file to save (including the file's name)
	 * @return true if the operation was successful, false otherwise
	 */
	public static boolean saveDefaultLocale(String path) {
		Preconditions.checkArgument(path != null && path.length() >= 1, "Length of path must be greater than or equal to 1");
		if (!localeFolder.exists()) localeFolder.mkdirs();
		
		if (!path.endsWith(FILE_EXTENSION))
			path = (path.lastIndexOf(".") == -1 ? path : path.substring(0, path.lastIndexOf('.'))) + FILE_EXTENSION;
		
		File destinationFile = new File(localeFolder, path);
		if (destinationFile.exists()) {
			return compareFiles(plugin.getResource(path), destinationFile);
		}
		
		try (OutputStream outputStream = new FileOutputStream(destinationFile)) {
			IOUtils.copy(plugin.getResource(path), outputStream);
			
			path = path.substring(0, path.lastIndexOf('.'));
			String[] localeValues = path.split("_");
			
			if (localeValues.length != 2) return false;
			
			LOCALES.add(new Locale(localeValues[0], localeValues[1]));
			if (defaultLocale == null) defaultLocale = path;
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Set the function to be used for {@link LocalizedMessageBuilder#send()}
	 * method invocations given that a BiConsumer was not provided. By default,
	 * this value is set to {@code CommandSender::sendMessage}
	 * 
	 * @param defaultMessageFunction the message function to use by default. Must not be null
	 */
	public static void setDefaultMessageFunction(BiConsumer<CommandSender, String> defaultMessageFunction) {
		Preconditions.checkNotNull(defaultMessageFunction, "Cannot set the message function to null");
		Locale.defaultMessageFunction = defaultMessageFunction;
	}
	
	/**
	 * Clear all current locale data
	 */
	public static void clearLocaleData() {
		LOCALES.forEach(l -> l.nodes.clear());
		LOCALES.clear();
	}
	
	// Write new changes to existing files, if any at all
	private static boolean compareFiles(InputStream defaultFile, File existingFile) {
		// Look for default
		if (defaultFile == null) {
			defaultFile = plugin.getResource(defaultLocale != null ? defaultLocale : "en_US");
			if (defaultFile == null) return false; // No default at all
		}
		
		boolean changed = false;
		
		List<String> defaultLines, existingLines;
		try (BufferedReader defaultReader = new BufferedReader(new InputStreamReader(defaultFile));
				BufferedReader existingReader = new BufferedReader(new FileReader(existingFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(existingFile, true))) {
			defaultLines = defaultReader.lines().collect(Collectors.toList());
			existingLines = existingReader.lines().map(s -> s.split("\\s*=")[0]).collect(Collectors.toList());
			
			for (String defaultValue : defaultLines) {
				if (defaultValue.isEmpty() || defaultValue.startsWith("#")) continue;
				
				String key = defaultValue.split("\\s*=")[0];
				
				if (!existingLines.contains(key)) {
					if (!changed) {
						writer.newLine(); writer.newLine();
						writer.write("# New messages for " + plugin.getName() + " v" + plugin.getDescription().getVersion());
					}
					
					writer.newLine();
					writer.write(defaultValue);
					
					changed = true;
				}
			}
		} catch (IOException e) {
			return false;
		}
		
		return changed;
	}
	
	
	/**
	 * Represents a message builder used to replace parameters with various different
	 * values throughout the message. Internally, the {@link #param(String, String)}
	 * method (and its overloading sister methods) will invoke
	 * {@link String#replace(CharSequence, CharSequence)} in order to replace
	 * parameters in the message.
	 * <p>
	 * Upon completion of the parameter replacement, calling {@link #get()} will return
	 * the parameterized message
	 */
	public final class LocalizedMessageBuilder {
		
		private String message;
		private final CommandSender receiver;
		
		private LocalizedMessageBuilder(String message, CommandSender receiver) {
			this.message = message;
			this.receiver = receiver;
		}
		
		private LocalizedMessageBuilder(String message) {
			this(message, null);
		}
		
		/**
		 * Replace all colour codes in this message using
		 * {@link ChatColor#translateAlternateColorCodes(char, String)}
		 * 
		 * @param prefix the colour prefix to replace
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder colourize(char prefix) {
			this.message = ChatColor.translateAlternateColorCodes(prefix, message);
			return this;
		}
		
		/**
		 * Replace all colour codes in this message using
		 * {@link ChatColor#translateAlternateColorCodes(char, String)} and the common
		 * colour prefix, '&'. This is similar to invoking {@code colourize('&')}
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder colourize() {
			return colourize('&');
		}
		
		/**
		 * Replace the specified parameter with a String value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, String value) {
			this.message = message.replace(key, value);
			return this;
		}
		
		/**
		 * Replace the specified parameter with an int value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, int value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a double value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, double value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a float value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, float value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a long value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, long value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a short value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, short value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a byte value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, byte value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with a boolean value
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, boolean value) {
			return param(key, String.valueOf(value));
		}
		
		/**
		 * Replace the specified parameter with an Object value. The provided object
		 * will invoke {@link Object#toString()}, therefore null is not supported
		 * 
		 * @param key the parameter key (includes surrounding symbols. i.e. {@code "%param%"})
		 * @param value the value with which to replace the parameter
		 * 
		 * @return this instance. Allows for chained method calls
		 */
		public LocalizedMessageBuilder param(String key, Object value) {
			return (value != null) ? param(key, value.toString()) : this;
		}
		
		/**
		 * Get the final localized message, ready to be sent to players
		 * 
		 * @return the localized message
		 */
		public String get() {
			return message;
		}
		
		/**
		 * Send the final localized message directly to a player (or CommandSender)
		 * 
		 * @param messageFunction the function used to send the message. Generally
		 * {@code CommandSender::sendMessage}. For that, see {@link #send()}. Otherwise,
		 * if a custom message implementation is given, use it instead
		 */
		public void send(BiConsumer<CommandSender, String> messageFunction) {
			Preconditions.checkNotNull(receiver, "ParameterizedMessageBuilder not constructed with message receiver. Cannot send message");
			messageFunction.accept(receiver, message);
		}
		
		/**
		 * Send the final parameterized message directly to a player (or CommandSender).
		 * This is similar to invoking {@link #send(BiConsumer)} with the default message
		 * function as the parameter (see {@link Locale#setDefaultMessageFunction(BiConsumer)})
		 */
		public void send() {
			this.send(defaultMessageFunction);
		}
		
	}
	
}