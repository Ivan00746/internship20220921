function getUserData() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/authdata_request");
    xhttp.send();
    xhttp.onload = function () {
        let userData = JSON.parse(this.response);
        givenName = userData.givenName;
        surname = userData.surname;
        document.getElementById("p_div2").innerHTML =
            "Welcome, " + givenName + " " + surname + "!";
    }
    document.getElementById("p_div3").innerHTML = "This is your bank access page.";
}

function logOut() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/exit", true);
    xhttp.send();
    xhttp.onload = function () {
        if (this.response === "exit") window.location.replace("/bankid");
    }
}
