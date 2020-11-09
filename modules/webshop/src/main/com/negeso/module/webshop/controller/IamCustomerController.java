package com.negeso.module.webshop.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.spring.interceptor.Unsecured;
import com.negeso.module.webshop.dto.CustomerDetailsDto;
import com.negeso.module.webshop.service.interfaces.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class IamCustomerController {

    private final CustomerDetailsService customerDetailsService;

    @Autowired
    public IamCustomerController(CustomerDetailsService customerDetailsService) {
        this.customerDetailsService = customerDetailsService;
    }

    @RequestMapping(value = "/webshop/api/customerdetails", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    @Unsecured
    public ResponseEntity<List<CustomerDetailsDto>> getCustomerDetails (HttpServletRequest request) throws IOException {
    
        User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);

        List<CustomerDetailsDto> customerDetailsDtoList = customerDetailsService.findCustomerDetailsByCustomerLogin(user.getLogin());

        return new ResponseEntity<>(customerDetailsDtoList, HttpStatus.OK);
    }
}
