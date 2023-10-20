package gambyt.proxy.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gambyt.proxy.NameDatabase;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	private NameDatabase nameData;
	
	public UserController() {
		nameData = new NameDatabase("src/Names.json");
	}
	
	
	@SuppressWarnings("unchecked")
	@CrossOrigin(origins = "*")
    @GetMapping("")
	public JSONObject getAllUsers() throws RemoteException {
//        Endpoint to get all users and names
    
    	JSONObject wrapper = new JSONObject();
    	
    	HashMap<String,String> nd = this.nameData.getData();
    	
    	for(String uID : nd.keySet()) {
    		wrapper.put(uID, nd.get(uID));
    	}
    	
    	return wrapper;
    	
    }
	
    @SuppressWarnings("unchecked")
	@CrossOrigin(origins = "*")
    @GetMapping("/login/{id}")
	public JSONObject checkLogin(@PathVariable("id") String uID) throws RemoteException {
		//Endpoint to login a user Returns JSON Object {"status": 0} 
    	//0 - login failed
    	//1 - login successful (user is in name database)
    
    	JSONObject wrapper = new JSONObject();
    	long status = 0;
    	
		
    	if(nameData.getName(uID) != null) {
    		status = 1; //Login was successful
    	}
    	
    	wrapper.put("status", status);
    	
    	return wrapper;
    	
    }
}
