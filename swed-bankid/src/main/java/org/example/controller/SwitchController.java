package org.example.controller;

import org.example.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bankid")
public class SwitchController {
    @Autowired
    PaymentService paymentService;

    @GetMapping("/payment_completed")
    public String paymentComplete() {
        paymentService.getAfterPaymentOperations();
        return "paymentHandle";
    }
}
