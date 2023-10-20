const baseURL = 'http://127.0.0.1:8080/api/v1'; // This needs to be updated and set if changed
userID = getCookie("userID");

// can't do this while on login page
window.onload = function() {
  getTickets();
}

function getCookie(cookie) {
  let name = cookie + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  let ca = decodedCookie.split(';');
  for(let i = 0; i <ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) == ' ') {
          c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
          return c.substring(name.length, c.length);
      }
  }
  return "";
}

function displayTickets(jsonData) {
  // Get a reference to the element where we will display the tickets
  const ticketsContainer = document.getElementById('tickets-container');

  // Parse the JSON data and get the tickets object
  const data = JSON.parse(jsonData);
  const tickets = data;


  // Loop through the tickets object and generate HTML code for each ticket
  for (const ticketId in tickets) {
    const ticket = tickets[ticketId];
    var p_val;
    var status_value;

    if (ticket.priority == 2) {
      p_val = "High";
    } else if (ticket.priority == 1) {
      p_val = "Medium";
    } else {
      p_val = "Low";
    }

    // console.log("Priority value after parsing: " + priority_value);
    console.log("P")
    if (ticket.status == 0) {
      status_value = "To-Do";
    } else if (ticket.status == 1) {
      status_value = "In-Progress";
    } else if (ticket.status == 2) {
      status_value = "Done";
    }


    const html = `
      <div class="ticket" id="${ticketId}" data-ticket-id="${ticketId}">
        <div class="ticket-title">${ticket.name}</div>
        <div class="ticket-description">${ticket.description}</div>
        <div class="ticket-info">
          <div class="date"><strong>Date:</strong> ${ticket.dateAssigned}</div>
          <div class="priority"><strong>Priority:</strong> ${p_val}</div>
        </div>
        <div class="ticket-info">
          <div class="assignee"><strong>Assignee:</strong> ${ticket.assigneeName}</div>
          <div class="status"><strong>Status:</strong> ${status_value}</div>
        </div>
        <div class="ticket-buttons">
          <button class="edit" id="edit" data-ticket='${JSON.stringify(ticket)}'>Edit</button>
          <button class="delete deleteTicket" type="button" id="deleteTicket">Delete</button>
        </div>
        <div class="ticket-buttons">
          <button class="subscribe" id="subscribe" data-ticket='${JSON.stringify(ticket)}'>Subscribe</button>
          <button class="claim" id="claim" data-ticket='${JSON.stringify(ticket)}'>Claim</button>
        </div>
      </div>
    `;
    
    // Append the generated HTML code to the tickets container
    ticketsContainer.innerHTML += html;
  }
}

function displayInbox(jsonData) {
  // Get a reference to the element where we will display notifications 
  const inboxContainer = document.getElementById('inbox-container');

  const data = JSON.parse(jsonData);

  console.log(data);

  // Loop through the notificationsgenerate HTML code for each

  for (var i = 0; i < data.length; i++) {
    console.log(typeof data);
    const ticketTitle = data[i].split(':')[0];
    const ticketDesc = data[i].split(':')[1];

    console.log(ticketTitle);
    

    const html = `
    <div class="inbox-item">
      <div class="ticket-title">${ticketTitle}</div>
      <div class="ticket-description">${ticketDesc}</div>
    </div>
    `;
    
    // Append the generated HTML code to the inbox container
    inboxContainer.innerHTML += html;
  }
}

function clearTickets() {
  const ticketsContainer = document.getElementById('tickets-container');
  ticketsContainer.innerHTML = '';
}

function clearInbox() {
  const inboxContainer = document.getElementById('inbox-container');
  inboxContainer.innerHTML = '';
}

