package org.example.entity.bankIdAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionData {
    @NonNull
    private User user;
    @NonNull
    private Device device;
    @NonNull
    private Cert cert;
    private String signature;
    private String ocspResponse;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cert {
        private String notBefore;
        private String notAfter;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Device {
        private String ipAddress;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String personalNumber;
        private String name;
        private String givenName;
        private String surname;
    }
}