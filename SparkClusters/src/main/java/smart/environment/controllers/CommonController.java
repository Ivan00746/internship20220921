package smart.environment.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("")
    public String getRowCount() {
        return  "Endpoints >>> 1. Selection method report with local default settings: 'host/localDefault/selectionReport'; " +
                "2. Sort method report with local default settings: 'host/localDefault/sortingReport'";
    }
}
