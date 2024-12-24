package org.example.commande.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.example.commande.client.ProductGraphQLClient;
import org.example.commande.controller.OrderNotFoundException;
import org.example.commande.event.OrderEvent;
import org.example.commande.model.dto.OrderRequest;
import org.example.commande.model.dto.OrderResponse;
import org.example.commande.model.entity.Order;
import org.example.commande.model.enumeration.OrderStatus;
import org.example.commande.repository.OrderRepository;
import org.example.produit.model.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.InsufficientStockException;

import java.time.LocalDateTime;


@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductGraphQLClient productClient;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponse createOrder(OrderRequest request) {
        // Vérifier la disponibilité du produit
        ProductDTO product = productClient.getProduct(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new IllegalStateException("Stock insuffisant");
        }

        // Créer la commande
        Order order = Order.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        order = orderRepository.save(order);

        // Mettre à jour le stock
        productClient.updateStock(request.getProductId(), -request.getQuantity());

        // Envoyer l'événement
        OrderEvent event = new OrderEvent(order.getId(), order.getUserId(), OrderStatus.CREATED);
        kafkaTemplate.send("order-events", event);

        return mapToResponse(order);
    }

    public OrderResponse fallbackCreateOrder(OrderRequest request, Exception e) {
        log.error("Circuit breaker activé pour la création de commande", e);
        Order order = Order.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status(OrderStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(order);
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée: " + id));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}