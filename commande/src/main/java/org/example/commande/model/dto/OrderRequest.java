package org.example.commande.model.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class OrderRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long productId;
    
    @NotNull
    @Min(1)
    private Integer quantity;
}