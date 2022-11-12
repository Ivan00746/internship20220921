package org.example.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;
@Data
public class Payer {
    private boolean digitalProducts; // false,
    @NonNull
    private NationalIdentifier nationalIdentifier;
    private String firstName;// "Leia"
    private String lastName;// "Ahlström"
    private String email;// "leia@payex.com",
    private String msisdn;// "+46787654321",
    private String payerReference;//not needed - "AB1234",
    @NonNull
    private ShippingAddress shippingAddress;
    @NonNull
    private BillingAddress billingAddress;
    @NonNull
    private AccountInfo accountInfo;

    @Data
    public static class NationalIdentifier {
        private String socialSecurityNumber;//!!! "199710202392"
        private String countryCode;//!!! "SE"
    }

    @Data
    public static class ShippingAddress {
        private String firstName;// "firstname/companyname",
        private String lastName;// "lastname",
        private String email;// "karl.anderssson@mail.se",
        private String msisdn;// "+46759123456",
        private String streetAddress;// "Helgestavägen 9",
        private String coAddress;// ""
        private String city;// "Solna"
        private String zipCode;//--> "17674"
        private String countryCode;// "SE"
    }

    @Data
    public static class BillingAddress {
        private String firstName;//!!! "firstname/companyname",
        private String lastName;//!!! "lastname",
        private String email;// "karl.anderssson@mail.se",
        private String msisdn;// "+46759123456",
        private String streetAddress;// "Helgestavägen 9",
        private String coAddress;// "",
        private String city;// "Solna",
        private String zipCode;// "17674",
        private String countryCode;// "SE"
    }

    @Data
    public static class AccountInfo {
        private String accountAgeIndicator;// "04"
        //Indicates the age of the payer’s account.
        //01 (No account, guest checkout)
        //02 (Created during this transaction)
        //03 (Less than 30 days old)
        //04 (30 to 60 days old)
        //05 (More than 60 days old)
        private String accountChangeIndicator;// "04"
        //Indicates when the last account changes occurred.
        //01 (Changed during this transaction)
        //02 (Less than 30 days ago)
        //03 (30 to 60 days ago)
        //04 (More than 60 days ago)
        private String accountPwdChangeIndicator;// "01"
        //Indicates when the account’s password was last changed.
        //01 (No changes)
        //02 (Changed during this transaction)
        //03 (Less than 30 days ago)
        //04 (30 to 60 days ago)
        //05 (More than 60 days old)
        private String shippingAddressUsageIndicator;// "01"
        //Indicates when the payer’s shipping address was last used.
        //01(This transaction)
        //02 (Less than 30 days ago)
        //03 (30 to 60 days ago)
        //04 (More than 60 days ago)
        private String shippingNameIndicator;// "01"
        //Indicates if the account name matches the shipping name.
        //01 (Account name identical to shipping name)
        //02 (Account name different from shipping name)
        private String suspiciousAccountActivity;// "01"
        //Indicates if there have been any suspicious activities linked to this account.
        //01 (No suspicious activity has been observed)
        //02 (Suspicious activity has been observed)
    }
}
