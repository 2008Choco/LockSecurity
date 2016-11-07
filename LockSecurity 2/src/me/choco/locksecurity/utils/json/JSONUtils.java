package me.choco.locksecurity.utils.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Utilities related to the reading and writing of JSON objects to and from files
 * @author Parker Hawke - 2008Choco
 */
public class JSONUtils {
	
	/** Read JSON information from a file
	 * @param file - The file to read from
	 * @return the JSON object that was read from file
	 * @throws IllegalArgumentException if a file type other than .json is provided
	 */
	public static JSONObject readJSON(File file){
		if (!file.getName().endsWith(".json"))
			throw new IllegalArgumentException("File type provided is not .json extended");
		
		try(BufferedReader reader = new BufferedReader(new FileReader(file))){
			StringBuilder jsonRawBuilder = new StringBuilder();
			
			String line;
			while ((line = reader.readLine()) != null)
				jsonRawBuilder.append(line);
			String jsonRaw = jsonRawBuilder.toString();
			
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(jsonRaw);
		}catch(IOException | ParseException e){ e.printStackTrace(); }
		return null;
	}
	
	/** Write JSON information to a file using {@link Gson}'s pretty print
	 * @param file - The file to write to
	 */
	public static void writeJSON(File file, JSONObject data){
		// Clear file information
		try { 
			PrintWriter fileClearer = new PrintWriter(file);
			fileClearer.close();
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		// Rewrite file information
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			JsonParser parser = new JsonParser();
	        JsonObject json = parser.parse(data.toJSONString()).getAsJsonObject();

	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        String prettyJson = gson.toJson(json);
	        
	        writer.write(prettyJson);
		}catch(IOException e){ e.printStackTrace(); }
	}
}