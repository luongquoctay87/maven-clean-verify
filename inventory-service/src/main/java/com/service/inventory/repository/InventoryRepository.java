package com.service.inventory.repository;

import com.service.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}
