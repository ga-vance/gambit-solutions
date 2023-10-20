package gambyt.proxy.controllers;

import gambyt.proxy.ServerNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gambyt.backend.*;
import gambyt.proxy.NameDatabase;

import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.UUID;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

	private NameDatabase nameData;

	public TaskController() {
		nameData = new NameDatabase("src/Names.json");
	}

	@CrossOrigin(origins = "*")
	@GetMapping("")
	public JSONObject getAllTasks() throws RemoteException, ServerNotFoundException {
//        Endpoint to return all tickets
		HashMap<String, Ticket> tickets = RMIInstance.getInstance().getAllTickets();
		JSONObject wrapper = new JSONObject();
		for (String tid : tickets.keySet()) {
			Ticket t = tickets.get(tid);
			if (t.assignee != -1) {
				t.assigneeName = this.nameData.getName(String.valueOf(t.assignee));
			}
			wrapper.put(tid, t);
		}
		return wrapper;
	}

	@CrossOrigin(origins = "*")
	@PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addNewTask(@RequestBody Ticket nt) throws RemoteException {
		// Endpoint to add new task
		nt.PrintTicketInfo();

		// Checks to see whether the assignee exists
		if (nt.assignee != -1 && this.nameData.getName(String.valueOf(nt.assignee)) == null) {
			return new ResponseEntity<>("BAD REQUEST", HttpStatus.BAD_REQUEST);
		}
		try {
			String tID = UUID.randomUUID().toString().replaceAll("-","");
			RMIInstance.getInstance().newTicket(nt,tID);
		}
		catch (ServerNotFoundException e) {
			return new ResponseEntity<>("Servers Offline", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@CrossOrigin(origins = "*")
	@PutMapping("/{id}")
	public ResponseEntity<String> updateTask(@PathVariable("id") String tID, @RequestBody Ticket ut)
			throws RemoteException {
//    Endpoint to update a given ticket by its id
		ut.PrintTicketInfo();

		// Checks to see whether the assignee exists
		if (ut.assignee != -1 && this.nameData.getName(String.valueOf(ut.assignee)) == null) {
			return new ResponseEntity<>("BAD REQUEST", HttpStatus.BAD_REQUEST);
		}
		try {
			RMIInstance.getInstance().updateTicket(tID, ut);
		}
		catch (ServerNotFoundException e) {
			return new ResponseEntity<>("Servers Offline", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@CrossOrigin(origins = "*")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable("id") String tID) throws RemoteException {
//        Endpoint to delete a task by id
		System.out.println("Trying to delete ticket");
		try {
			RMIInstance.getInstance().deleteTicket(tID);
		}
		catch (ServerNotFoundException e) {
			return new ResponseEntity<>("Servers Offline", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@CrossOrigin(origins = "*")
	@GetMapping("/{id}")
	public JSONObject getTask(@PathVariable("id") String tID) throws RemoteException, ServerNotFoundException {
//        Endpoint to get a task by id
		Ticket t = RMIInstance.getInstance().getTicket(tID);

		if (t == null) {
			throw new RemoteException("Invalid task");
		}

		if (t.assignee != -1) {
			t.assigneeName = this.nameData.getName(String.valueOf(t.assignee));
		}

		JSONObject wrapper = new JSONObject();
		wrapper.put(tID, t);
		return wrapper;

	}

	@CrossOrigin(origins = "*")
	@GetMapping("/user/{id}")
	public JSONObject getUserTasks(@PathVariable("id") long id) throws RemoteException, ServerNotFoundException {
//        Endpoint to get all of a specific users tasks
		String uID = Long.toString(id);
		HashMap<String, Ticket> tickets = RMIInstance.getInstance().getTicketByUser(uID);
		JSONObject wrapper = new JSONObject();
		for (String tid : tickets.keySet()) {
			Ticket t = tickets.get(tid);
			if (t.assignee != -1) {
				t.assigneeName = this.nameData.getName(String.valueOf(t.assignee));
			}
			wrapper.put(tid, t);
		}
		return wrapper;
	}

	@CrossOrigin(origins = "*")
	@GetMapping("/unassigned")
	public JSONObject getAllUnassigned() throws RemoteException, ServerNotFoundException {
//        Endpoint to return all tickets
		HashMap<String, Ticket> tickets = RMIInstance.getInstance().getAllUnassigned();
		JSONObject wrapper = new JSONObject();
		for (String tid : tickets.keySet()) {
			Ticket t = tickets.get(tid);
			if (t.assignee != -1) {
				t.assigneeName = this.nameData.getName(String.valueOf(t.assignee));
			}
			wrapper.put(tid, t);
		}
		return wrapper;
	}
}