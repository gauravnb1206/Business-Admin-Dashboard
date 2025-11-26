package com.example.businessadmin.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;
    private Double price;

}
