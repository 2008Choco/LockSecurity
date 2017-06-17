package me.choco.locksecurity.registration;

import java.util.Arrays;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Holds information regarding per-player statistics to award achievements based on
 * their progress in LockSecurity
 * 
 * @author Parker Hawke - 2008Choco
 */
public class StatsHandler {
	
	public final Statistic blocksLocked, blocksUnlocked;
	
	private final Statistic[] stats;
	
	/**
	 * Load information from JSON to the statistic handler. The passed JSON object should
	 * contain keys of all existing statistics. 
	 * 
	 * @param statisticData the statistics data
	 */
	public StatsHandler(JsonObject statisticData) {
		this.stats = new Statistic[] {
			this.blocksLocked = new Statistic("blocks_locked", statisticData.get("blocks_locked"), JsonElement::getAsInt, 0),
			this.blocksUnlocked = new Statistic("blocks_unlocked", statisticData.get("blocks_unlocked"), JsonElement::getAsInt, 0)
		};
	}
	
	/**
	 * Get all statistics handled by the stats manager
	 * 
	 * @return all statistics
	 */
	public Statistic[] getAllStats() {
		return Arrays.copyOf(stats, stats.length);
	}
	
	/**
	 * Convert all statistics to JSON data
	 * 
	 * @return all stats in a JsonObject
	 */
	public JsonObject statsToJson() {
		JsonObject data = new JsonObject();
		data.addProperty(blocksLocked.identifier, blocksLocked.value);
		data.addProperty(blocksUnlocked.identifier, blocksUnlocked.value);
		
		return data;
	}
	
	
	/**
	 * Represents a statistic with an accumulated or defined value
	 * 
	 * @author Parker Hawke - 2008Choco
	 */
	public class Statistic {
		
		private int value;
		private final String identifier;
		
		/**
		 * Construct a new statistic with a value, a unique identifier, and a default value in
		 * the case that the provided value is null
		 * 
		 * @param identifier the unique idenfitier
		 * @param value the value of the statistic
		 * @param defaultValue the default value
		 */
		public Statistic(String identifier, JsonElement value, Function<JsonElement, Integer> gsonFunction, int defaultValue) {
			this.identifier = identifier;
			this.value = (value != null ? gsonFunction.apply(value) : defaultValue);
		}
		
		/**
		 * Construct a new statistic with a default value and a unique identifier
		 * 
		 * @param identifier the unique identifier
		 * @param value the value of the statistic
		 */
		public Statistic(String identifier, int value) {
			this.identifier = identifier;
			this.value = value;
		}
		
		/**
		 * Get the statistic's unique identifier to be stored in JSON
		 * 
		 * @return the statistic identifier
		 */
		public String getIdentifier() {
			return identifier;
		}
		
		/**
		 * Set the new value of the statistic
		 * 
		 * @param value the value to set
		 */
		public void setValue(int value) {
			this.value = value;
		}
		
		/**
		 * Get the current value of the statistic
		 * 
		 * @return the current value
		 */
		public int getValue() {
			return value;
		}
		
	}
	
}