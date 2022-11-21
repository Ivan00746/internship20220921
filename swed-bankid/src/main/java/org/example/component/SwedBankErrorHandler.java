package org.example.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.shop.SBProblem;
import org.example.service.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Slf4j
public class SwedBankErrorHandler implements ResponseErrorHandler {
    private final DataStore dataStore;

    public SwedBankErrorHandler(DataStore dataStore) {
        this.dataStore = dataStore;
    }

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
        int bodyByte, opBrCount;
        StringBuilder bodyStringBuilder = new StringBuilder();
        bodyStringBuilder.append((char) httpResponse.getBody().read());
        if (bodyStringBuilder.charAt(0) == 123) {
            opBrCount = 1;
            do {
                bodyByte = httpResponse.getBody().read();
                bodyStringBuilder.append((char) bodyByte);
                if (bodyByte == 123) ++opBrCount;
                if (bodyByte == 125) --opBrCount;
            } while (opBrCount != 0);
        }
        if (bodyStringBuilder.charAt(0) == 123 &&
                bodyStringBuilder.charAt(bodyStringBuilder.length() - 1) == 125) {
            ObjectMapper mapper = new ObjectMapper();
            SBProblem SBErrorObj = mapper.readValue(bodyStringBuilder.toString(), SBProblem.class);
            dataStore.setSbProblem(SBErrorObj);
            log.warn("<--Response error. Status:" + SBErrorObj.getStatus()
                    + ". " + SBErrorObj.getTitle() + ". " + SBErrorObj.getDetail() + ". "
                    + SBErrorObj.getProblems());
        } else {
            log.warn("<--Response error. Unknown error body. String:" + bodyStringBuilder);
        }
    }
}

