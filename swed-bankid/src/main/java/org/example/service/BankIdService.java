package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.bankIdAuth.AuthInfo;
import org.example.entity.bankIdAuth.CollectInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@Slf4j
@Service
public class BankIdService {
    private static final int MIN_AUTH_RESPONSE_TIMEOUT_SEC = 3;
    private static final int MIN_COLLECT_RESPONSE_TIMEOUT_SEC = 20;
    private final RestTemplate restTemplate;
    private final DataStore dataStore;

    private int requestIndex = 0;

    @Value("${application.bankId.server.url}")
    private String bankIdServerUrl;

    @Autowired
    public BankIdService(@Qualifier("rtBankId") RestTemplate restTemplate,
                         @Qualifier("dataStoreCreate") DataStore dataStore) {
        this.restTemplate = restTemplate;
        this.dataStore = dataStore;
    }

    public AuthInfo connectionRequest(String clientIp) {
        String url = bankIdServerUrl + "/rp/v5.1/auth";
        String requestBody = "{\"endUserIp\":\"" + clientIp + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        try {
            log.debug("--> Request to /rp/v5.1/auth method;");
            log.debug("    Request body: " + requestBody + ";");
            dataStore.setAuthInfo(restTemplate
                    .postForObject(new URI(url), httpEntity, AuthInfo.class));
            if (dataStore.getAuthInfo() != null) {
                dataStore.setAuthResponseTime(Instant.now());
                log.debug("<-- Response from /rp/v5.1/auth method with AuthInfo");
                log.debug("    OrderRef: " + dataStore.getAuthInfo().getOrderRef() + ";");
                log.trace("    AutoStartToken: " + dataStore.getAuthInfo().getAutoStartToken() + ";");
                log.trace("    QrStartToken: " + dataStore.getAuthInfo().getQrStartToken() + ";");
                log.trace("    QrStartSecret: " + dataStore.getAuthInfo().getQrStartSecret() + ";");
                requestIndex = 0;
                return dataStore.getAuthInfo();
            } else
                throw new ResourceAccessException("org.springframework.web.client.ResourceAccessException: Error response from \"https://appapi2.test.bankid.com/rp/v5.1/auth\": appapi2.test.bankid.com");
        } catch (ResourceAccessException e) {
            if (requestIndex < MIN_AUTH_RESPONSE_TIMEOUT_SEC) {
                ++requestIndex;
                log.error("Exception:" + e);
                try {
                    Thread.sleep(1000);
                    connectionRequest(clientIp);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
            requestIndex = 0;
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CollectInfo collectRequest() {
        if (dataStore.getAuthInfo() != null && (dataStore.getCollectInfo().getStatus() == null || dataStore.getCollectInfo().getStatus().equals("pending"))) {
            String url = bankIdServerUrl + "/rp/v5.1/collect";
            String requestBody = "{\"orderRef\":\"" + dataStore.getAuthInfo().getOrderRef() + "\"}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            try {
                log.debug("--> Request to /rp/v5.1/collect method;");
                log.debug("    Request body: " + requestBody + ";");
                CollectInfo instantCollectInfo = restTemplate.
                        postForObject(new URI(url), httpEntity, CollectInfo.class);
                if (instantCollectInfo != null && instantCollectInfo.getOrderRef() != null) {
                    dataStore.setCollectInfo(instantCollectInfo);
                    log.debug("<-- Response from /rp/v5.1/collect method with DataStore.getCollectInfo():");
                    log.trace("    OrderRef: " + dataStore.getCollectInfo().getOrderRef() + ";");
                    log.debug("    Status: " + dataStore.getCollectInfo().getStatus() + ";");
                    log.debug("    HintCode: " + dataStore.getCollectInfo().getHintCode() + ";");
                    log.trace("    CompletionData: " + dataStore.getCollectInfo().getCompletionData().getUser() + ", "
                            + dataStore.getCollectInfo().getCompletionData().getDevice() + ";");
                }
            } catch (ResourceAccessException e) {
                if (requestIndex < MIN_COLLECT_RESPONSE_TIMEOUT_SEC) {
                    ++requestIndex;
                    log.error("Exception:" + e);
                } else {
                    dataStore.getCollectInfo().setStatus("failed");
                    dataStore.getCollectInfo().setHintCode("");
                    requestIndex = 0;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (dataStore.getAuthInfo() == null) {
            dataStore.getCollectInfo().setStatus("failed");
            requestIndex = 0;
        }
        return dataStore.getCollectInfo();
    }

    //  Not used method:
    public String logOut() {
        String url = bankIdServerUrl + "/rp/v5.1/cancel";
        String requestBody = "{\"orderRef\":\"" +
                dataStore.getCollectInfo().getOrderRef() + "\"}";
        System.out.println(requestBody);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        try {
            return restTemplate.postForObject(new URI(url), httpEntity, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
