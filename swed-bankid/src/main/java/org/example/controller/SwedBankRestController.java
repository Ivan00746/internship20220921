package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.bankIdAuth.CollectInfo;
import org.example.entity.shop.*;
import org.example.entity.shop.dto.PaymentSettingsDTO;
import org.example.service.DataStore;
import org.example.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/bankid")
public class SwedBankRestController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    DataStore dataStore;

    @RequestMapping(path = "/setUserData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public PaymentOrderResponse setUserData(@RequestBody String settingsJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentSettingsDTO paymentSettings = objectMapper.readValue(settingsJson, PaymentSettingsDTO.class);
        return paymentService.paymentSettlement(paymentSettings);
    }

    @GetMapping("/get_payment_response")
    public PaymentOrderResponse getPaymentResponse() {
        return dataStore.getPaymentOrderResponse();
    }


    @GetMapping("/get_pastpayment_response")
    public GetPaymentResponse getPastPaymentResponse () {
        return dataStore.getGetPaymentResponse();
    }

//  The URL to the API endpoint receiving POST requests on transaction activity related to the payment order.
    @PostMapping ("/payment_callback")
    public void paymentCallbackPost (@RequestBody String s) {
        System.out.println("Received PostRequest to /payment_callback with param:" + s);
    }

    @GetMapping("/logout")
    public String logout() {
        CollectInfo collectInfo = new CollectInfo();
        dataStore.setCollectInfo(collectInfo);
        PaymentOrderRequest.Paymentorder paymentorder = new PaymentOrderRequest.Paymentorder(
                new PaymentOrderRequest.Paymentorder.Urls(),
                new PaymentOrderRequest.Paymentorder.PayeeInfo(),
                new Payer(
                        new Payer.NationalIdentifier(),
                        new Payer.ShippingAddress(),
                        new Payer.BillingAddress(),
                        new Payer.AccountInfo()),
                new ArrayList<OrderItems>(),
                new PaymentOrderRequest.Paymentorder.RiskIndicator(
                        new PaymentOrderRequest.Paymentorder.RiskIndicator.PickUpAddress()));
        dataStore.setPaymentorder(paymentorder);
        dataStore.setOrder(new OrderItems[8]);
        dataStore.productRangFill("Session data is refreshed. DataStore is refilled...");
        return "logout_OK";
    }

}