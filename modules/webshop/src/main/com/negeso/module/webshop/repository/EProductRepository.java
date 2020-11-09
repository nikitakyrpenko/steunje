package com.negeso.module.webshop.repository;

import com.negeso.module.webshop.entity.EProduct;

import java.util.List;
import java.util.Optional;

public interface EProductRepository {

    Optional<EProduct> findByProductNumberIfExists(String productCode);

    List<EProduct> findAll();

}
