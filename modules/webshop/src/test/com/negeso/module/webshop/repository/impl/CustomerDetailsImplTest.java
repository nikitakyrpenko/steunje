package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.EntityObjectFactory;
import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.CustomerDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

public class CustomerDetailsImplTest {

    private static SessionFactory sessionFactory;
    private static CustomerDetailsRepository customerDetailsRepository;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                CustomerDetails.class,
                CustomerDetailsRepositoryImpl.class
        );
        customerDetailsRepository = applicationContext.getBean(CustomerDetailsRepository.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);
    }

    private void rollback(Transaction transaction){
        transaction.rollback();
    }

    @Test
    public void whenCustomerDetailsRepositorySave_ThenOk (){
        CustomerDetails customerDetails = EntityObjectFactory.getCustomerDetails();

        customerDetails.setId(null);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();


        Optional<CustomerDetails> act = customerDetailsRepository.findById(customerDetailsRepository.save(customerDetails));

        Assert.assertThat(customerDetails, new ReflectionEquals(act.get()));

    }

    @Test
    public void whenCustomerDetailsRepositoryFindByIdExist_ThenOk(){

        Optional<CustomerDetails> res = customerDetailsRepository.findById(20);

        Assert.assertTrue(res.isPresent());

    }

    @Test
    public void whenCustomerDetailsRepositoryFindByIdNotExist_ThenEmptyOptional(){

        Optional<CustomerDetails> res = customerDetailsRepository.findById(2000000);

        Assert.assertFalse(res.isPresent());
    }
}
