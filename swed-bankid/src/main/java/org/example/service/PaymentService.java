package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.shop.*;
import org.example.entity.shop.dto.PaymentSettingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Slf4j
@Service
public class PaymentService {
    private final RestTemplate restTemplate;
    @Value("${application.swedBank.server.url}")
    private String swedBankServerUrl;
    @Value("${application.swedBank.token}")
    private String accessToken;
    @Value("${application.swedBank.paymentOrder.default.operation}")
    private String operation;
    @Value("${application.swedBank.paymentOrder.default.currency}")
    private String currency;
    @Value("${application.swedBank.paymentOrder.default.language}")
    private String language;
    @Value("${application.swedBank.paymentOrder.default.productName}")
    private String productName;
    @Value("${application.swedBank.paymentOrder.default.payer.payerReference}")
    private String payerReference;
    @Value("${application.swedBank.paymentOrder.default.orderItems.restrictedToInstruments}")
    private String restrictedToInstruments;
    @Value("${application.swedBank.paymentOrder.default.riskIndicator.pickUpAddress}")
    private String pickUpAddress;

    @Value("${application.swedBank.paymentOrder.defined.description}")
    private String description;
    @Value("${application.swedBank.paymentOrder.defined.payeeInfo.payeeId}")
    private String payeeId;
    @Value("${application.swedBank.paymentOrder.defined.payeeInfo.payeeName}")
    private String payeeName;
    @Value("${application.swedBank.paymentOrder.defined.riskIndicator.giftCardPurchase}")
    private Boolean giftCardPurchase;
    private final DataStore dataStore;

    @Autowired
    public PaymentService(@Qualifier("dataStoreCreate") DataStore dataStore,
                          @Qualifier("rtSwedBank") RestTemplate restTemplate) {
        this.dataStore = dataStore;
        this.restTemplate = restTemplate;
    }

