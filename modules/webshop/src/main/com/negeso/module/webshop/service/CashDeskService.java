package com.negeso.module.webshop.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.module.webshop.bo.CashDeskOrder;
import com.negeso.module.webshop.bo.CashDeskTransaction;
import com.negeso.module.webshop.util.CashDeskHttpClient;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@Service
public class CashDeskService {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_COMPLETE = "completed";


    public CashDeskOrder createOrder(String transactionId, BigDecimal amount, String issuerId) throws IOException {
        return this.createOrder(transactionId, amount, "EUR", "Order #" + transactionId, CashDeskHttpClient.RETURN_URL, issuerId);
    }

    public CashDeskOrder createPayPalOrder(String transactionId, BigDecimal amount, String paymentMethod) throws IOException {
        CashDeskOrder requestOrder = new CashDeskOrder();
        CashDeskTransaction transaction = new CashDeskTransaction(paymentMethod);

        requestOrder.setMerchantOrderId(transactionId);
        Long coinsAmount = amount.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        requestOrder.setAmount(coinsAmount);
        requestOrder.setCurrency("EUR");
        requestOrder.setDescription("Order #" + transactionId);
        requestOrder.setReturnUrl(CashDeskHttpClient.RETURN_URL);
        requestOrder.addTransaction(transaction);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();

        StringEntity postingString = new StringEntity(gson.toJson(requestOrder));
        CashDeskOrder responseOrder = CashDeskHttpClient.createAndExecute("/v1/orders/", "POST", CashDeskOrder.class, postingString);

        return responseOrder;
    }

    public CashDeskOrder createOrder(String transactionId, BigDecimal amount, String currency, String description, String returnUrl, String issuerId) throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();

        CashDeskOrder requestOrder = this.prepareRequestOrder(transactionId, amount, currency, description, returnUrl, issuerId);
        StringEntity postingString = new StringEntity(gson.toJson(requestOrder));

        CashDeskOrder responseOrder = CashDeskHttpClient.createAndExecute("/v1/orders/", "POST", CashDeskOrder.class, postingString);
        return responseOrder;
    }

    private CashDeskOrder prepareRequestOrder(String transactionId, BigDecimal amount, String currency, String description, String returnUrl, String issuerId) {
        CashDeskOrder requestOrder = new CashDeskOrder();
        CashDeskTransaction transaction = this.createTransaction(issuerId);

        requestOrder.setMerchantOrderId(transactionId);
        Long coinsAmount = amount.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        requestOrder.setAmount(coinsAmount);
        requestOrder.setCurrency(currency);
        requestOrder.setDescription(description);
        requestOrder.setReturnUrl(returnUrl);
        requestOrder.addTransaction(transaction);

        return requestOrder;
    }

    public CashDeskOrder checkOrderStatus(String transactionId){
        return CashDeskHttpClient.createAndExecute("/v1/orders/" + transactionId, "GET", CashDeskOrder.class);
    }

    private CashDeskTransaction createTransaction(String issuerId) {
        return issuerId == null ? new CashDeskTransaction("card") : new CashDeskTransaction("ideal", issuerId);
    }
}
