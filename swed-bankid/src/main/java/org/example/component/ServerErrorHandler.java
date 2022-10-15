package org.example.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ClientBankIdTest;
import org.example.entity.CollectInfo;
import org.example.service.SiteServiceRest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class ServerErrorHandler implements ResponseErrorHandler {

    private static final Logger log = Logger.getLogger(ClientBankIdTest.class.getName());

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == CLIENT_ERROR ||
                httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            responseParser(httpResponse, "SERVER_ERROR");
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            responseParser(httpResponse, "CLIENT_ERROR");
        }
    }

    private void responseParser(ClientHttpResponse httpResponse, String errorType)
            throws IOException {
        int bodyByte;
        String errorCode = "", details = "";
        StringBuilder bodyStringBuilder = new StringBuilder();
        do {
            bodyByte = httpResponse.getBody().read();
            bodyStringBuilder.append((char) bodyByte);
            if (bodyStringBuilder.charAt(0) != 123) break;
        } while (bodyByte != 125);
        if (bodyStringBuilder.charAt(0) == 123 &&
                bodyStringBuilder.charAt(bodyStringBuilder.length() - 1) == 125) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> responseBodyMap = mapper.readValue(bodyStringBuilder.toString(), Map.class);
            errorCode = responseBodyMap.get("errorCode");
            details = responseBodyMap.get("details");
            log.warning("<-- " + errorType + " response from /rp/v5.1/...; " +
                    httpResponse.getRawStatusCode() + ":" + httpResponse.getStatusText() +
                    "; errorCode:" + errorCode + "; details:" + details);
        } else {
            log.warning("<-- " + errorType + " response from /rp/v5.1/...; " +
                    httpResponse.getRawStatusCode() + ":" + httpResponse.getStatusText() +
                    "; no/broken response body");
        }
        if (SiteServiceRest.getCollectInfo() == null) {
            SiteServiceRest.setCollectInfo(new CollectInfo());
        }
        if (!errorCode.equals("invalidParameters")) {
            SiteServiceRest.getCollectInfo().setStatus("failed");
            SiteServiceRest.getCollectInfo().setHintCode(errorCode);
        }
    }
}

