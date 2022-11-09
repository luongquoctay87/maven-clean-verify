package com.service.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_inventory")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "quantity")
    private Integer quantity;
}
