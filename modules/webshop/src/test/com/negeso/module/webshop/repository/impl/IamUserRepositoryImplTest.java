package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.EntityObjectFactory;

import com.negeso.module.webshop.TestConfigProperties;
import com.negeso.module.webshop.entity.IamUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

public class IamUserRepositoryImplTest {

    private static IamUserRepository userRepository;
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                IamUser.class,
                IamUserRepositoryImpl.class
        );
        userRepository = applicationContext.getBean(IamUserRepository.class);
        sessionFactory = applicationContext.getBean("apiSessionFactory", SessionFactory.class);

    }

    @Test
    public void whenIamUserRepositorySave_ThenOk(){
        IamUser iamUser = EntityObjectFactory.getIamUser();

        iamUser.setId(null);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        transaction.begin();

        Optional<IamUser> actual = userRepository.findById(userRepository.save(iamUser));

        Assert.assertThat(iamUser, new ReflectionEquals(actual.get()));

        rollback(transaction);
    }


    @Test
    public void whenIamUserRepositoryFindByIdExist_ThenOk(){
        Optional<IamUser> result = userRepository.findById(12801);

        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void whenIamUserRepositoryFindByIdNotExists_ThenEmptyOptional(){
        Optional<IamUser> expected = userRepository.findById(1488);

        Assert.assertFalse(expected.isPresent());
    }

    private void rollback(Transaction transaction){
        transaction.rollback();
    }

}