package com.negeso.module.webshop.converter.impl.internal;

import com.negeso.module.webshop.EntityObjectFactory;
import com.negeso.module.webshop.dto.IamCustomerDto;
import com.negeso.module.webshop.entity.IamContact;
import com.negeso.module.webshop.entity.IamCustomer;
import org.junit.Assert;
import org.junit.Test;

import org.modelmapper.ModelMapper;

public class GenericConverterCustomerTest {

    private final GenericConverter<IamCustomer, IamCustomerDto> genericConverter =
            new GenericConverter<>(new ModelMapper());


    @Test
    public void test(){
        IamCustomer customer = EntityObjectFactory.getIamCustomer();
        IamContact shippingContact = EntityObjectFactory.getIamContact();
        IamContact billingContact = EntityObjectFactory.getIamContact();

        customer.setBillingContact(billingContact);
        customer.setShippingContact(shippingContact);

        IamCustomerDto iamCustomerDto = genericConverter.convertEntityToDto(customer, IamCustomerDto.class);

        Assert.assertNotNull(iamCustomerDto);
    }

}
