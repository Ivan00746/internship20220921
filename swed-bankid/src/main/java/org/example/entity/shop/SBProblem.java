package org.example.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SBProblem {
    String type; // "https://api.payex.com/psp/errordetail/<resource>/inputerror"
    String title; // "There was an input error"
    String detail; // "Please correct the errors and retry the request"
    String instance; // "ec2a9b09-601a-42ae-8e33-a5737e1cf177"
    int status; // 400
    ArrayList<Problem> problems;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Problem {
        String name; // "CreditCardParameters.Issuer"
        String description; // "minimum one issuer must be enabled"
    }
}
