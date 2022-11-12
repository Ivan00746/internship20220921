package org.example.entity.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
    private int id;
    private String imageUrl;
    private String name;
    private int quantity;
    private String quantityUnit;
    private int unitPrice;
    private int amount;
}