function updateDynamicTicketButtons() {
  subscribe = document.getElementsByClassName("subscribe");
  // unsubscribe = document.getElementsByClassName("unsubscribe");
  del = document.getElementsByClassName("delete deleteTicket");
  claim = document.getElementsByClassName("claim");
  edit = document.getElementsByClassName("edit");


  for (var i = 0; i < subscribe.length; i++) {
    subscribe[i].addEventListener('click', subTicket);
  }
  // for (var i = 0; i < unsubscribe.length; i++) {
  //   unsubscribe[i].addEventListener('click', unsubTicket);
  // }
  for (var i = 0; i < del.length; i++) {
    del[i].addEventListener('click', deleteTicket);
  }
  for (var i = 0; i < claim.length; i++) {
    claim[i].addEventListener('click', claimTicket);
  }

  for (var i = 0; i < edit.length; i++) {
    edit[i].addEventListener('click', displayEdit);
  }
}



// Get the HTML elements to show/hide
// const loginSection = document.getElementById("login-section");
const editTicketSection = document.getElementById("edit-ticket");
const createTicketSection = document.getElementById("create-ticket");
const viewTicketsSection = document.getElementById("view-tickets");
const inboxSection = document.getElementById("inbox");
const navbar = document.getElementById("navbar");

// Get the menu items 
const createTicketLink = document.querySelector("a[href='#create-ticket']");
const viewTicketsLink = document.querySelector("a[href='#view-tickets']");
const inboxLink = document.querySelector("a[href='#inbox']");

// Hide all sections except for the Create a Ticket section initially
// loginSection.style.display = "block";
createTicketSection.style.display = "none";
editTicketSection.style.display = "none";
viewTicketsSection.style.display = "block";
inboxSection.style.display = "none";
// navbar.style.display = "none";

// Attach click event listeners to switch scenes
createTicketLink.addEventListener("click", function(event) {
  event.preventDefault();
  editTicketSection.style.display = "none";
  createTicketSection.style.display = "block";
  viewTicketsSection.style.display = "none";
  inboxSection.style.display = "none";
});

viewTicketsLink.addEventListener("click", function(event) {
  event.preventDefault();
  editTicketSection.style.display = "none";
  createTicketSection.style.display = "none";
  viewTicketsSection.style.display = "block";
  inboxSection.style.display = "none";
});

inboxLink.addEventListener("click", function(event) {
  event.preventDefault();
  editTicketSection.style.display = "none";
  createTicketSection.style.display = "none";
  viewTicketsSection.style.display = "none";
  inboxSection.style.display = "block";
});

function displayEdit(event) {
  event.preventDefault();
  console.log("Event target dataset ticket: " + event.target.dataset.ticket);
  var ticketData = JSON.parse(event.target.dataset.ticket);

  // Display the edit screen
  editTicketSection.style.display = "block";
  createTicketSection.style.display = "none";
  viewTicketsSection.style.display = "none";
  inboxSection.style.display = "none";
  
  console.log(ticketData);

  var ticketId = event.target.closest('.ticket').dataset.ticketId;
  console.log("Ticket id: " + ticketId);

  // Prepopulate the information from the ticket
  document.querySelector('#hidden-ticket-id').value = ticketId;
  document.querySelector('#edit-job-title').value = ticketData.name;
  document.querySelector('#edit-description').value = ticketData.description;
  document.querySelector('#edit-due-date').value = ticketData.dateAssigned;
  document.querySelector('#edit-priority').value = ticketData.priority;
  document.querySelector('#edit-status').value = ticketData.status;
  document.querySelector('#edit-assignee').value = ticketData.assigneeName;

  // form.id = ticketData.id;
  // console.log("Ticket id: " + ticketData.id);


  
}


