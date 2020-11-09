package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.CustomerDetailsDto;
import com.negeso.module.webshop.dto.IamHairdresserDto;

public interface HairdresserService {

    CustomerDetailsDto saveCustomerDetails(CustomerDetailsDto customerDetailsDto, Integer creatorId);

    IamHairdresserDto save(IamHairdresserDto hairdresserDto);

    IamHairdresserDto findByUserId(Integer userId);
}
