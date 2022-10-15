package org.example.service;

import org.example.ClientBankIdTest;
import org.example.entity.AuthInfo;
import org.example.entity.CollectInfo;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.logging.Logger;

@Service
public class SiteServiceRest {
    @Autowired
    private final RestTemplate restTemplate;
    private final String serverUrl;
    private static final int MIN_AUTH_RESPONSE_TIMEOUT_SEC = 3,
            MIN_COLLECT_RESPONSE_TIMEOUT_SEC = 20;
    private static AuthInfo authInfo;
    private static CollectInfo collectInfo;
    private static boolean usedDesktopApp, launchSuccess;
    private static Instant authResponseTime;
    private static int requestIndex = 0;
    private static final Logger log = Logger.getLogger(ClientBankIdTest.class.getName());

    public SiteServiceRest(
            RestTemplate restTemplate, @Value("${application.server.url}") String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public AuthInfo connectionRequest(String clientIp) {
        String url = serverUrl + "/rp/v5.1/auth";
        String requestBody = "{\"endUserIp\":\"" + clientIp + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
        try {
            log.info("--> Request to /rp/v5.1/auth method;");
            log.info("    Request body: " + requestBody + ";");
            authInfo = restTemplate.postForObject(new URI(url), httpEntity, AuthInfo.class);
            if (authInfo != null) {
                authResponseTime = Instant.now();
                log.info("<-- Response from /rp/v5.1/auth method with AuthInfo");
                log.info("    OrderRef: " + authInfo.getOrderRef() + ";");
                log.fine("    AutoStartToken: " + authInfo.getAutoStartToken() + ";");
                log.fine("    QrStartToken: " + authInfo.getQrStartToken() + ";");
                log.fine("    QrStartSecret: " + authInfo.getQrStartSecret() + ";");
                requestIndex = 0;
                return authInfo;
            } else
                throw new ResourceAccessException("org.springframework.web.client.ResourceAccessException: Error response from \"https://appapi2.test.bankid.com/rp/v5.1/auth\": appapi2.test.bankid.com");
        } catch (ResourceAccessException e) {
            if (requestIndex < MIN_AUTH_RESPONSE_TIMEOUT_SEC) {
                ++requestIndex;
                log.severe("Exception:" + e);
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
        if (authInfo != null && (collectInfo == null || collectInfo.getStatus().equals("pending"))) {
            String url = serverUrl + "/rp/v5.1/collect";
            String requestBody = "{\"orderRef\":\"" + authInfo.getOrderRef() + "\"}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            try {
                log.info("--> Request to /rp/v5.1/collect method;");
                log.info("    Request body: " + requestBody + ";");
                CollectInfo instantCollectInfo = restTemplate.postForObject(new URI(url), httpEntity, CollectInfo.class);
                if (instantCollectInfo != null && instantCollectInfo.getOrderRef() != null) {
                    collectInfo = instantCollectInfo;
                    log.info("<-- Response from /rp/v5.1/collect method with CollectInfo:");
                    log.fine("    OrderRef: " + collectInfo.getOrderRef() + ";");
                    log.info("    Status: " + collectInfo.getStatus() + ";");
                    log.info("    HintCode: " + collectInfo.getHintCode() + ";");
                    log.fine("    CompletionData: " + (collectInfo.getCompletionData() != null ?
                            collectInfo.getCompletionData().getUser() + ", " +
                                    collectInfo.getCompletionData().getDevice() + ";" : "null"));
                }
            } catch (ResourceAccessException e) {
                if (requestIndex < MIN_COLLECT_RESPONSE_TIMEOUT_SEC) {
                    ++requestIndex;
                    log.severe("Exception:" + e);
                } else {
                    collectInfo = new CollectInfo();
                    collectInfo.setStatus("failed");
                    collectInfo.setHintCode("");
                    requestIndex = 0;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (authInfo == null) {
            collectInfo = new CollectInfo();
            collectInfo.setStatus("failed");
            requestIndex = 0;
        }
        return collectInfo;
    }

//  Not used method:
    public String logOut() {
        String url = serverUrl + "/rp/v5.1/cancel";
        String requestBody = "{\"orderRef\":\"" + collectInfo.getOrderRef() + "\"}";
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

    public static Instant getAuthResponseTime() {
        return authResponseTime;
    }

    public static AuthInfo getAuthInfo() {
        return authInfo;
    }

    public static void setAuthInfo(AuthInfo authInfo) {
        SiteServiceRest.authInfo = authInfo;
    }

    public static CollectInfo getCollectInfo() {
        return collectInfo;
    }

    public static void setCollectInfo(CollectInfo collectInfo) {
        SiteServiceRest.collectInfo = collectInfo;
    }

    public static boolean isUsedDesktopApp() {
        return usedDesktopApp;
    }

    public static void setUsedDesktopApp(boolean usedDesktopApp) {
        SiteServiceRest.usedDesktopApp = usedDesktopApp;
    }

    public static boolean isLaunchSuccess() {
        return launchSuccess;
    }

    public static void setLaunchSuccess(boolean launchSuccess) {
        SiteServiceRest.launchSuccess = launchSuccess;
    }
}
