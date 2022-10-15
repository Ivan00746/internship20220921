package org.example.config;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.example.component.ServerErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.springframework.util.ResourceUtils.getFile;

@Configuration
public class RestTemplateConfig {
    private static final String CERTPASSWORD = "qwerty123", STOREPASSWORD = "storepass";

    @Bean
    public RestTemplate rt() {
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
        restTemplate.setErrorHandler(new ServerErrorHandler());
        return restTemplate;
    }
}
