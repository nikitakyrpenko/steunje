package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.EntityObjectFactory;
import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.*;
import junit.framework.Assert;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Optional;

public class IamOrderRepositoryImplTest {

    private static IamOrderRepositoryImpl orderRepository;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                IamOrder.class,
                IamOrderItem.class,
                IamProduct.class,
                IamOrderRepositoryImpl.class


        );
        orderRepository = applicationContext.getBean(IamOrderRepositoryImpl.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);
    }

    @Test
    public void whenOrderRepositoryImplFindById_thenShouldPresent(){
        Optional<IamOrder> byId = orderRepository.findById(10);

        Assert.assertTrue(byId.isPresent());
    }

    @Test
    public void whenOrderRepositoryImplFindByIdNotExists_thenShouldReturnEmptyOptional(){
        List<IamOrder> byHairdresserId = orderRepository.findByHairdresserId(12);

        Assert.assertFalse(byHairdresserId.isEmpty());
    }

    @Test
    public void whenOrderRepositoryImplSave_thenShouldSaveToDB(){

        IamOrder order = EntityObjectFactory.getIamOrder();
        IamCustomer iamCustomer = EntityObjectFactory.getIamCustomer();

        order.setUserName(iamCustomer);

        Integer id = orderRepository.save(order);

        Assert.assertNotNull(id);
    }

    @Test
    public void whenOrderRepositoryImplUpdate_thenShouldSaveToDB(){
        IamOrder iamOrder = orderRepository.findById(2072).get();

        IamOrderItem orderItem = EntityObjectFactory.getIamOrderItem();
        IamProduct product = EntityObjectFactory.getIamProduct();

        orderItem.setProduct(product);
        iamOrder.addOrderItem(orderItem);

        Integer id = orderRepository.update(iamOrder);

        Assert.assertNotNull(id);
    }
}