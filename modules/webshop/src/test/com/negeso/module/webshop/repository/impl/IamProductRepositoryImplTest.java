package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.IamProduct;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

import static org.junit.Assert.*;

public class IamProductRepositoryImplTest {

    private static IamProductRepositoryImpl productRepository;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                IamProduct.class,
                IamProductRepositoryImpl.class
        );
        productRepository = applicationContext.getBean(IamProductRepositoryImpl.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);
    }

    @Test
    public void whenIamProductRepositoryFindByProductCodeNotExists_ThenReturnProduct(){
        Optional<IamProduct> product = productRepository.findByProductCode("96945010");

        assertTrue(product.isPresent());
    }

    @Test
    public void whenIamProductRepositoryFindByProductCodeNotExists_ThenReturnEmptyOptional(){
        Optional<IamProduct> product = productRepository.findByProductCode("12345");

        assertFalse(product.isPresent());
    }
}