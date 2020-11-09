package com.negeso.module.webshop.service;


import com.negeso.module.webshop.service.interfaces.IamOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IamOrderItemServiceImpl implements IamOrderItemService {

    @Autowired
    private IamOrderItemRepository orderItemRepository;

    @Override
    public void deleteOrderItem(Integer id) {
        orderItemRepository.delete(id);
    }
}
