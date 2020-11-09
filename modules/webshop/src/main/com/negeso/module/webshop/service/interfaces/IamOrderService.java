package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.IamOrderDto;

public interface IamOrderService {

    IamOrderDto createOrderByCustomerId(Integer customerId, Integer customerDetailsId);

    Integer insertOrderItemInOrder (String orderId, String quantity, String productId);

    IamOrderDto findById(Integer id);

    IamOrderDto commitOrderByOrderId(Integer orderId, String paymentMethod);

    void deleteOrderItem (String orderId, String productId);
}
