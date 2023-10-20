package gambyt.backend;


import java.io.Serializable;
import java.util.ArrayList; // import the ArrayList class


/**
 * Object to represent a Ticket
 *
 */
public class Ticket implements Serializable {

	public String name = "";
	public long assignee = -1; //id of user who has been assigned the ticket (-1 means unassigned)
	public String assigneeName = "Unassigned";
	public long status = -1; //0=to-do, 1=in-progress, 2=done
	public ArrayList<String> subscribers = new ArrayList<String>();
	public String description = "";
	public String dateAssigned = "0000-00-00";
	public long priority = -1; //0=low 1=medium 2=high

	
	public void PrintTicketInfo() {
		
		System.out.println(" Name: " + name);
		System.out.println(" assignee: " + assignee);
		System.out.println(" status: " + status);
		
		System.out.println(" Subscribers: ");
		for(int i = 0; i < subscribers.size();i++) {
			System.out.println("    " + subscribers.get(i));
		}
		
		System.out.println(" description: " + description);
		System.out.println(" dateAssigned: " + dateAssigned);
		System.out.println(" priority: " + priority + "\n");
		
	
	}
}
