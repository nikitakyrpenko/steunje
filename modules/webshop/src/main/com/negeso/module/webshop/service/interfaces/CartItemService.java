package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.CartItemDto;

import java.util.List;

public interface CartItemService {

    void createAndSaveCartItemByProductNumberAndQuantityAndCartOwnerId(String productCode, Integer quantity, String cartOwnerId);

    List<CartItemDto> findAllByCartOwnerId(String cartOwnerId);

}