document.addEventListener('DOMContentLoaded', function() {
  console.log("DOM loaded");

  // const jsonData = '{"tickets": {"100": {"name": "Super-Ticket","assignee": 0,"status": 0,"subscribers": ["0", "10", "2", "3"],"description": "Hello World!","date_assigned": "2023-02-15","priority": 0},"618": {"name": "Super Lame Ticket","assignee": 10,"status": 2,"subscribers": [],"description": "This ticket sucks","date_assigned": "2023-02-04","priority": 2}},"inbox": {"0": ["Hello", "World!", "Boo"],"1": ["Leave me Here", "F-Society"]}}';
  // displayJson(jsonData);


  // Get the elements for REST API calls
  const myInbox = document.getElementById("my-inbox");
  const allTickets = document.getElementById("all-tickets");
  const createNewTicket = document.getElementById("submitTicket");
  const updateTicket = document.getElementById("editTicket");
  // const submitLogin = document.getElementById("submitLogin");
  const clearInboxButton = document.getElementById("clear-inbox");
  const allTicketstwo = document.getElementById("all");
  const myTickets = document.getElementById("mine");
  const unclaimed = document.getElementById("unclaimed");

  updateDynamicTicketButtons();



  // Add listeners
  // myInbox.addEventListener('click', getInbox);
  allTickets.addEventListener('click', getTickets);
  myInbox.addEventListener('click', getInbox)
  createNewTicket.addEventListener('click', postTicket);
  updateTicket.addEventListener("click", putTicket);
  // submitLogin.addEventListener('click', attemptLogin);
  clearInboxButton.addEventListener('click', clearUserInbox);
  allTicketstwo.addEventListener('click', getTickets);
  myTickets.addEventListener('click', getMyTickets);
  unclaimed.addEventListener('click', getUnclaimed);

});


// Functions

// function getInbox(event) {
//   const path = '/inbox/${userID}';
//   const url = baseURL + path;

//   fetch(url)
//     .then(response => response.json())
//     .then(data => console.log(data)) // DO stuff with Response
//     .catch(error => console.error(error));
// }

// function loginSuccess() {
//   editTicketSection.style.display = "none";
//   createTicketSection.style.display = "none";
//   viewTicketsSection.style.display = "block";
//   inboxSection.style.display = "none";
//   loginSection.style.display = "none";
//   // navbar.style.display = "block";
//   getTickets();
//   updateDynamicTicketButtons();
// }

// function loginFailure() {
//   alert("Failed login; user doesn't exist");
// }

function getTickets(event) {
  const path = '/tasks';
  const url = baseURL + path;


  fetch(url)
    .then(response => response.json())
    .then(data => 
      {
        clearTickets();
        displayTickets(JSON.stringify(data));
        updateDynamicTicketButtons();
      }) // Do stuff with response
    .catch(error => console.error(error));
}

function getMyTickets(event) {
  // event.preventDefault();
  const path = '/tasks/user/' + userID
  const url  = baseURL + path

  const options = {
    method: 'GET', 
    headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin' : '*'},
  }

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      clearTickets();
      displayTickets(JSON.stringify(data));
      updateDynamicTicketButtons();
    })
    .catch(error => console.error(error));
}

function getUnclaimed(event) {
  // event.preventDefault();
  const path = '/tasks/unassigned'
  const url  = baseURL + path

  const options = {
    method: 'GET', 
    headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin' : '*'},
  }

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      clearTickets();
      displayTickets(JSON.stringify(data));
      updateDynamicTicketButtons();
    })
    .catch(error => console.error(error));
}


function clearUserInbox(event) {
  event.preventDefault();
  const path = '/inbox/' + userID;
  const url = baseURL + path;
  console.log(url);

  const options = {
    method: 'DELETE',
    headers: {'Content-Type': 'application/json'},
  };

  console.log(options);

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      console.log(data);
      clearInbox();
      getInbox(event);
    }) 
    .catch(error => console.error(error));

}

function getInbox(event) {
  const path = '/inbox/' + userID;
  const url = baseURL + path;
  console.log(url);

  const options = {
    method: 'GET',
    headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin' : '*'},
  };

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      console.log("Data response");
      console.log(data);
      clearInbox();
      if (data != undefined) {displayInbox(JSON.stringify(data));}
    }) 
    .catch(error => console.error(error));



}



