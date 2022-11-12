function getUserData() {
    const request = new XMLHttpRequest();
    request.addEventListener('load', function () {
        let paymentSettingsResponse = JSON.parse(this.responseText);
        let script = document.createElement('script');
        let operation = paymentSettingsResponse.operations.find(function (o) {
            return o.rel === 'view-checkout';
        });
        script.setAttribute('src', operation.href);
        script.onload = function () {
            // When the 'view-checkout' script is loaded, we can initialize the
            // Payment Menu inside 'checkout-container'.
            payex.hostedView.checkout({
                container: {
                    checkout: "checkout"
                },
                culture: 'en-US',
            }).open();
        };
        // Append the Checkout script to the <head>
        let head = document.getElementsByTagName('head')[0];
        head.appendChild(script);
    });
// Like before, you should replace the address here with
// your own endpoint.
    request.open('GET', '/bankid/get_payment_response', true);
    request.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
    request.send();
}

function logOut() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/bankid/exit", true);
    xhttp.send();
    xhttp.onload = function () {
        if (this.response === "exit") window.location.replace("/bankid");
    }
}
