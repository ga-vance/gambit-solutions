package gambyt.proxy.controllers;

import gambyt.backend.Database;
import gambyt.backend.RemoteFrontend;
import gambyt.proxy.ServerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.rmi.RemoteException;

@RestController
@RequestMapping("/api/v1/database")
public class DatabaseController {
    @PostMapping("/register")
    public ResponseEntity<String> registerBackend(HttpServletRequest req) throws RemoteException {
        String ip = req.getRemoteAddr();
        System.out.println("New registration request from " + ip);

        // If the IP connecting is already registered, remove it from the rotation before re-registering
        if (RMIInstance.ipInRotation(ip)) {
            RMIInstance.removeServer(ip);
        }

        // If first instance, just add it to the rotation.
        if (!RMIInstance.instanceExists()) {
            System.out.println("No instances currently connected. New server being added...");
            RMIInstance.registerFirstInstance(ip);
        }
        // register backend and send the db copy to new backend
        else {
            try {
                Database db = RMIInstance.getInstance().getDatabase();
                RemoteFrontend newServer = RMIInstance.registerNewInstance(ip);
                newServer.setDatabase(db);
                RMIInstance.addInstanceToRotation(newServer, ip);
            }
            catch (ServerNotFoundException e) {
                return new ResponseEntity<>("Servers Offline", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/check_status")
    public ResponseEntity<String> checkProxyStatus(HttpServletRequest req) {
        String ip = req.getRemoteAddr();

        if (RMIInstance.ipInRotation(ip)) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Server Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
