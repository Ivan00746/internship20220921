package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bankid")
public class SwitchController {

    @GetMapping("")
    public String index() {
        return "toolsMenuPage";
    }

    @GetMapping("/qr_page")
    public String logInPage() {
        return "logInPage";
    }

    @GetMapping("/authsuccess_page")
    public String authSuccessPage() {
        return "authSuccessPage";
    }
}
