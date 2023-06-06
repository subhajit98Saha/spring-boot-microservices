package com.subhatech.orderservice.service;


import com.subhatech.orderservice.dto.InventoryResponse;
import com.subhatech.orderservice.dto.OrderLineItemsDto;
import com.subhatech.orderservice.dto.OrderRequest;
import com.subhatech.orderservice.model.Order;
import com.subhatech.orderservice.model.OrderLineItems;
import com.subhatech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapDtoToModel)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
        //Call Inventory Service, and place order if product is in stock
        InventoryResponse[] result = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                                .bodyToMono(InventoryResponse[].class).block();
        boolean allProductsInStock = Arrays.stream(result).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapDtoToModel(OrderLineItemsDto e) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(e.getPrice());
        orderLineItems.setQuantity(e.getQuantity());
        orderLineItems.setSkuCode(e.getSkuCode());
        return orderLineItems;
    }
}
