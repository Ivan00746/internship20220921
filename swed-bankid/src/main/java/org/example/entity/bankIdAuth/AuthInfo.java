package org.example.entity.bankIdAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthInfo {
    private String orderRef;
    private String autoStartToken;
    private String qrStartToken;
    private String qrStartSecret;
}
