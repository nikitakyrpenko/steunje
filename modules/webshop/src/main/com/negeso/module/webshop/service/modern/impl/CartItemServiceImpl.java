package com.negeso.module.webshop.service.modern.impl;

import com.negeso.module.webshop.dto.CartItemDto;
import com.negeso.module.webshop.dto.ProductDto;
import com.negeso.module.webshop.entity.modern.ECartItem;
import com.negeso.module.webshop.repository.ECartItemRepository;
import com.negeso.module.webshop.service.modern.CartItemService;
import com.negeso.module.webshop.service.modern.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired private ProductService productService;

    @Autowired private ECartItemRepository cartItemRepository;

    @Autowired private ModelMapper modelMapper;

    @Override
    public void createAndSaveCartItemByProductNumberAndQuantityAndCartOwnerId(String productNumber, Integer quantity, String cartOwnerId) {
        ProductDto product = productService.findByProductNumber(productNumber);

        CartItemDto cartItem = new CartItemDto(product, quantity, cartOwnerId);

        ECartItem entity = modelMapper.map(cartItem, ECartItem.class);

        cartItemRepository.save(entity);
    }

    @Override
    public List<CartItemDto> findAllByCartOwnerId(String cartOwnerId) {
        return cartItemRepository.findAllByCartOwnerId(cartOwnerId)
                .stream()
                .map(entity -> modelMapper.map(entity, CartItemDto.class))
                .collect(Collectors.toList());
    }
}
