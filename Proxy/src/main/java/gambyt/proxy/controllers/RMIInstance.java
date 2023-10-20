package gambyt.proxy.controllers;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.lang.*;

import gambyt.backend.Database;
import gambyt.backend.RemoteFrontend;
import gambyt.backend.Server;
import gambyt.backend.Ticket;
import gambyt.proxy.ServerNotFoundException;

public class RMIInstance implements RemoteFrontend {
	private static ArrayList<RemoteFrontend> INSTANCES;
	private static ArrayList<String> IPS;
	private static RemoteFrontend SELF;
	private static int INDEX = 0;

	private static ArrayBlockingQueue<Request> Q;
	private static QueueThread QThread;

	static {
		INSTANCES = new ArrayList<RemoteFrontend>();
		IPS = new ArrayList<String>();
		Q = new ArrayBlockingQueue<Request>(100);
		QThread = new QueueThread(Q);
	}

	/**
	 * Initialize Proxy with servers at first startup
	 */
	public static void initRMI() {
		System.setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
		Request.InitBroadcast(INSTANCES, IPS);
		SELF = new RMIInstance();
		QThread.start();
		System.out.println("RMI Init Complete");
	}

	/**
	 * Designate the server to be used as the source of truth
	 * 
	 * @param ip IP address of the registering server
	 */
	public static void registerFirstInstance(String ip) {
		String url = "rmi://" + ip + '/';
		try {
			System.out.println("Registering " + ip + " as primary replica");
			RemoteFrontend newInstance = (RemoteFrontend) Naming.lookup(url + "FrontendImpl");
			INSTANCES.add(newInstance);
			IPS.add(ip);
			System.out.println('\t' + ip + " (success): " + newInstance.getPathToData());
		} catch (Exception e) {
			System.out.println("IP: " + ip);
			System.out.println("Exception occurred initiating RMI: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Gets the associated RMI object for a given IP.
	 * 
	 * @param ip IP address of the registering server
	 * @return A reference to the server's RMI object
	 * @throws RemoteException thrown when initiation fails
	 */
	public static RemoteFrontend registerNewInstance(String ip) throws RemoteException {
		String url = "rmi://" + ip + '/';
		try {
			RemoteFrontend newInstance = (RemoteFrontend) Naming.lookup(url + "FrontendImpl");
			return newInstance;
		} catch (Exception e) {
			System.out.println("IP: " + ip);
			System.out.println("Exception occurred initiating RMI: " + e.toString());
			throw new RemoteException(e.toString());
		}
	}

	/**
	 * Add a given RMI instance and IP to the Round-Robin rotation.
	 * 
	 * @param inst RMI object reference of the registering server
	 * @param ip   IP address of the registering server
	 * @throws RemoteException thrown when a connection is not present
	 */
	public static void addInstanceToRotation(RemoteFrontend inst, String ip) throws RemoteException {
		INSTANCES.add(inst);
		IPS.add(ip);
		try {
			System.out.println('\t' + ip + " (success): " + inst.getPathToData());
		} catch (ServerNotFoundException e) {
			e.printStackTrace();
			System.err.println("should not happen");
		}
	}

	/**
	 * Gets the singleton instance of this class.
	 * 
	 * @return instance of the implemented interface used for queuing.
	 */
	public static RemoteFrontend getInstance() {
		return SELF;
	}

	/**
	 * Get a singular RMI object reference in a Round-Robin order. This performs a
	 * status check to the server.
	 * 
	 * @return an RMI initiated reference to a backend server object
	 * @throws ServerNotFoundException thrown when no servers are present
	 */
	private static synchronized RemoteFrontend getBackendRR() throws ServerNotFoundException {
		while (instanceExists()) {
			INDEX = INDEX % INSTANCES.size();
//			if (INDEX >= INSTANCES.size())
//				INDEX = 0;
			System.out.println(INDEX + ": " + IPS.get(INDEX));
			try {
				INSTANCES.get(INDEX).checkStatus();
				return INSTANCES.get(INDEX++);
			} catch (RemoteException e) {
				System.out.println("Remote Server " + IPS.get(INDEX) + " is not responding. Dropping it from list...");
				INSTANCES.remove(INDEX);
				IPS.remove(INDEX);
			}
		}
		throw new ServerNotFoundException("No more servers, please wait for another server to connect");
	}

	// Check if there are any backend servers
	public static boolean instanceExists() {
		return INSTANCES.size() > 0;
	}

	// Check if a given ip exists in the rotation
	public static boolean ipInRotation(String ip) {
		return IPS.contains(ip);
	}

	// Remove a given server from the rotation
	public static void removeServer(String ip) {
		int i = IPS.indexOf(ip);
		IPS.remove(i);
		INSTANCES.remove(i);
	}

	/**
	 * Request creator for the RemoteFrontend interface. A request is instantiated
	 * with the RemoteSupplier object and later fulfilled by the Queue Daemon
	 * Thread. <b>This is a blocking call.</b>
	 * 
	 * @param <R> Generic parameter for the type of the retrieved value
	 * @param sf  Supplier function to be fulfilled by the Queue Daemon Thread
	 * @return The requested value upon fulfillment.
	 * @throws RemoteException         thrown when the remote call to the server
	 *                                 fails
	 * @throws ServerNotFoundException thrown when no servers are present
	 */
	private <R> R queueAndBlock(boolean write, RemoteFunction<R> sf) throws RemoteException, ServerNotFoundException {
		if (!instanceExists()) {
			throw new ServerNotFoundException("No more servers, please wait for another server to connect");
		}
		Request<R> r = new Request<R>(sf, getBackendRR(), write);
		try {
			while (!Q.offer(r, 1000, TimeUnit.MILLISECONDS))
				;
			return r.get();
		} catch (InterruptedException | ExecutionException e) {
			System.err.println(e);
		}
		throw new RuntimeException("no idea how we got here");
	}

	// each call blocks until its released from Q
	// Write
	@Override
	public String newTicket(Ticket ticket, String tID) throws RemoteException, ServerNotFoundException {
		return queueAndBlock(true, (rf) -> {
			return rf.newTicket(ticket,tID);
		});
	}

	// Write
	@Override
	public void deleteTicket(String tID) throws RemoteException, ServerNotFoundException {
		queueAndBlock(true, (rf) -> {
			rf.deleteTicket(tID);
			return null;
		});
	}

	// Read
	@Override
	public ArrayList<String> getUserInbox(String userID) throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getUserInbox(userID);
		});
	}

