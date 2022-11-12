package org.example.entity.bankIdAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchInfo {
    private String orderRef;
    private String autoStartToken;
}
