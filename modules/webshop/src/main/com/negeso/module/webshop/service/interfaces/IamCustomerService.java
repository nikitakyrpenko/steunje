package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.IamCustomerDto;

import java.util.List;
import java.util.Optional;

public interface IamCustomerService {

    IamCustomerDto findById(Integer id);

    IamCustomerDto findByLogin(String login);

    void save(IamCustomerDto customer);

    void update(IamCustomerDto customer);

    Optional<IamCustomerDto> findByIdOptional(Integer id);

    List<IamCustomerDto> findAllByUserId(Integer hairdresserId);
}
