package gambyt.backend;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import gambyt.proxy.ServerNotFoundException;

public interface RemoteFrontend extends Remote {

	public String newTicket(Ticket ticket, String tID) throws RemoteException, ServerNotFoundException;

	public void deleteTicket(String tID) throws RemoteException, ServerNotFoundException;

	public ArrayList<String> getUserInbox(String userID) throws RemoteException, ServerNotFoundException;

	public void updateTicket(String tID, Ticket ticket) throws RemoteException, ServerNotFoundException;

	public void clearUserInbox(String userID) throws RemoteException, ServerNotFoundException;

	public HashMap<String, Ticket> getTicketByUser(String userID) throws RemoteException, ServerNotFoundException;

	public HashMap<String, Ticket> getAllTickets() throws RemoteException, ServerNotFoundException;

	public HashMap<String, Ticket> getAllUnassigned() throws RemoteException, ServerNotFoundException;

	public Ticket getTicket(String tID) throws RemoteException, ServerNotFoundException;

	public String getPathToData() throws RemoteException, ServerNotFoundException;

	public void setPathToData(String pathToData) throws RemoteException, ServerNotFoundException;

	public Database getDatabase() throws RemoteException, ServerNotFoundException;

	public void setDatabase(Database db) throws RemoteException, ServerNotFoundException;

	public int checkStatus() throws RemoteException, ServerNotFoundException;

}
