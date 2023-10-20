package gambyt.proxy;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import gambyt.backend.RemoteFrontend;
import gambyt.backend.Ticket;
import gambyt.backend.Database;

/*
    Class for proxy to interact with backend server as a client (using RMI).
 */
public class ProxyToServer {
    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            RemoteFrontend server = (RemoteFrontend) registry.lookup("FrontendImpl");
            server.getDatabase().printTickets();
        }
        catch (Exception e) {
            System.out.println("Exception occurred while running client: " + e.toString());
            e.printStackTrace();
        }
    }
}
