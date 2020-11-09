package com.negeso.module.webshop.transformer.impl;

import com.negeso.module.webshop.dto.CartItemDto;
import com.negeso.module.webshop.dto.OrderItemDto;
import com.negeso.module.webshop.transformer.CartItemToOrderItemTransformer;
import org.springframework.stereotype.Service;

@Service
public class CartItemToOrderItemTransformerImpl implements CartItemToOrderItemTransformer {

    @Override
    public OrderItemDto transformCartItemDtoToOrderItemDto(CartItemDto cartItemDto, Integer orderId) {
        OrderItemDto orderItem = new OrderItemDto();

        orderItem.setProduct(cartItemDto.getProduct());
        orderItem.setPrice(cartItemDto.getProduct().getPriceInc());
        orderItem.setDiscount(0.0);
        orderItem.setQuantity(cartItemDto.getQuantity());
        orderItem.setOrder(orderId);

        return orderItem;
    }
}
