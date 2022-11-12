package org.example.config;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.example.component.ServerErrorHandler;
import org.example.entity.bankIdAuth.CollectInfo;
import org.example.entity.shop.OrderItems;
import org.example.entity.shop.Payer;
import org.example.entity.shop.PaymentOrderRequest;
import org.example.service.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import static org.springframework.util.ResourceUtils.getFile;

@Configuration
public class ContextConfig {
    private static final String CERTPASSWORD = "qwerty123", STOREPASSWORD = "storepass";
    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Bean
    @SessionScope
    public DataStore dataStoreCreate() {
        CollectInfo collectInfo = new CollectInfo();
        PaymentOrderRequest.Paymentorder paymentorder = new PaymentOrderRequest.Paymentorder(
                new PaymentOrderRequest.Paymentorder.Urls(),
                new PaymentOrderRequest.Paymentorder.PayeeInfo(),
                new Payer(
                        new Payer.NationalIdentifier(),
                        new Payer.ShippingAddress(),
                        new Payer.BillingAddress(),
                        new Payer.AccountInfo()),
                new ArrayList<OrderItems>(),
                new PaymentOrderRequest.Paymentorder.RiskIndicator(
                        new PaymentOrderRequest.Paymentorder.RiskIndicator.PickUpAddress()));
        OrderItems[] order = new OrderItems[8];
        return new DataStore(collectInfo, paymentorder, order,
                String.valueOf(webServerAppCtxt.getWebServer().getPort()));
    }

    @Bean
    @SessionScope
    public RestTemplate rtBankId(@Qualifier("dataStoreCreate") DataStore dataStore) {
        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder
                    .create()
                    .loadKeyMaterial(getFile("classpath:keystorefile.jks"),
                            STOREPASSWORD.toCharArray(), CERTPASSWORD.toCharArray())
                    .loadTrustMaterial(getFile("classpath:truststorefile.jks"),
                            STOREPASSWORD.toCharArray(), new TrustSelfSignedStrategy())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException |
                 CertificateException | IOException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClientBuilder.create().setSSLContext(sslContext).build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(client);
//        TimeOut settings: clientHttpRequestFactory.setConnectTimeout(3000) + .setReadTimeout(5000) + ...
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
//        RestTemplate ErrorHandler setting:
        restTemplate.setErrorHandler(new ServerErrorHandler(dataStore));
        return restTemplate;
    }
}
