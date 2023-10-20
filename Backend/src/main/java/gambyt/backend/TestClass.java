package gambyt.backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

/*
    Tester class for client-server interaction.
 */
public class TestClass {

//Left this here as an example of how to use the database
	public static void main(String[] args) throws ParseException, FileNotFoundException, IOException {

//		gambyt.backend.Database d = new gambyt.backend.Database(); // or d = new gambyt.backend.Database(path)
//		d.readJSON("src/JSON_TEST.json"); //Not needed if gambyt.backend.Database(path) was used
//
//		d.printTickets(); //Displays Tickets
//		d.printMessages(); //Displays Messages
//
//		d.saveJSON("src/JSON_TEST.json"); //Writes database

        try {
            //System.setSecurity(new RMISecurityManager());
            //System.setSecurityManager(new RMISecurityManager());
            //String url = "rmi://localhost/";
            Registry registry = LocateRegistry.getRegistry();
            RemoteFrontend server = (RemoteFrontend) registry.lookup("FrontendImpl");
            //HashMap<String, gambyt.backend.Ticket> ticks = server.getAllTickets();
            server.getDatabase().printTickets();
            Ticket ticket = new Ticket();
            ticket.name = "Dynamic Ticket";
            ticket.assignee = 20;
            ticket.dateAssigned = "2023-02-04";
            ticket.description = "This ticket was updated!";
            ticket.priority = 0;
            ticket.status = 1;
            ticket.subscribers = new ArrayList<String>(Arrays.asList("1","2","3","20"));
            server.updateTicket("2", ticket);
            server.getDatabase().printTickets();
        }
        catch (Exception e) {
            System.out.println("Exception occurred while running client: " + e.toString());
            e.printStackTrace();
        }

	}
}
