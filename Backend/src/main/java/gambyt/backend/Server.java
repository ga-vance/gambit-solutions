package gambyt.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.net.http.HttpRequest;

public class Server extends Thread {
	private String proxyIp;

	public Server(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public void run() {
		RemoteFrontend front = null;
		Registry registry = null;
		try {
			// Change path to the actual database post-testing
			front = new FrontendImpl("src/JSON_Test.json");

			// Create registry local to server on port 1099 (default port)
			registry = LocateRegistry.createRegistry(1099);
			registry.rebind("FrontendImpl", front);
			System.err.println("RMI Server Running Succesfully Bois");

			int status = sendConnectionRequest(proxyIp);

			if (status == 200) {
				System.out.println("Server connected to " + proxyIp);
			}
			
			Object dummy = new Object();
			synchronized (dummy) {
				dummy.wait();
			}
			System.out.println("this print should never run");
		} catch (InterruptedException ie) {
			try {
				deactivateRMI(front, registry);
			} catch (Exception e) {
				System.out.println("Error shutting down RMI server.");
			}
		} catch (Exception e) {
			System.out.println("Exception occurred while running server: " + e.toString());
			e.printStackTrace();
		}
	}

	private int sendConnectionRequest(String proxyIp) throws InterruptedException {
		HttpResponse resp = null;
		while (true) {
			try {
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.uri(new URI("http://" + proxyIp + ":8080/api/v1/database/register"))
						.headers("Content-Type", "text/plain;charset=UTF-8")
						.POST(HttpRequest.BodyPublishers.ofString("Initiate Connection")).build();

				resp = client.send(request, HttpResponse.BodyHandlers.ofString());
				return resp.statusCode();
			} catch (Exception e) {
				System.out.println("HTTP Connection Request failed, trying again...");
				Thread.sleep(5000);
			}
		}
	}

	private void deactivateRMI(RemoteFrontend front, Registry registry) throws NotBoundException, RemoteException {
		registry.unbind("FrontendImpl");
		// Shut down RMI server object
		UnicastRemoteObject.unexportObject(front, true);
		// Shutdown RMI registry
		UnicastRemoteObject.unexportObject(registry, true);
		System.out.println("RMI Server has been shutdown");
	}

	/*
	 * From https://www.baeldung.com/java-get-ip-address
	 */
//    private static String getIP() {
//        String urlString = "http://checkip.amazonaws.com/";
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(URL.openStream()))) {
//            return br.readLine();
//        }
//        catch (Exception e ) {
//            System.out.println("Error retrieving IP Address of Current Machine: " + e.getMessage());
//        }
//
//    }
}
