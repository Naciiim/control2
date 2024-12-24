package org.example.commande.client;

import com.example.order.dto.ProductDTO;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductGraphQLClient {
    private final GraphQlClient graphQlClient;

    public ProductGraphQLClient(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    public ProductDTO getProduct(Long id) {
        String query = """
            query getProduct($id: ID!) {
              product(id: $id) {
                id
                name
                price
                stock
              }
            }
        """;

        return graphQlClient.document(query)
            .variable("id", id)
            .retrieve("product")
            .toEntity(ProductDTO.class)
            .block();
    }

    public ProductDTO updateStock(Long id, Integer quantity) {
        String mutation = """
            mutation updateStock($id: ID!, $quantity: Int!) {
              updateStock(id: $id, quantity: $quantity) {
                id
                stock
              }
            }
        """;

        return graphQlClient.document(mutation)
            .variable("id", id)
            .variable("quantity", quantity)
            .retrieve("updateStock")
            .toEntity(ProductDTO.class)
            .block();
    }
}