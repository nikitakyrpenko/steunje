package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.EntityObjectFactory;
import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.CustomerDetails;
import com.negeso.module.webshop.entity.IamContact;
import com.negeso.module.webshop.entity.IamCustomer;
import com.negeso.module.webshop.entity.IamUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

public class IamCustomerRepositoryImplTest {

    private static IamCustomerRepository customerRepository;
    private static IamUserRepository userRepository;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                IamUser.class,
                IamCustomer.class,
                IamContact.class,
                CustomerDetails.class,
                IamCustomerRepositoryImpl.class,
                IamUserRepositoryImpl.class

        );
        customerRepository = applicationContext.getBean(IamCustomerRepository.class);
        userRepository = applicationContext.getBean(IamUserRepository.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);
    }

    @Test
    public void whenIamCustomerRepositoryFindByLogin_thenReturnIamCustomer(){
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        transaction.begin();

        Optional<IamCustomer> customer = customerRepository.findByLogin("kate");

        Assert.assertTrue(customer.isPresent());

    }

    @Test
    public void whenIamCustomerRepositorySave_ThenOk(){
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        transaction.begin();

        IamUser iamUser = EntityObjectFactory.getIamUser();
        iamUser.setId(null);
        iamUser.setLogin("test13@13login");
        userRepository.save(iamUser);

        IamCustomer customer = EntityObjectFactory.getIamCustomer();
        IamContact contact = EntityObjectFactory.getIamContact();

        customer.setId(null);
        customer.setLogin("test13@13login");
        contact.setId(null);

        customer.setBillingContact(contact);

        customerRepository.save(customer);

        Assert.assertNotNull(customer.getId());

        rollback(transaction);
    }

    private void rollback(Transaction transaction){
        transaction.rollback();
    }

}