    public PaymentOrderResponse paymentSettlement(PaymentSettingsDTO paymentSettings) {
        dataStore.getPaymentorder().setOperation(operation);
        dataStore.getPaymentorder().setCurrency(currency);
//        amount is defined below
//        vatAmount is defined below
        dataStore.getPaymentorder().setDescription(description);
//        userAgent is defined by CommonRestController
        dataStore.getPaymentorder().setLanguage(language);
        dataStore.getPaymentorder().setProductName(productName);

        dataStore.getPaymentorder().getUrls()
                .setHostUrls(new String[]{dataStore.getServerPath() + "/bankid"});
        dataStore.getPaymentorder().getUrls()
                .setCompleteUrl(dataStore.getServerPath() + "/bankid/payment_completed");
        dataStore.getPaymentorder().getUrls()
                .setCancelUrl(dataStore.getServerPath() + "/bankid/payment_cancelled");
        dataStore.getPaymentorder().getUrls()
                .setCallbackUrl(dataStore.getServerPath() + "/bankid/payment_callback");
//        termsOfServiceUrl supports HTTPS only:
        dataStore.getPaymentorder().getUrls().setTermsOfServiceUrl(null);

        dataStore.getPaymentorder().getPayeeInfo().setPayeeId(payeeId);
//        payeeReference is defined by CommonRestController
        dataStore.getPaymentorder().getPayeeInfo().setPayeeName(payeeName);
//        productCategory: null
//        orderReference is defined by CommonRestController

//        TODO -->Correct logic disabled for manual selection:
//        boolean onlyElectronicDelivery = true;
//        for (int i=0; i < 4; i++) {
//            if (dataStore.getOrder()[i].getQuantity() != 0) {
//                onlyElectronicDelivery = false;
//                break;
//            }
//        }

//        TODO -->Correct logic disabled for manual selection:
        dataStore.getPaymentorder().getPayer().setDigitalProducts(paymentSettings.getDigitalProducts());

        Payer.NationalIdentifier nationalIdentifier = new Payer.NationalIdentifier();
        nationalIdentifier.setSocialSecurityNumber(dataStore.getCollectInfo().getCompletionData().
                getUser().getPersonalNumber());
        nationalIdentifier.setCountryCode("SE");
        dataStore.getPaymentorder().getPayer().setNationalIdentifier(nationalIdentifier);

        dataStore.getPaymentorder().getPayer().setFirstName(dataStore.getCollectInfo().getCompletionData()
                .getUser().getGivenName());
        dataStore.getPaymentorder().getPayer().setLastName(dataStore.getCollectInfo().getCompletionData()
                .getUser().getSurname());
        dataStore.getPaymentorder().getPayer().setEmail(paymentSettings.getEmail());
        dataStore.getPaymentorder().getPayer().setMsisdn(paymentSettings.getMsisdn()
                .replaceAll(" |\\(|\\)|-", ""));
//      since there is SNN payerReference is not needed and not supported

        if (!paymentSettings.getDigitalProducts()) {
            if (dataStore.getPaymentorder().getPayer().getShippingAddress() == null)
                dataStore.getPaymentorder().getPayer().setShippingAddress(new Payer.ShippingAddress());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setFirstName(paymentSettings
                    .getFirstNameShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setLastName(paymentSettings
                    .getLastNameShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setEmail(paymentSettings
                    .getEmailShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setMsisdn(paymentSettings
                    .getMsisdnShipping().replaceAll(" |\\(|\\)|-", ""));
            dataStore.getPaymentorder().getPayer().getShippingAddress().setStreetAddress(
                    paymentSettings.getStreetAddressShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setCity(paymentSettings.getCityShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setZipCode(paymentSettings.getZipCodeShipping());
            dataStore.getPaymentorder().getPayer().getShippingAddress().setCountryCode(paymentSettings.getCountryCodeShipping());
        } else dataStore.getPaymentorder().getPayer().setShippingAddress(null);

        dataStore.getPaymentorder().getPayer().getBillingAddress()
                .setFirstName(dataStore.getCollectInfo().getCompletionData().getUser().getGivenName());
        dataStore.getPaymentorder().getPayer().getBillingAddress().
                setLastName(dataStore.getCollectInfo().getCompletionData().getUser().getSurname());
        dataStore.getPaymentorder().getPayer().getBillingAddress().setEmail(paymentSettings.getEmail());
        dataStore.getPaymentorder().getPayer().getBillingAddress()
                .setMsisdn(paymentSettings.getMsisdn().replaceAll(" |\\(|\\)|-", ""));
        dataStore.getPaymentorder().getPayer().getBillingAddress().setStreetAddress(
                paymentSettings.getStreetAddress());
        dataStore.getPaymentorder().getPayer().getBillingAddress().setCity(paymentSettings.getCity());
        dataStore.getPaymentorder().getPayer().getBillingAddress().setZipCode(paymentSettings.getZipCode());
        dataStore.getPaymentorder().getPayer().getBillingAddress().setCountryCode(paymentSettings.getCountryCode());

        dataStore.getPaymentorder().getPayer().getAccountInfo().setAccountAgeIndicator(paymentSettings
                .getAccountAgeIndicator());
        dataStore.getPaymentorder().getPayer().getAccountInfo().setAccountChangeIndicator(paymentSettings
                .getAccountChangeIndicator());
        dataStore.getPaymentorder().getPayer().getAccountInfo().setAccountPwdChangeIndicator(paymentSettings
                .getAccountPwdChangeIndicator());
        dataStore.getPaymentorder().getPayer().getAccountInfo().setShippingAddressUsageIndicator(paymentSettings
                .getShippingAddressUsageIndicator());
        if (!paymentSettings.getDigitalProducts()) {
            dataStore.getPaymentorder().getPayer().getAccountInfo()
                    .setShippingNameIndicator((dataStore.getPaymentorder().getPayer().getFirstName() +
                            dataStore.getPaymentorder().getPayer().getLastName()).equals(
                            (dataStore.getPaymentorder().getPayer().getShippingAddress().getFirstName() +
                                    dataStore.getPaymentorder().getPayer().getShippingAddress()
                                            .getLastName())) ? "01" : "02");
        } else dataStore.getPaymentorder().getPayer().getAccountInfo().setShippingNameIndicator("01");
        dataStore.getPaymentorder().getPayer().getAccountInfo().setSuspiciousAccountActivity(paymentSettings
                .getSuspiciousAccountActivity());

        int amount = 0, vatAmount = 0;
        ArrayList<OrderItems> orderItems = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (dataStore.getOrder()[i].getQuantity() > 0)
                orderItems.add(dataStore.getOrder()[i]);
            amount = amount + dataStore.getOrder()[i].getAmount();
            vatAmount = vatAmount + dataStore.getOrder()[i].getVatAmount();
        }
        dataStore.getPaymentorder().setOrderItems(orderItems);
        dataStore.getPaymentorder().setAmount(amount);
        dataStore.getPaymentorder().setVatAmount(vatAmount);
        String deliveryEmailAddress = paymentSettings.getDeliveryEmailAddress();
        if (!deliveryEmailAddress.equals("")) dataStore.getPaymentorder().getRiskIndicator()
                .setDeliveryEmailAddress(deliveryEmailAddress);
        dataStore.getPaymentorder().getRiskIndicator()
                .setDeliveryTimeframeIndicator(paymentSettings.getDeliveryTimeframeIndicator());
        String preOrderDate = paymentSettings.getPreOrderDate().replaceAll("-|\\.|/", "");
        if (!preOrderDate.equals("")) dataStore.getPaymentorder().getRiskIndicator().setPreOrderDate(preOrderDate);
        dataStore.getPaymentorder().getRiskIndicator()
                .setPreOrderPurchaseIndicator(paymentSettings.getPreOrderPurchaseIndicator());
        dataStore.getPaymentorder().getRiskIndicator()
                .setShipIndicator(paymentSettings.getShipIndicator());
        dataStore.getPaymentorder().getRiskIndicator()
                .setGiftCardPurchase(giftCardPurchase);
        dataStore.getPaymentorder().getRiskIndicator()
                .setReOrderPurchaseIndicator(paymentSettings.getReOrderPurchaseIndicator());

        String url = swedBankServerUrl + "/psp/paymentorders";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("User-Agent", dataStore.getPaymentorder().getUserAgent());
        headers.add("Accept", "application/json; q=0.9");
        headers.add("Forwarded", dataStore.getCollectInfo()
                .getCompletionData().getDevice().getIpAddress());
        log.debug("-->PaymentOrder request to " + url);
        log.debug("   PaymentOrderRequest(Object): " + dataStore.getPaymentorder());
        HttpEntity<PaymentOrderRequest> httpEntity =
                new HttpEntity<>(new PaymentOrderRequest(dataStore.getPaymentorder()), headers);
        PaymentOrderResponse response = new PaymentOrderResponse();
        try {
            String responseString = restTemplate
                    .postForObject(url, httpEntity, String.class);
            if (responseString != null) {
                ObjectMapper mapper = new ObjectMapper();
                response = mapper.readValue(responseString, PaymentOrderResponse.class);
            }
            if (response.getPaymentOrder() != null) {
                log.debug("<--Response(String): " + responseString);
                log.trace("   PaymentOrderResponse(Object): " + response);
            } else {
                response.setSbProblem(dataStore.getSbProblem());
                dataStore.setSbProblem(null);
            }
        } catch (JsonProcessingException e) {
            log.error(e.toString());
        }
        dataStore.setPaymentOrderResponse(response);
        return response;
    }

    public void getAfterPaymentOperations() {
        String url = swedBankServerUrl + dataStore.getPaymentOrderResponse().getPaymentOrder().getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + accessToken);
        log.debug("-->GetAfterPaymentOperations request to " + url);
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate
                    .exchange(url, HttpMethod.GET, requestEntity, String.class);
            String responseString = response.getBody();
            log.debug("<--GetAfterPaymentOperations response(String): " + responseString);
            ObjectMapper mapper = new ObjectMapper();
            GetPaymentResponse getResponse = mapper.readValue(responseString, GetPaymentResponse.class);
            log.trace("   GetAfterPaymentOperations(Object): " + getResponse);
            if (getResponse != null) {
                dataStore.setGetPaymentResponse(getResponse);
            }
        } catch (JsonProcessingException e) {
            log.error(e.toString());
        }
    }
}
