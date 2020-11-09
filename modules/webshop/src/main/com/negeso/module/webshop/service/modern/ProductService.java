package com.negeso.module.webshop.service.modern;

import com.negeso.module.webshop.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();

    ProductDto findByProductNumber (String productNumber);
}
