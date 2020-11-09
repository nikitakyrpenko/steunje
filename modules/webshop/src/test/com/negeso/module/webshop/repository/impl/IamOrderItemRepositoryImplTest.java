package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.*;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.List;

import static org.junit.Assert.*;

public class IamOrderItemRepositoryImplTest {

    private static IamOrderItemRepository orderItemRepository;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                IamOrderItem.class,
                IamProduct.class,
                IamOrderItemRepositoryImpl.class

        );
        orderItemRepository = applicationContext.getBean(IamOrderItemRepository.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);
    }

    @Test
    public void whenOrderItemRepositoryFindByOrderIdExists_thenReturnList(){
        List<IamOrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(6);

        assertTrue(orderItems.size() > 0);
    }

    @Test
    public void whenOrderItemRepositoryFindByOrderIdNotExists_thenReturnEmptyList(){
        List<IamOrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(133);

        assertFalse(orderItems.size() > 0);
    }

}