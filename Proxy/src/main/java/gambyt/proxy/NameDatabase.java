package gambyt.proxy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap; // import the HashMap class

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.Iterator;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NameDatabase {

	private HashMap<String,String> names; //Key = id, Value = Name 
	
	/**
	 * Constructor. Creates name database from JSON.
	 * @param path : The path to the JSON file
	 */
	public NameDatabase(String path) {
		this.names = new HashMap<String,String>();
		try {
			readJSON(path);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Populates database with contents of JSON file.
	 * @param path : The path to the JSON file
	 * @throws ParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readJSON(String path) throws ParseException, FileNotFoundException, IOException{
		JSONParser parser = new JSONParser(); //Creates JSON Parser
		Reader reader = new FileReader(path); //Creates file reader which points to path
		
		JSONObject JObj= (JSONObject) parser.parse(reader); //Attempts to parse JSON
		
		reader.close(); //Closes reader
		
		for(Object uID : JObj.keySet()) {
			this.names.put((String) uID, (String) JObj.get(uID));
		}
	}
	
	/**
	 * Prints contents of name database to console
	 */
	public void printContents() {
		for(String uID : this.names.keySet()) {
			System.out.println(uID + " " + this.names.get(uID));
		}
	}
	
	
	/**
	 * Gets the name of a user from their id.
	 * @param uID : User ID as a String
	 * @return : Name as a String or null if no id found
	 */
	public String getName(String uID){
		return this.names.get(uID);
	}
	
	/**
	 * For retrieving entire database contents to iterate through
	 * @return HashMap key = UID, value = name
	 */
	public HashMap<String,String> getData(){
		return new HashMap<String,String>(this.names);
	}
	
}
