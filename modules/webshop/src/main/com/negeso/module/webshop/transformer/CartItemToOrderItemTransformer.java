package com.negeso.module.webshop.transformer;

import com.negeso.module.webshop.dto.CartItemDto;
import com.negeso.module.webshop.dto.OrderItemDto;

public interface CartItemToOrderItemTransformer {

    OrderItemDto transformCartItemDtoToOrderItemDto(CartItemDto cartItemDto, Integer orderId);

}
