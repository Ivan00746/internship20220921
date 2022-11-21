app.controller('CartController', function ($scope) {

    $scope.cart = new Array(8);
    $scope.showCartPopup = false;
    $scope.needShippingAddress = false;
    $scope.isShippingAddress = false;
    $scope.userPaymentDataPosted = false;
    $scope.maxItemQantity = 10;
    $scope.testData = "empty";

    $(document).ready(function () {
        let xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/bankid/getCart");
        xhttp.send();
        xhttp.onload = function () {
            $scope.publicOrderDataDTO = JSON.parse(this.response);
            $scope.cart.fill(0);
            if ($scope.publicOrderDataDTO.itemDTOList != null) {
                for (let i = 0; i < $scope.publicOrderDataDTO.itemDTOList.length; i++) {
                    $scope.cart[$scope.publicOrderDataDTO.itemDTOList[i].id - 1] =
                        $scope.publicOrderDataDTO.itemDTOList[i].quantity;
                    if (document.getElementById("orderTable") != null) addOrderRow(i);
                }
            }
            if (document.getElementById("orderTable") != null) addOrderSummaryRow();
            for (let i = 0; i < 4; i++) {
                if ($scope.cart[i] !== 0) $scope.needShippingAddress = true;
            }
            for (let i = 4; i < 8; i++) {
                if ($scope.cart[i] !== 0) $scope.needElectronicDelivery = true;
            }
            if (document.getElementById("orderTable") != null &&
                document.getElementById("needShippingAddressL") != null) {
                if (!$scope.needShippingAddress) {
                    document.getElementById("needShippingAddressL")
                        .setAttribute("style", "color: #a9a9a9");
                        // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
                    // document.getElementById("needShippingAddressI").setAttribute("disabled", "");
                    deliveryFieldDelShippingOffer();
                    preorderFieldDelShippingOffer();
                } else {
                    deliveryFieldDelElectronicDelivery();
                    preorderFieldDelElectronicDelivery();
                }
            }
            $scope.$apply();
        }
    });

    $scope.removeProduct = function (id) {
        //Decrease number of one product
        if ($scope.cart[id] > 0) {
            $scope.cart[id] = $scope.cart[id] - 1;
            const xhttp = new XMLHttpRequest();
            xhttp.open("POST", "/bankid/updateCart");
            xhttp.setRequestHeader("Content-Type", "application/json");
            xhttp.send(JSON.stringify($scope.cart));
        }
    };

    $scope.removeAll = function (id) {
        $scope.cart[id] = 0;
        const xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/bankid/updateCart");
        xhttp.setRequestHeader("Content-Type", "application/json");
        xhttp.send(JSON.stringify($scope.cart));
    };

    $scope.addProduct = function (id) {
        //Increase number of one product
        if ($scope.cart[id] < $scope.maxItemQantity) {
            $scope.showCartPopup = true;
            $scope.cart[id] = $scope.cart[id] + 1;
            const xhttp = new XMLHttpRequest();
            xhttp.open("POST", "/bankid/updateCart");
            xhttp.setRequestHeader("Content-Type", "application/json");
            xhttp.send(JSON.stringify($scope.cart));
        } else {
            throw new Error('You can order ' + $scope.maxItemQantity + ' same item at most');
        }
    };

    $scope.closeCartPopup = function () {
        $scope.showCartPopup = false;
    };

    $scope.itemCount = function (id) {
        return $scope.cart[id];
    };

    $scope.amountCount = function () {
        return $scope.cart.getAmountCount();
    };

    $scope.totalItemCount = function () {
        return $scope.cart.getTotalItemCount();
    };

    $scope.moveOn = function () {
        if ($scope.publicOrderDataDTO.givenName != null) {
            location.href = "/bankid/paysettings_page";
        } else {
            location.href = "/bankid/authMethodSelect";
        }
    }

    $scope.logout = function () {
        const sbhttp = new XMLHttpRequest();
        sbhttp.open("GET", "/bankid/logout");
        sbhttp.send();
        sbhttp.onload = function () {
            if (this.response === "logout_OK") window.location.replace("/bankid");
        }
    }

    $scope.profile = function () {
        // const sbhttp = new XMLHttpRequest();
        // sbhttp.open("GET", "/bankid/get_pastpayment_response");
        // sbhttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
        // sbhttp.send();
        // sbhttp.onload = function () {
        //     let paymentOrderResponse = JSON.parse(this.response);
        //     let operation = paymentOrderResponse.operations.find(function (o) {
        //         return o.rel === "redirect-checkout";
        //     });
        //     document.getElementById("msgField").innerText = operation.href;
        //     window.location.replace(operation.href);
        // }
        window.open("https://admin.externalintegration.payex.com/psp/beta/login?return=%2Fpayments");
    }

    $scope.setShippingAddress = function () {
        $scope.isShippingAddress = !($scope.isShippingAddress);
        if ($scope.isShippingAddress && !$scope.userPaymentDataPosted) {
            document.getElementById("shipBillingAdrI").removeAttribute("checked");
            document.getElementById("shipDiffersAdrI")
                .setAttribute("checked", "checked");
            document.getElementById("shipBillingAdrL")
                .setAttribute("style", "color: #a9a9a9");
                // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
            // document.getElementById("shipBillingAdrI").setAttribute("disabled", "");
            document.getElementById("emailShipping").value =
                document.getElementById("email").value
            document.getElementById("msisdnShipping").value =
                document.getElementById("msisdn").value
            document.getElementById("firstNameShipping").value =
                document.getElementById("firstName").value
            document.getElementById("lastNameShipping").value =
                document.getElementById("lastName").value
        } else {
            document.getElementById("shipDiffersAdrI").removeAttribute("checked");
            document.getElementById("shipBillingAdrI")
                .setAttribute("checked", "checked");
            document.getElementById("shipBillingAdrL")
                .removeAttribute("style");
            // document.getElementById("shipBillingAdrI").removeAttribute("disabled");
        }
    }

    $scope.postUserPaymentData = function () {
        const xhttp = new XMLHttpRequest();
        xhttp.open("POST", "/bankid/setUserData");
        xhttp.setRequestHeader("Content-Type", "application/json");
        xhttp.send(JSON.stringify({
            "email": document.getElementById("email").value,
            "msisdn": document.getElementById("msisdn").value,
            "streetAddress": document.getElementById("streetAddress").value,
            "city": document.getElementById("city").value,
            "zipCode": document.getElementById("zipCode").value,
            "countryCode": document.getElementById("countryCode").value,
            "emailShipping": document.getElementById("emailShipping").value,
            "msisdnShipping": document.getElementById("msisdnShipping").value,
            "firstNameShipping": document.getElementById("firstNameShipping").value,
            "lastNameShipping": document.getElementById("lastNameShipping").value,
            "streetAddressShipping": document.getElementById("streetAddressShipping").value,
            "cityShipping": document.getElementById("cityShipping").value,
            "zipCodeShipping": document.getElementById("zipCodeShipping").value,
            "countryCodeShipping": document.getElementById("countryCodeShipping").value,
            "accountAgeIndicator": getInputRadioValue("accountAgeIndicator"),
            "accountChangeIndicator": getInputRadioValue("accountChangeIndicator"),
            "accountPwdChangeIndicator": getInputRadioValue("accountPwdChangeIndicator"),
            "shippingAddressUsageIndicator": getInputRadioValue("shippingAddressUsageIndicator"),
            "suspiciousAccountActivity": getInputRadioValue("suspiciousAccountActivity"),
            "deliveryEmailAddress": getDeliveryEmailAddress(),
            "digitalProducts": document.getElementById("digitalProductsI").checked,
            "deliveryTimeframeIndicator": getInputRadioValue("deliveryTimeframeIndicator"),
            "preOrderDate": document.getElementById("preOrderDate").value,
            "preOrderPurchaseIndicator": getInputRadioValue("preOrderPurchaseIndicator"),
            "shipIndicator": getInputRadioValue("shipIndicator"),
            "reOrderPurchaseIndicator": getInputRadioValue("reOrderPurchaseIndicator"),
        }));
        $scope.userPaymentDataPosted = true;
        document.getElementById("needShippingAddressI").removeAttribute("checked");
        $scope.isShippingAddress = false;
        xhttp.onload = function () {
            let paymentOrderResponse = JSON.parse(this.response);
            if (paymentOrderResponse.operations != null) {
                let operation = paymentOrderResponse.operations.find(function (o) {
                    return o.rel === "redirect-checkout";
                });
                window.location.replace(operation.href);
            } else if (paymentOrderResponse.sbProblem !=null) {
                let msg = paymentOrderResponse.sbProblem.title + ". "
                    + paymentOrderResponse.sbProblem.detail + ".\n";
                for (let i=0; i<paymentOrderResponse.sbProblem.problems.length; i++) {
                    msg = msg + "Problem #" + (i+1) + ": "
                        + paymentOrderResponse.sbProblem.problems[i].description + ".\n";
                }
                document.getElementById("msgDiv").setAttribute("style",
                    "background-color:#ffffc8; text-align: left; margin-top: 40px; padding: 10px");
                document.getElementById("msgField").innerText = msg;
            }
        }
    }

    function getInputRadioValue(inputName) {
        let radios = document.getElementsByName(inputName);
        for (let i = 0; i < radios.length; i++) {
            if (radios[i].checked) {
                return radios[i].value;
            }
        }
    }

    function getDeliveryEmailAddress() {
        if (document.getElementById("deliveryEmailAddress").checked) {
            return document.getElementById("email").value;
        } else {
            return "";
        }
    }

    function deliveryFieldDelShippingOffer() {
        document.getElementById("digitalProductsI").setAttribute("checked", "");
        document.getElementById("sameDayShippingI").removeAttribute("checked");
        document.getElementById("electronicDeliveryI")
            .setAttribute("checked", "checked");
        document.getElementById("sameDayShippingL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("sameDayShippingI").setAttribute("disabled", "");
        document.getElementById("overnightShippingL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("overnightShippingI").setAttribute("disabled", "");
        document.getElementById("twoDayShippingL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("twoDayShippingI").setAttribute("disabled", "");
    }

    function deliveryFieldDelElectronicDelivery() {
        document.getElementById("digitalProductsL").setAttribute("style", "color: #a9a9a9");
        document.getElementById("electronicDeliveryL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("electronicDeliveryI").setAttribute("disabled", "");
    }

    function preorderFieldDelShippingOffer() {
        document.getElementById("shipBillingAdrI").removeAttribute("checked");
        document.getElementById("digitalGoodsI")
            .setAttribute("checked", "checked");
        document.getElementById("shipBillingAdrL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("shipBillingAdrI").setAttribute("disabled", "");
        document.getElementById("shipVerifiedAdrL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("shipVerifiedAdrI").setAttribute("disabled", "");
        document.getElementById("shipDiffersAdrL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("shipDiffersAdrI").setAttribute("disabled", "");
    }

    function preorderFieldDelElectronicDelivery() {
        document.getElementById("digitalGoodsL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("digitalGoodsI").setAttribute("disabled", "");
        document.getElementById("digitalTicketsL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("digitalTicketsI").setAttribute("disabled", "");
        document.getElementById("digitalOtherL")
            .setAttribute("style", "color: #a9a9a9");
            // .setAttribute("style", "color: #a9a9a9; cursor: not-allowed");
        // document.getElementById("digitalOtherI").setAttribute("disabled", "");
    }

    function addOrderRow(i) {
        let tr = document.createElement("tr");
        let td = document.createElement("td");
        td.setAttribute("style", "width: 120px; text-align: center");
        let img = document.createElement("img");
        img.setAttribute("style", "height: 60px")
        img.setAttribute("src", $scope.publicOrderDataDTO.itemDTOList[i].imageUrl);
        td.appendChild(img);
        tr.appendChild(td);
        td = document.createElement("td");
        td.setAttribute("style", "width: 40%; font-size: 20px; text-align: left");
        td.innerText = $scope.publicOrderDataDTO.itemDTOList[i].name;
        tr.appendChild(td);
        td = document.createElement("td");
        td.innerText = $scope.publicOrderDataDTO.itemDTOList[i].quantity + " " +
            $scope.publicOrderDataDTO.itemDTOList[i].quantityUnit;
        tr.appendChild(td);
        td = document.createElement("td");
        td.innerText = $scope.publicOrderDataDTO.itemDTOList[i].unitPrice / 100 + " €";
        tr.appendChild(td);
        td = document.createElement("td");
        td.innerText = $scope.publicOrderDataDTO.itemDTOList[i].amount / 100 + " €";
        tr.appendChild(td);
        document.getElementById("orderTable").appendChild(tr);
    }

    function addOrderSummaryRow() {
        let tr = document.createElement("tr");
        tr.setAttribute("style", "background-color: #f9f9f9");
        let th = document.createElement("th");
        tr.appendChild(th);
        th = document.createElement("th");
        tr.appendChild(th);
        th = document.createElement("th");
        tr.appendChild(th);
        th = document.createElement("th");
        tr.appendChild(th);
        th = document.createElement("th");
        th.innerText = $scope.amountCount() + " €";
        tr.appendChild(th);
        document.getElementById("orderTable").appendChild(tr);
    }
});

Array.prototype.getAmountCount = function () {
    return this[0] * 100 +
        this[1] * 10 +
        this[2] * 1 +
        this[3] * 1 +
        this[4] * 100 +
        this[5] * 10 +
        this[6] * 1 +
        this[7] * 1;
};

Array.prototype.getTotalItemCount = function () {
    let totalItemCount = 0;
    for (let i = 0; i < 8; i++) totalItemCount = totalItemCount + this[i];
    return totalItemCount;
};

