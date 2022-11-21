package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.component.DynamicQR;
import org.example.entity.bankIdAuth.*;
import org.example.entity.shop.dto.OrderItemDTO;
import org.example.entity.shop.dto.PublicOrderDataDTO;
import org.example.service.BankIdService;
import org.example.service.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

@RestController
@RequestMapping("/bankid")
public class CommonRestController {
    @Autowired
    private BankIdService bankIdService;
    @Autowired
    private DynamicQR dynamicQR;
    @Autowired
    private DataStore dataStore;

    @GetMapping("/getCart")
    public PublicOrderDataDTO getCart() {
        String userAgent = ((ServletRequestAttributes) RequestContextHolder.
                currentRequestAttributes()).getRequest().getHeader("User-Agent");
        dataStore.getPaymentorder().setUserAgent(userAgent);
        PublicOrderDataDTO publicOrderDataDTO = new PublicOrderDataDTO();
        ArrayList<OrderItemDTO> orderDTO = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (dataStore.getOrder()[i].getQuantity() != 0) {
                OrderItemDTO itemDTO = new OrderItemDTO(i + 1,
                        dataStore.getOrder()[i].getImageUrl(),
                        dataStore.getOrder()[i].getName(),
                        dataStore.getOrder()[i].getQuantity(),
                        dataStore.getOrder()[i].getQuantityUnit(),
                        dataStore.getOrder()[i].getUnitPrice(),
                        dataStore.getOrder()[i].getAmount());
                orderDTO.add(itemDTO);
            }
        }
        if (orderDTO.size() != 0) {
            if (dataStore.getPaymentorder().getPayeeInfo().getPayeeReference() == null &&
                    dataStore.getCollectInfo().getCompletionData()
                            .getUser().getPersonalNumber() != null) {
//                long orderNumber = System.nanoTime() % 1000000000000L;
                long orderNumber = System.nanoTime();
                dataStore.getPaymentorder().getPayeeInfo()
                        .setPayeeReference("Hi9d813f7a" + orderNumber);
                dataStore.getPaymentorder().getPayeeInfo()
                        .setOrderReference("HiOrder" + orderNumber);
            }
            publicOrderDataDTO.setPayeeReference(
                    dataStore.getPaymentorder().getPayeeInfo().getPayeeReference());
            publicOrderDataDTO.setGivenName(
                    dataStore.getCollectInfo().getCompletionData().getUser().getGivenName());
            publicOrderDataDTO.setSurname(
                    dataStore.getCollectInfo().getCompletionData().getUser().getSurname());
            publicOrderDataDTO.setItemDTOList(orderDTO);
        }
        return publicOrderDataDTO;
    }

    @PostMapping("/updateCart")
    public void updateCart(@RequestBody String orderJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer[] order = objectMapper.readValue(orderJson, Integer[].class);
        int amount;
        for (int i = 0; i < 8; i++) {
            dataStore.getOrder()[i].setQuantity(order[i]);
            amount = dataStore.getOrder()[i].getUnitPrice() * order[i];
            dataStore.getOrder()[i].setAmount(amount);
            dataStore.getOrder()[i]
                    .setVatAmount(amount * dataStore.getOrder()[i].getVatPercent() / 10000);
        }
    }

    @PostMapping("/connection_request")
    public LaunchInfo setConnection() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.
                currentRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();
        AuthInfo authInfo = bankIdService.connectionRequest(ip);
        LaunchInfo launchInfo = new LaunchInfo();
        if (authInfo != null) {
            launchInfo.setAutoStartToken(authInfo.getAutoStartToken());
            launchInfo.setOrderRef(authInfo.getOrderRef());
        }
        return launchInfo;
    }

    @GetMapping(path = "/QRImage", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generateQRCodeData(@RequestParam long t) throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        BufferedImage bImage = dynamicQR.generateQRImage();
        if (bImage != null) {
            ImageIO.write(bImage, "png", bao);
        } else return null;
        return bao.toByteArray();
    }

    @PostMapping("/collection_request")
    public PendingInfo getConnectionInfo(@RequestBody ObjectNode objectNode) {
        boolean usedDesktopApp = objectNode.get("usedDesktopApp").asBoolean();
        boolean launchSuccess = objectNode.get("launchSuccess").asBoolean();
        if (usedDesktopApp) {
            dataStore.setUsedDesktopApp(true);
            dataStore.setLaunchSuccess(launchSuccess);
        }
        String message = "", status, hintCode;
        PendingInfo pendingInfo = new PendingInfo();

//        Launch BankID app result management:
        if (usedDesktopApp && !launchSuccess) {
            pendingInfo.setStatus("failed");
            pendingInfo.setMessage("Failed to start the BankID security application. If the BankID application is not installed, contact your Internet bank.");
            return pendingInfo;
        }

        CollectInfo collectInfo = bankIdService.collectRequest();
        if (collectInfo != null) {
            status = collectInfo.getStatus();
            hintCode = collectInfo.getHintCode();
            if (status.equals("pending") && dataStore.isUsedDesktopApp())
                message = "Identification or signing in progress.";
            if (status.equals("pending") &&
                    (hintCode.equals("outstandingTransaction") || hintCode.equals("noClient")) &&
                    dataStore.isUsedDesktopApp()) message = "Start your BankID app.";
            if (status.equals("pending") && hintCode.equals("userSign"))
                message = "Enter your security code in the BankID app and select Identify or Sign.";
            if (status.equals("pending") && hintCode.equals("started") && dataStore.isUsedDesktopApp())
                message = "Searching for BankID:s, it may take a little while... If a few seconds have passed and still no BankID has been found, you probably don’t have a BankID which can be used for this identification/signing on this computer. If you have a BankID card, please insert it into your card reader. If you don’t have a BankID you can order one from your internet bank.";
            if (status.equals("pending") && hintCode.equals("started") && !dataStore.isUsedDesktopApp())
                message = "Searching for BankID:s, it may take a little while... If a few seconds have passed and still no BankID has been found, you probably don’t have a BankID which can be used for this identification/signing on this device. If you don’t have a BankID you can order one from your internet bank";
            if (status.equals("failed"))
                message = "Unknown error. Please try again.";
            if (status.equals("failed") && hintCode.equals("userCancel"))
                message = "Action cancelled.";
            if (status.equals("failed") && hintCode.equals("expiredTransaction"))
                message = "The BankID app is not responding. Please check that the program is started and that you have internet access. If you don’t have a valid BankID you can get one from your bank. Try again.";
            if (status.equals("failed") && hintCode.equals("certificateErr"))
                message = "The BankID you are trying to use is revoked or too old. Please use another BankID or order a new one from your internet bank.";
            if (status.equals("failed") && hintCode.equals("startFailed") && dataStore.isUsedDesktopApp())
                message = "The BankID app couldn’t be found on your computer or mobile device. Please install it and order a BankID from your internet bank. Install the app from your app store or https://install.bankid.com.";
            if (status.equals("failed") && hintCode.equals("startFailed") && !dataStore.isUsedDesktopApp())
                message = "Failed to scan the QR code. Start the BankID app and scan the QR code. Check that the BankID app is up to date. If you don't have the BankID app, you need to install it and order a BankID from your internet bank. Install the app from your app store or https://install.bankid.com.";
//        BankIdErrorHandler status management:
            if (status.equals("failed") && hintCode.equals("cancelled"))
                message = "Action cancelled. Please try again.";
            if (status.equals("failed") && hintCode.equals("alreadyInProgress"))
                message = "An identification or signing for this personal number is already started. Please try again.";
            if (status.equals("failed") && (hintCode.equals("requestTimeout") || hintCode.equals("internalError")))
                message = "Internal error. Please try again.";
            pendingInfo.setStatus(status);
            pendingInfo.setMessage(message);
        }
        return pendingInfo;
    }

    @PostMapping("/authdata_request")
    public CompletionData.User getUserInfo() {
        return dataStore.getCollectInfo().getCompletionData().getUser();
    }

    @PostMapping("/testUserData")
    public String setTestUserData(@RequestBody String testUserDataJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CompletionData.User testUserData = objectMapper.readValue(testUserDataJson, CompletionData.User.class);
        if (testUserData != null && testUserData.getGivenName() != null &&
                testUserData.getSurname() != null && testUserData.getPersonalNumber() != null) {
            dataStore.getCollectInfo().getCompletionData().getUser()
                    .setGivenName(testUserData.getGivenName());
            dataStore.getCollectInfo().getCompletionData().getUser()
                    .setSurname(testUserData.getSurname());
            dataStore.getCollectInfo().getCompletionData().getUser()
                    .setPersonalNumber(testUserData.getPersonalNumber());
            return "auth_OK";
        }
        return "auth_error";
    }

    @PostMapping("/exit")
    public String logOut() {
        dataStore.setAuthInfo(new AuthInfo());
        dataStore.setCollectInfo(new CollectInfo());
        dataStore.setUsedDesktopApp(false);
        dataStore.setLaunchSuccess(false);
        return "exit";
    }
}
