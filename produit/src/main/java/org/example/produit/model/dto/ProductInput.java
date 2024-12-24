package org.example.produit.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductInput {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}