	// Write
	@Override
	public void updateTicket(String tID, Ticket ticket) throws RemoteException, ServerNotFoundException {
		queueAndBlock(true, (rf) -> {
			rf.updateTicket(tID, ticket);
			return null;
		});
	}

	// Write
	@Override
	public void clearUserInbox(String userID) throws RemoteException, ServerNotFoundException {
		queueAndBlock(true, (rf) -> {
			rf.clearUserInbox(userID);
			return null;
		});
	}

	// Read
	@Override
	public HashMap<String, Ticket> getTicketByUser(String userID) throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getTicketByUser(userID);
		});
	}

	// Read
	@Override
	public HashMap<String, Ticket> getAllTickets() throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getAllTickets();
		});
	}

	// Read
	@Override
	public HashMap<String, Ticket> getAllUnassigned() throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getAllUnassigned();
		});
	}

	// Read
	@Override
	public Ticket getTicket(String tID) throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getTicket(tID);
		});
	}

	// Read
	@Override
	public String getPathToData() throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getPathToData();
		});
	}

	// Write
	@Override
	public void setPathToData(String pathToData) throws RemoteException, ServerNotFoundException {
		queueAndBlock(true, (rf) -> {
			rf.setPathToData(pathToData);
			return null;
		});
	}

	// Read
	@Override
	public Database getDatabase() throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.getDatabase();
		});
	}

	// Write
	@Override
	public void setDatabase(Database db) throws RemoteException, ServerNotFoundException {
		queueAndBlock(true, (rf) -> {
			rf.setDatabase(db);
			return null;
		});
	}

	// Read
	@Override
	public int checkStatus() throws RemoteException, ServerNotFoundException {
		return queueAndBlock(false, (rf) -> {
			return rf.checkStatus();
		});
	}
}
