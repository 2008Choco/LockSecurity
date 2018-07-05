package wtf.choco.locksecurity.api.json;

import com.google.gson.JsonObject;

/** 
 * Any object that should be serialized as a JSON object should implement 
 * this class to contain {@link #write(JsonObject)} and {@link #read(JsonObject)} 
 * methods for simplicity and ease.
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface JSONSerializable {
	
	/** 
	 * Write information to a JsonObject provided as a parameter. Any
	 * information that should be saved to file should be provided here
	 * 
	 * @param data the JSON data object to modify
	 * @return the modified JSON data object
	 */
	public JsonObject write(JsonObject data);
	
	/** 
	 * Read information from a JsonObject provided as a parameter. Any
	 * information that should be read from file to fields should be
	 * read from this data tag
	 * 
	 * @param data the JSON data object to read
	 * @return true if the reading was successful
	 */
	public boolean read(JsonObject data);
	
}