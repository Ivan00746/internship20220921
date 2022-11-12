function dataSessionRequest() {
    usedDesktopApp = false;
    launchSuccess = false;
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/connection_request");
    xhttp.send();
    xhttp.onload = function () {
        let launchInfo = JSON.parse(this.response);
        autoStartToken = launchInfo.autoStartToken;
        orderRef = launchInfo.orderRef;
        if (orderRef == null || orderRef === "") {
            message("\n\n\n\nUnknown server error. Please try again.\n\n\n\n\n", "background:#EAC6C6FF");
        } else {
            collectRequest();
        }
    }
    getDynamicQR = setInterval(function () {
        if (orderRef !== "") document.getElementById("dynamicQRImg").setAttribute("src", "/bankid/QRImage?t=" + new Date().getTime());
    }, 1000);
}

function launchBankIdApp() {
    let myWindow = window.open("bankid:///?autostarttoken=" + window.autoStartToken + "&redirect=null");
    document.getElementById("launchReference").removeAttribute("href");
    document.getElementById("launchReference").remove();
    window.usedDesktopApp = true;
    window.launchSuccess = (myWindow != null);
    collectRequest();
}

function collectRequest() {
    const collectInterval = setInterval(function () {
        const xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/bankid/collection_request");
        xhttp.setRequestHeader("Content-Type", "application/json");
        xhttp.send(JSON.stringify({"usedDesktopApp": usedDesktopApp, "launchSuccess": launchSuccess}));
        xhttp.onload = function () {
            let pendingInfo = JSON.parse(this.response);
            let authStatus = pendingInfo.status;
            let userMessage = pendingInfo.message;
            // document.getElementById("testLabel").innerHTML = authStatus;
            if (authStatus !== "pending") {
                clearInterval(collectInterval);
                clearInterval(window.getDynamicQR);
            }
            if (authStatus === "complete") {
                window.location.replace("/bankid/paysettings_page");
            }
            if (authStatus === "pending" && userMessage !== "") {
                message("\n\n\n\n" + userMessage + "\n\n\n\n\n", "background:#BDE0FF");
            }
            if (authStatus === "failed") {
                message("\n\n\n\n" + userMessage + "\n\n\n\n\n", "background:#EAC6C6FF");
            }
        }
    }, 2000);
}

function message(message, background) {
    document.getElementById("divMain").setAttribute("style", background);
    document.getElementById("div1").setAttribute("style", "text-align: right");
    document.getElementById("img_div1").setAttribute("height", "30px");
    document.getElementById("img_div1").setAttribute("src", "/images/red_x_close.png");
    document.getElementById("img_div1").setAttribute("onclick", "logOut()");
    document.getElementById("img_div1").setAttribute("class", "button button1");
    document.getElementById("a_div2").setAttribute("style", "font-size: 20px; font-weight: bold");
    document.getElementById("a_div2").innerText = message;
    document.getElementById("dynamicQRImg").remove();
    document.getElementById("launchReference").remove();
    document.getElementById("logOut").remove();
}

function logOut() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/exit", true);
    xhttp.send();
    xhttp.onload = function () {
        if (this.response === "exit") window.location.replace("/bankid/authMethodSelect");
    }
}

function shopReturn() {
    location.href = "/bankid";
}
