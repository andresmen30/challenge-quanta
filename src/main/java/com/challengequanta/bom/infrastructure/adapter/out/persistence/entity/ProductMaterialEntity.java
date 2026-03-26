package com.challengequanta.bom.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaterialEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("product_id")
    private Long productId;

    @Column("material")
    private String material;

    @Column("quantity")
    private Integer quantity;
}
