package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.ProductDto;

import java.util.List;

public interface IamProductService {

    List<ProductDto> findAll();

    ProductDto findByProductNumber (String productNumber);
}
