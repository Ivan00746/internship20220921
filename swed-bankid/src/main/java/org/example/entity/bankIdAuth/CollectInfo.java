package org.example.entity.bankIdAuth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectInfo {
    private String orderRef;
    private String status;
    private String hintCode;
    @NonNull
    private CompletionData completionData;

    public CollectInfo() {
        this.completionData = new CompletionData(
                new CompletionData.User(),
                new CompletionData.Device(),
                new CompletionData.Cert());
    }
}
