package gambyt.backend;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap; // import the HashMap class

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.Iterator;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * https://www.digitalocean.com/community/tutorials/json-simple-example
 *
 */
public class Database implements Serializable {
	
	private HashMap<String,Ticket> Tickets;
	private HashMap<String, ArrayList<String>> Inbox;

	
	
	/**
	 * Generic Constructor
	 * Populates database with empty values
	 */
	public Database() {
		this.Tickets = new HashMap<String,Ticket>();
		this.Inbox = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * Copy Constructor
	 * @param newDB
	 */
	public Database(Database newDB) {
		Tickets = newDB.getTickets();
		Inbox = newDB.getInboxes();
	}
	
	/**
	 * Constructor which populates off of a JSON file.
	 * @param path : This is the filepath to the JSON
	 */
	public Database(String path) {
		this.Tickets = new HashMap<String,Ticket>();
		this.Inbox = new HashMap<String, ArrayList<String>>();
		
		try {
			readJSON(path);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all tickets in memory
	 * @return : HashMap of all the tickets (Key = id, value = ticket)
	 */
	public HashMap<String,Ticket> getTickets(){
		return new HashMap<String,Ticket>(this.Tickets);
	}
	
	/**
	 * Gets particular ticket in memory
	 * As of right it is returned by reference as no clone constructor is present in Ticket
	 * @param tID : The id of the ticket as a String
	 * @return : Ticket Object of requested ticket
	 */
	public Ticket getTicket(String tID) {
		return this.Tickets.get(tID);
	}
	
	/**
	 * Gets all inboxes in database
	 * @return : HashMap of all messages (Key = userID, value = ArrayList of messages)
	 */
	public HashMap<String, ArrayList<String>> getInboxes(){
		return new HashMap<String, ArrayList<String>>(this.Inbox);
	}
	
	/**
	 * Gets messages of a particular user
	 * @param uID : Id of user as String
	 * @return : ArrayList of messages
	 */
	public ArrayList<String> getMessages(String uID){
		return new ArrayList<String>(this.Inbox.get(uID));
	}
	
	
	/**
	 * Removes ticket from database
	 * @param tID : The id of the ticket as a String
	 */
	public void removeTicket(String tID) {
		this.Tickets.remove(tID);
	}
	
	
	/**
	 * Removes all messages of particular user
	 * @param uID : Id of user as String
	 */
	public void clearMessages(String uID) {
		this.Inbox.remove(uID);
	}
	
	
	public void replaceTicket(String tID, Ticket t) {
		this.Tickets.replace(tID, t);
	}
	
	 /**
	 * Adds ticket to database
	 * @param tID : The id of the ticket as a String
	 * @param t : Ticket object being added
	 */
	public void addTicket(String tID, Ticket t) {
		this.Tickets.put(tID, t);
	}
	
	/**
	 * Adds new inbox of messages for a particular user
	 * @param uID : Id of user as String
	 * @param messages : The array list of messages being added
	 */
	public void addUserInbox(String uID, ArrayList<String> messages) {
		this.Inbox.put(uID,messages);
	}
	
	/*
	 * Adds message to a particular users inbox
	 * @param uID : Id of user as String
	 * @param message : The message to be added to their inbox
	 */
	public void addMessage(String uID, String message) {
		if(this.Inbox.containsKey(uID)) {
			this.Inbox.get(uID).add(message);
		}else {
			ArrayList<String> m = new ArrayList<String>();
			m.add(message);
			this.Inbox.put(uID, m);
		}
		
	}
	
	/**
	 * Notifies all subscribers a message
	 * @param t : The ticket 
	 * @param Message : The message which is to be added to users inboxes
	 */
	public void notifySubscribers(Ticket t, String Message) {
		for(String sub : t.subscribers) {
			this.addMessage(sub, Message);
		}
	}
	
	/**
	 * Notifies all subscribers a message
	 * @param tID : The ticket ID as a String
	 * @param Message : The message which is to be added to users inboxes
	 */
	public void notifySubscribers(String tID, String Message) {
		Ticket t = this.Tickets.get(tID);
		for(String sub : t.subscribers) {
			this.addMessage(sub, Message);
		}
	}
	
	/**
	 * The function returns if a particular user has messages waiting for them
	 * @param uID : The ID of the user as a String
	 * @return true -> User has messages | false -> User does not have messages
	 */
	public boolean doesUserHaveMessages(String uID) {
		if(this.Inbox.containsKey(uID)) {
			if(Inbox.get(uID).isEmpty()) {
				return false;
			}
			
			return true;
		}else {
			return false;
		}
	}
	
	
	
	/**
	 * Gets a list of Ticket IDs that are assigned to a particular user
	 * @param uID : The id of a user as a String
	 * @return : an ArrayList of Strings representing ticket IDs
	 */
	public ArrayList<String> getTicketsByUser(String uID){
		ArrayList<String> l = new ArrayList<String>();
		
		for(String tID : this.Tickets.keySet()) {
			Ticket t = this.Tickets.get(tID);
			if(String.valueOf(t.assignee).equals(uID)) {
				l.add(tID);
			}
		}
		
		return l;
	}
	
	
	/**
	 * Gets all TicketIDs of unassigned tickets
	 * @return A list of ticketIDs as Strings
	 */
	public ArrayList<String> getTicketsUnassigned(){
		ArrayList<String> l = new ArrayList<String>();
		
		for(String tID : this.Tickets.keySet()) {
			Ticket t = this.Tickets.get(tID);
			if(t.assignee == -1) {
				l.add(tID);
			}
		}
		
		return l;
		
	}
	
	/**
	 * Returns a list of ticket objects
	 * Can be used in conjunction with getTickets functions
	 * @param tIDs : A list of ticket ids
	 * @return : A list of ticket objects
	 */
	public ArrayList<Ticket> getTickets(ArrayList<String> tIDs){
		ArrayList<Ticket> l = new ArrayList<Ticket>();
		
		for(String tID : this.Tickets.keySet()) {
			if(tIDs.contains(tID)) {
				l.add(this.Tickets.get(tID));
			}
		}
		
		return l;
	}
	
	/**
	 * Reads JSON and populates database
	 * NOTE: REPLACES DATABASE IN MEMORY
	 * @param path : The path the to JSON file
	 */
	public void readJSON(String path) throws ParseException, FileNotFoundException, IOException {
		
		JSONParser parser = new JSONParser(); //Creates JSON Parser
		Reader reader = new FileReader(path); //Creates file reader which points to path
		
		JSONObject JObj= (JSONObject) parser.parse(reader); //Attempts to parse JSON
		
		reader.close(); //Closes reader
		
		JSONObject JTickets = (JSONObject) JObj.get("tickets"); //Grabs nested object for tickets
		JSONObject JInbox = (JSONObject) JObj.get("inbox"); //Grabs nested object for inbox messages
		
		
		//First we will read all of the ticket information
		
		//Taken from https://www.tabnine.com/code/java/methods/org.json.simple.JSONObject/keySet
		//Iterates over every Ticket in the JSON and adds it to memory
		for (Iterator iterator = JTickets.keySet().iterator(); iterator.hasNext(); ) {
			
			//Get key and value
			String ticketID = (String) iterator.next();
			JSONObject JTicketObject = (JSONObject) JTickets.get(ticketID);
			
			//populate ticket object
			Ticket t = new Ticket();
			t.name = (String) JTicketObject.get("name");
			t.assignee = (long) JTicketObject.get("assignee");
			t.status = (long) JTicketObject.get("status");
			
			t.subscribers = new ArrayList<String>();
			
			JSONArray subs = (JSONArray) JTicketObject.get("subscribers");
			ArrayList<String> s = new ArrayList<String>();
			
			for(@SuppressWarnings("unchecked")
			Iterator<String> it = subs.iterator();it.hasNext(); ) {
				s.add((String) it.next());
			}
			
			t.subscribers = s;
			
			t.description = (String) JTicketObject.get("description");
			t.dateAssigned = (String) JTicketObject.get("date_assigned");
			t.priority = (long) JTicketObject.get("priority");
			
			
			this.Tickets.put(ticketID, t); //Adds ticket to memory
			
		}
		
		
		//Now that tickets are read, we will read the inbox
		for (Iterator iterator = JInbox.keySet().iterator(); iterator.hasNext(); ) {
			
			//Gets User ID and their messages
			String userID = (String) iterator.next();
			
			JSONArray JMessageList = (JSONArray) JInbox.get(userID);
			
			ArrayList<String> messages = new ArrayList<String>();
			
			//Commits messages to an ArrayList
			for(@SuppressWarnings("unchecked")
			Iterator<String> it = JMessageList.iterator();it.hasNext(); ) {
				messages.add((String) it.next());
			}
			
			this.Inbox.put(userID, messages); //Adds messages to memory		
			
		}	
		
	}
	
	
	
	/**
	 * Saves database in Memory to JSON file
	 * Note: This will replace the currently saved JSON
	 * @param path : The path which the JSON is written to
	 */
	@SuppressWarnings("unchecked")
	public void saveJSON(String path) throws IOException {	

		FileWriter file = new FileWriter(path); //Opens writer first
		
		JSONObject mainObject = new JSONObject();
		JSONObject ticketsObject = new JSONObject();
		JSONObject inboxesObject = new JSONObject();
		
		//Populate Tickets
		for(String ticketID : this.Tickets.keySet()) {
			JSONObject tObject = new JSONObject(); //Creates JSON Object for particular ticket
			
			Ticket t = this.Tickets.get(ticketID);
			
			tObject.put("name", t.name);
			tObject.put("assignee", t.assignee);
			tObject.put("status", t.status);
			
			
			JSONArray subs = new JSONArray();
			for(int i = 0; i < t.subscribers.size(); i++) {
				subs.add(t.subscribers.get(i));
			}
			
			tObject.put("subscribers", subs);
			
			
			
			tObject.put("description", t.description);
			tObject.put("date_assigned", t.dateAssigned);
			tObject.put("priority", t.priority);
			
			ticketsObject.put(ticketID, tObject); //Puts ticket in JSON Object
		}
		
		//Populate Inbox
		for(String uID : this.Inbox.keySet()) {
			JSONArray iArray = new JSONArray(); //Creates JSON Array for particular inbox
			
			for(int i = 0; i < this.Inbox.get(uID).size(); i++) {
				iArray.add(this.Inbox.get(uID).get(i));
			}
			
			inboxesObject.put(uID, iArray); //Adds user and their messages to JSON object
			
		}
		
		//Put together and write
		mainObject.put("tickets", ticketsObject);
		mainObject.put("inbox", inboxesObject);
		
		file.write(mainObject.toJSONString());
		file.flush();
		file.close();
		
		
		
	}
	
	
	/**
	 * Prints all the tickets within memory
	 */
	public void printTickets() {
		for (String key : this.Tickets.keySet()) {
		    System.out.println("Ticket ID: " + key);
		    this.Tickets.get(key).PrintTicketInfo();
		}
	}
	
	/**
	 * Prints all the messages within memory
	 */
	public void printMessages() {
		for(String user : this.Inbox.keySet()) {
			System.out.println("User: " + user);
			System.out.println(" Messages: ");
			
			int i = 0;
			for(String message : this.Inbox.get(user)) {
				System.out.println("   [" + i + "]:  " + message);
				i++;
			}
			
			System.out.println(""); //Newline
		}
	}
}
