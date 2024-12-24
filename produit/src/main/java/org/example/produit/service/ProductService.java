package org.example.produit.service;

import lombok.extern.slf4j.Slf4j;
import org.example.produit.model.dto.ProductInput;
import org.example.produit.model.entity.Product;
import org.example.produit.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.InsufficientStockException;
import util.ProductNotFoundException;

import java.util.List;
@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.info("Récupération de tous les produits");
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        log.info("Récupération du produit avec l'ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec l'ID: " + id));
    }

    @Transactional
    public Product createProduct(ProductInput input) {
        log.info("Création d'un nouveau produit");
        Product product = new Product();
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setStock(input.getStock());

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductInput input) {
        log.info("Mise à jour du produit avec l'ID: {}", id);
        Product product = getProduct(id);

        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setStock(input.getStock());

        return productRepository.save(product);
    }

    @Transactional
    public Product updateStock(Long id, Integer quantityChange) {
        log.info("Mise à jour du stock pour le produit {} de {}", id, quantityChange);
        Product product = getProduct(id);

        int newStock = product.getStock() + quantityChange;
        if (newStock < 0) {
            throw new InsufficientStockException(
                    String.format("Stock insuffisant. Stock actuel: %d, Changement demandé: %d",
                            product.getStock(), quantityChange)
            );
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Suppression du produit avec l'ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
        productRepository.deleteById(id);
    }

    public boolean checkStock(Long id, Integer quantity) {
        log.info("Vérification du stock pour le produit {} - quantité demandée: {}", id, quantity);
        Product product = getProduct(id);
        return product.getStock() >= quantity;
    }
}
