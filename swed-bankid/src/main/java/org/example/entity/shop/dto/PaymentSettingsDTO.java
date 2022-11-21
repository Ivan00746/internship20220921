package org.example.entity.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSettingsDTO {
    private String email;
    private String msisdn;
    private String streetAddress;
    private String city;
    private String zipCode;
    private String countryCode;
    private String emailShipping;
    private String msisdnShipping;
    private String firstNameShipping;
    private String lastNameShipping;
    private String streetAddressShipping;
    private String cityShipping;
    private String zipCodeShipping;
    private String countryCodeShipping;
    private String accountAgeIndicator;
    private String accountChangeIndicator;
    private String accountPwdChangeIndicator;
    private String shippingAddressUsageIndicator;
    private String suspiciousAccountActivity;
    private String deliveryEmailAddress;
    private Boolean digitalProducts;
    private String deliveryTimeframeIndicator;
    private String preOrderDate;
    private String preOrderPurchaseIndicator;
    private String shipIndicator;
    private String reOrderPurchaseIndicator;
}
