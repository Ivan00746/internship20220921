package org.example.entity.shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicOrderDataDTO {
    private String payeeReference;
    private String givenName;
    private String surname;
    private List<OrderItemDTO> itemDTOList;
}