function putTicket(event) {
  event.preventDefault();
  // Need to get ticket ID
  console.log("Put ticket called");

  const form = event.target.parentNode;
  console.log(form);
  const formData = new FormData(form);

  const id = document.querySelector('#hidden-ticket-id').value;
  console.log("id: " + id);

  const path = "/tasks/" + id;
  const url = baseURL + path;

  const options = {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(Object.fromEntries(formData))
  };
  console.log(options);
  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      console.log(data);
      form.reset();
    }) 
    .catch(error => console.error(error));
}

function postTicket(event) {
  event.preventDefault();
  console.log("Post ticket called");
  const form = event.target.parentNode;
  console.log(form)
  const formData = new FormData(form);
  const path = '/tasks';
  const url = baseURL + path;
  const options = {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(Object.fromEntries(formData))
  };
  console.log(options);

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      console.log(data);
      form.reset();
    }) 
    .catch(error => console.error(error));
}

// function attemptLogin(event) {
//   console.log("Attempt login called")
//   event.preventDefault();
//   const form = event.target.parentNode;
//   const formData = new FormData(form);
//   const id = formData.get("id");

//   const path = "/users/login/" + id;
//   const url = baseURL + path;
//   console.log(url);

//   const options = {
//     method: 'GET',
//     headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin' : '*'},
//   };

//   console.log(options);

//   fetch(url, options)
//     .then(response => response.json())
//     .then(data => {
//       if (data['status']) {
//         userID = id;
//         loginSuccess();
//       } else {
//         loginFailure();
//       }
//     }) 
//     .catch(error => console.error(error));

//   console.log(JSON.stringify(Object.fromEntries(formData)));
//   // if valid response, set global variable userID to id entered 
// }

// function unsubTicket() {
//   //  Need to get ticket ID from inbox for ticket
//   const path = '/tasks/${ticketID}';
//   const url = baseURL + path;
//   const options = {
//     method: 'PUT',
//     headers: {'Content-Type': 'application/json'},
//     body: JSON.stringify(Object.fromEntries(formData))
//   };

//   fetch(url, options)
//     .then(response => response.json())
//     .then(data => console.log(data)) // Do Stuff with response
//     .catch(error => console.error(error));
// }

function subTicket(event) {
  event.preventDefault();
  var ticketID = event.srcElement.parentNode.parentNode.id;
  var ticketData = JSON.parse(event.target.dataset.ticket);
  var subsList = ticketData.subscribers;
  console.log(ticketData)
  if (subsList.includes(userID)) {
    console.log("User is already subscribed to this");
    return
  }
  else {
    subsList.push(userID)
  }
  // console.log(subsList);
  const path = '/tasks/' + ticketID;
  const url = baseURL + path;
  const options = {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(ticketData)
  };

  fetch(url, options)
    .then(response => response.json())
    .then(data => console.log(data)) // Do Stuff with response
    .catch(error => console.error(error));
}

// THis one may need to be changed, No form really for this one. Will just need user id and ticket id
function claimTicket(event) {
  event.preventDefault();
  var ticketID = event.srcElement.parentNode.parentNode.id;
  var ticketData = JSON.parse(event.target.dataset.ticket);
  ticketData.assignee = Number(userID);
  ticketData.assigneeName = "";
  console.log(ticketData);
  const path = '/tasks/' + ticketID;
  const url = baseURL + path;
  console.log(url);
  const options = {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(ticketData)
  };

  fetch(url, options)
    .then(response => response.json())
    .then(data => console.log(data)) // Do Stuff with response
    .catch(error => console.error(error));
}



function deleteTicket(event) {
  event.preventDefault();
  var ticketID = event.srcElement.parentNode.parentNode.id;
  console.log("Delete button pressed");
  console.log(ticketID);
  const path = '/tasks/' + ticketID;
  const url = baseURL + path;
  const options = {
    method: 'DELETE'
  };

  console.log(path);

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      getTickets(); 
    }) // Do Stuff with response
    .catch(error => console.error(error));
}

