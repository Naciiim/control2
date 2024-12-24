package org.example.produit.controller;

import org.example.produit.model.entity.Product;
import org.example.produit.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @SchemaMapping(typeName = "Query", value = "product")
    public Product getProduct(@Argument Long id) {
        return productService.getProduct(id);
    }

    @SchemaMapping(typeName = "Query", value = "products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @SchemaMapping(typeName = "Mutation", value = "updateStock")
    public Product updateStock(@Argument Long id, @Argument Integer quantity) {
        return productService.updateStock(id, quantity);
    }
}