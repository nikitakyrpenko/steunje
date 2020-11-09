package com.negeso.module.webshop.repository.impl;

public class ECustomerDetailsImplTest {
/*
    private static SessionFactory sessionFactory;
    private static CustomerDetailsRepository customerDetailsRepository;

    @BeforeClass
    public static void init(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                TestConfigProperties.class,
                ECustomerDetails.class,
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
        ECustomerDetails ECustomerDetails = EntityObjectFactory.getCustomerDetails();

        ECustomerDetails.setId(null);

        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();


        Optional<ECustomerDetails> act = customerDetailsRepository.findById(customerDetailsRepository.save(ECustomerDetails));

        Assert.assertThat(ECustomerDetails, new ReflectionEquals(act.get()));

    }

    @Test
    public void whenCustomerDetailsRepositoryFindByIdExist_ThenOk(){

        Optional<ECustomerDetails> res = customerDetailsRepository.findById(20);

        Assert.assertTrue(res.isPresent());

    }

    @Test
    public void whenCustomerDetailsRepositoryFindByIdNotExist_ThenEmptyOptional(){

        Optional<ECustomerDetails> res = customerDetailsRepository.findById(2000000);

        Assert.assertFalse(res.isPresent());
    }*/
}
