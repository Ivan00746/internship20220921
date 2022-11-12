function loginPageCheckOut() {
    location.href = "/bankid/qr_page";
}

function menuReturn() {
    location.href = "/bankid/authMethodSelect";
}

function shopReturn() {
    location.href = "/bankid/";
}

function messageSwedBankId() {
    document.getElementById("divMain").setAttribute("style", "background:#BDE0FF");
    document.getElementById("div1").setAttribute("style", "text-align: right");
    document.getElementById("img_div1").setAttribute("src", "/images/red_x_close.png");
    document.getElementById("img_div1").setAttribute("width", "30px");
    document.getElementById("img_div1").setAttribute("onclick", "menuReturn()");
    document.getElementById("img_div1").setAttribute("class", "button button1");
    document.getElementById("img_div1").setAttribute("style", "margin: 5px");

    document.getElementById("input_div2").remove();
    document.getElementById("input_div3").remove();
    document.getElementById("input_div4").remove();
    const messageTag = document.createElement("a");
    messageTag.innerText = "This method can be implemented by using SwedBank authentication " +
        "after agreement this service with the bank";
    messageTag.setAttribute("style", "font-size: 20px; font-weight: bold");
    document.getElementById("div2").appendChild(messageTag);
    document.getElementById("div2").setAttribute("style", "padding: 80px 30px 110px 30px");
}

function testDataInput() {
    document.getElementById("divMain").setAttribute("style", "background:#BDE0FF");
    document.getElementById("div1").setAttribute("style", "text-align: right");
    document.getElementById("img_div1").setAttribute("src", "/images/red_x_close.png");
    document.getElementById("img_div1").setAttribute("width", "30px");
    document.getElementById("img_div1").setAttribute("onclick", "menuReturn()");
    document.getElementById("img_div1").setAttribute("class", "button button1");
    document.getElementById("img_div1").setAttribute("style", "margin: 5px");
    document.getElementById("input_div2").remove();
    document.getElementById("input_div3").remove();
    document.getElementById("input_div4").remove();
    const form = document.createElement("form");
    form.setAttribute("id", "dataForm");
    form.addEventListener("submit", function (event) {
            event.preventDefault();
            testUserDataPost();});
    const fieldSet = document.createElement("fieldset");
    fieldSet.setAttribute("style", "margin: auto; width: 300px; text-align: left");
    const legend = document.createElement("legend");
    legend.innerText = "TEST personal data:";
    fieldSet.appendChild(legend);

    let label = document.createElement("label");
    label.setAttribute("for", "firstName");
    label.innerText = "First name:";
    fieldSet.appendChild(label);
    let input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("class", "inputFieldset");
    input.setAttribute("id", "firstName");
    input.setAttribute("placeholder", "Leia");
    input.setAttribute("pattern", "^[A-Z][a-zA-Z]+$");
    input.setAttribute("required", "");
    fieldSet.appendChild(input);

    label = document.createElement("label");
    label.setAttribute("for", "lastName");
    label.innerText = "Last name:";
    fieldSet.appendChild(label);
    input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("class", "inputFieldset");
    input.setAttribute("id", "lastName");
    input.setAttribute("placeholder", "Ahlstrom");
    input.setAttribute("pattern", "^[A-Z][a-zA-Z]+$");
    input.setAttribute("required", "");
    fieldSet.appendChild(input);

    label = document.createElement("label");
    label.setAttribute("for", "personalNumber");
    label.innerText = "Personal Number:";
    fieldSet.appendChild(label);
    input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("class", "inputFieldset");
    input.setAttribute("id", "personalNumber");
    input.setAttribute("placeholder", "199710202392");
    input.setAttribute("pattern", "^[1-2][0-9]{11}$");
    input.setAttribute("required", "");
    fieldSet.appendChild(input);

    input = document.createElement("input");
    input.setAttribute("type", "submit");
    input.setAttribute("class", "submitInput");
    input.setAttribute("value", "Next");
    fieldSet.appendChild(input);
    form.appendChild(fieldSet);
    document.getElementById("div2").appendChild(form);
    document.getElementById("div2").setAttribute("style", "padding: 35px 20px 70px 20px");
}

function testUserDataPost() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/testUserData");
    xhttp.setRequestHeader("Content-Type", "application/json");
    xhttp.send(JSON.stringify({
        "personalNumber": document.getElementById("personalNumber").value,
        "givenName": document.getElementById("firstName").value,
        "surname": document.getElementById("lastName").value
    }));
    xhttp.onload = function () {
        if (this.response === "auth_OK") window.location.replace("/bankid/paysettings_page");
    }
}