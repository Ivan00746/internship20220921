package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.shop.GetPaymentResponse;
import org.example.entity.shop.PaymentOrderResponse;
import org.example.entity.shop.dto.PaymentSettingsDTO;
import org.example.service.DataStore;
import org.example.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/bankid")
public class SwedBankRestController {
    @Autowired
    PaymentService paymentService;
    @Autowired
    DataStore dataStore;

    @RequestMapping(path = "/setUserData", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
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
    public GetPaymentResponse getPastPaymentResponse() {
        return dataStore.getGetPaymentResponse();
    }

    //  The URL to the API endpoint receiving POST requests on transaction activity related to the payment order.
    @PostMapping("/payment_callback")
    public void paymentCallbackPost(@RequestBody String s) {
        log.info("Received PostRequest to /payment_callback with param:" + s);
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        log.info("Session ID#" + dataStore.getSessionId() + " is invalidated...");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
//        ClientBankIdTest.restart();
        return "logout_OK";
    }
}