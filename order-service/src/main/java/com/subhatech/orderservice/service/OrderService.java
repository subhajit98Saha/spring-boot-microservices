package com.subhatech.orderservice.service;


import com.subhatech.orderservice.dto.OrderLineItemsDto;
import com.subhatech.orderservice.dto.OrderRequest;
import com.subhatech.orderservice.model.Order;
import com.subhatech.orderservice.model.OrderLineItems;
import com.subhatech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapDtoToModel)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        orderRepository.save(order);

    }

    private OrderLineItems mapDtoToModel(OrderLineItemsDto e) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(e.getPrice());
        orderLineItems.setQuantity(e.getQuantity());
        orderLineItems.setSkuCode(e.getSkuCode());
        return orderLineItems;
    }
}
