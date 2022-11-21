package org.example.service;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.bankIdAuth.AuthInfo;
import org.example.entity.bankIdAuth.CollectInfo;
import org.example.entity.shop.*;

import java.time.Instant;

@Slf4j
@Data
public class DataStore {
    private final String serverPath;
    private final String sessionId;

//  BankId variables:
    private AuthInfo authInfo;
    @NonNull
    private CollectInfo collectInfo;
    private Instant authResponseTime;
    private boolean usedDesktopApp, launchSuccess;

//  Shop variables:
    @NonNull
    private OrderItems[] order;
    @NonNull
    private PaymentOrderRequest.Paymentorder paymentorder;
    private PaymentOrderResponse paymentOrderResponse;
    private GetPaymentResponse getPaymentResponse;
    private SBProblem sbProblem;

    public DataStore(CollectInfo collectInfo,
                     PaymentOrderRequest.Paymentorder paymentorder,
                     OrderItems[] order, String serverPath, String sessionId) {
        this.collectInfo = collectInfo;
        this.paymentorder = paymentorder;
        this.order = order;
        this.serverPath = serverPath;
        this.sessionId = sessionId;
        productRangFill("New session ID#" + sessionId + " is started. Session dataStore is filled...");
    }

    public void productRangFill(String msg) {
        log.info(msg);
        order[0] = new OrderItems("G1", "T-shirt Anton", "PRODUCT", "Goods",
                serverPath + "/bankid",
                serverPath + "/shop/images/1_shutterstock_122602567.png",
                "", "", "pcs", 10000, 2500);
        order[1] = new OrderItems("G2", "T-shirt Ted", "PRODUCT", "Goods",
                serverPath + "/bankid",
                serverPath + "/shop/images/2_shutterstock_127698176.png",
                "", "", "pcs", 1000, 2500);
        order[2] = new OrderItems("G3", "T-shirt Mathias", "PRODUCT", "Goods",
                serverPath + "/bankid",
                serverPath + "/shop/images/3_shutterstock_105769478.png",
                "", "", "pcs", 100, 2500);
        order[3] = new OrderItems("G4", "T-shirt Benny", "PRODUCT", "Goods",
                serverPath + "/bankid",
                serverPath + "/shop/images/4_shutterstock_89766265.png",
                "", "", "pcs", 100, 2500);
        order[4] = new OrderItems("S5", "Value code Energetic bouquet", "SERVICE", "Services",
                serverPath + "/bankid",
                serverPath + "/shop/images/1_shutterstock_120567646.png",
                "", "", "srv", 10000, 0);
        order[5] = new OrderItems("S6", "Value code Happy bouquet", "SERVICE", "Services",
                serverPath + "/bankid",
                serverPath + "/shop/images/2_shutterstock_128790185.png",
                "", "", "srv", 1000, 0);
        order[6] = new OrderItems("S7", "Value code Blue bouquet", "SERVICE", "Services",
                serverPath + "/bankid",
                serverPath + "/shop/images/3_shutterstock_94660375.png",
                "", "", "srv", 100, 0);
        order[7] = new OrderItems("S8", "Value code Grateful bouquet", "SERVICE", "Services",
                serverPath + "/bankid",
                serverPath + "/shop/images/4_shutterstock_95018155.png",
                "", "", "srv", 100, 0);
    }
}
