package com.negeso.module.webshop.service;

import com.negeso.module.core.service.ParameterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class);

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private CustomerService customerService;

}
