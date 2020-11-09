package com.negeso.module.webshop.service;

import com.negeso.module.webshop.converter.impl.internal.GenericConverter;
import com.negeso.module.webshop.dto.ProductDto;
import com.negeso.module.webshop.entity.EProduct;
import com.negeso.module.webshop.exception.service.ProductRetrievingException;
import com.negeso.module.webshop.repository.EProductRepository;
import com.negeso.module.webshop.service.interfaces.IamProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IamProductServiceImpl implements IamProductService {

    @Autowired
    private EProductRepository productRepository;

    @Autowired
    private GenericConverter<EProduct, ProductDto> converter;

    @Override
    public List<ProductDto> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(entity -> converter.convertEntityToDto(entity, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto findByProductNumber(String productNumber) {
        return productRepository
                .findByProductNumberIfExists(productNumber)
                .map(iamProduct -> converter.convertEntityToDto(iamProduct, ProductDto.class))
                .orElseThrow(() -> new ProductRetrievingException("Product with such [id] does not exist"));
    }
}
