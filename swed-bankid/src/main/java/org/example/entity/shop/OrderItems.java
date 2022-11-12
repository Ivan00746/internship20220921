package org.example.entity.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class OrderItems {
    private String reference;
    private String name;
    private String type;
    private String clazz;
    private String itemUrl;
    private String imageUrl;
    private String description;
    private String discountDescription;
    private int quantity;
    private String quantityUnit;
    private int unitPrice;
    private int discountPrice;
    private int vatPercent;
    private int amount;
    private int vatAmount;
    private List<String> restrictedToInstruments;

    public OrderItems(String reference, String name, String type, String clazz, String itemUrl,
                      String imageUrl, String description, String discountDescription,
                      String quantityUnit, int unitPrice, int vatPercent) {
        this.reference = reference;
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.itemUrl = itemUrl;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discountDescription = discountDescription;
        this.quantityUnit = quantityUnit;
        this.unitPrice = unitPrice;
        this.vatPercent = vatPercent;
    }
    @JsonProperty("class")
    public String getClazz() {
        return clazz;
    }
    @JsonProperty("class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
