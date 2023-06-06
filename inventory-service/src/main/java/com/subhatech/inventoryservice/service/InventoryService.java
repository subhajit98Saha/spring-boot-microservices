package com.subhatech.inventoryservice.service;

import com.subhatech.inventoryservice.dto.InventoryResponse;
import com.subhatech.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(e-> InventoryResponse.builder().skuCode(e.getSkuCode())
                            .isInStock(e.getQuantity() > 0).build()
                    ).toList();
    }
}
