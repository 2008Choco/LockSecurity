package me.choco.locksecurity.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.choco.locksecurity.LockSecurity;

/** 
 * Utilities related to the reading and writing of JSON objects to and from files
 * 
 * @author Parker Hawke - 2008Choco
 */
public class JSONUtils {
	
	/** 
	 * Read JSON information from a file
	 * 
	 * @param file the file to read from
	 * @return the JsonObject that was read from file. Empty JsonObject if unable to read
	 * 
	 * @throws IllegalArgumentException if the file extension is not .json
	 */
	public static JsonObject readJSON(File file) {
		if (!file.getName().endsWith(".json"))
			throw new IllegalArgumentException("File type provided is not .json extended");
		
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return LockSecurity.GSON.fromJson(reader, JsonObject.class);
		} catch (IOException e) {}
		
		return new JsonObject();
	}
	
	/** 
	 * Write JSON information to a file using {@link Gson}'s pretty print
	 * 
	 * @param file the file to write to
	 * @param data the data to write
	 */
	public static void writeJSON(File file, JsonObject data) {
		try (PrintWriter writer = new PrintWriter(file)) {
			writer.write(LockSecurity.GSON.toJson(data));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}