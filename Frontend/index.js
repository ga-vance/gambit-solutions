const baseURL = 'http://127.0.0.1:8080/api/v1'; // This needs to be updated and set if changed
var userID = -1;

const submitLogin = document.getElementById("submitLogin");
submitLogin.addEventListener('click', attemptLogin);


function loginSuccess() {
    window.location.href = './front.html'
}
  
function loginFailure() {
alert("Failed login; user doesn't exist");
}

function setCookie() {
    document.cookie = `userID=${userID}`
}

function attemptLogin(event) {
  console.log("Attempt login called")
  event.preventDefault();
  const form = event.target.parentNode;
  const formData = new FormData(form);
  const id = formData.get("id");

  const path = "/users/login/" + id;
  const url = baseURL + path;
  console.log(url);

  const options = {
    method: 'GET',
    headers: {'Content-Type': 'application/json', 'Access-Control-Allow-Origin' : '*'},
  };

  console.log(options);

  fetch(url, options)
    .then(response => response.json())
    .then(data => {
      if (data['status']) {
        userID = id;
        setCookie();
        loginSuccess();
      } else {
        loginFailure();
      }
    }) 
    .catch(error => console.error(error));

  console.log(JSON.stringify(Object.fromEntries(formData)));
  // if valid response, set global variable userID to id entered 
}
