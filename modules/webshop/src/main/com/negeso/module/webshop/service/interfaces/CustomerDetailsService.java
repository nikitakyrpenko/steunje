package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.CustomerDetailsDto;

import java.util.List;

public interface CustomerDetailsService {

    CustomerDetailsDto findById(Integer id);

    List<CustomerDetailsDto> findCustomerDetailsByCustomerLogin (String login);

    List<CustomerDetailsDto> findAllByCustomerId(Integer customerId);
}
