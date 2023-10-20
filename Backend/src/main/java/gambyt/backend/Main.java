package gambyt.backend;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Expected a command line argument for Proxy IP Address.");
            return;
        }
        // Get proxy IP from cmd-line args
        String proxyIp = args[0];

        Server serverObj = new Server(proxyIp);

        // Start server thread
        serverObj.start();

        boolean serverRunning = true;
        while (serverRunning) {
            try {
                Thread.sleep(30000);
            }
            catch (Exception e) {
                System.out.println("Error in making thread sleep, exiting...");
                serverRunning = false;
            }

            try {
                // Check if proxy is still running
                HttpResponse resp = sendStatusCheck(proxyIp);

                if (resp.statusCode() != 200) {
                    System.out.println(resp.toString());
                    System.out.println("Connection with Proxy has been disrupted, rebooting server to reconnect...");

                    // Restart Server
                    serverObj = restartThread(serverObj, proxyIp);
                }
                else {
                    System.out.println("Server-Proxy Connection is Maintained");
                }
            }
            catch (Exception e) {
                System.out.println("Proxy is down, rebooting server to attempt a reconnect...");
                serverObj = restartThread(serverObj, proxyIp);
            }
        }
    }

    private static Server restartThread(Server serverObj, String proxyIp) {
        serverObj.interrupt();
        serverObj = new Server(proxyIp);
        serverObj.start();
        return serverObj;
    }

    private static HttpResponse sendStatusCheck(String proxyIp) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://" + proxyIp + ":8080/api/v1/database/check_status"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString("Maintain Connection"))
                .build();

        HttpResponse resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        return resp;
    }
}
