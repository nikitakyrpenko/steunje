package com.negeso.module.webshop.converter.impl.jsons;

import com.negeso.module.webshop.converter.JsonConverter;
import com.negeso.module.webshop.dto.CustomerDetailsDto;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonToCustomerDetailsConverter extends JsonConverter<CustomerDetailsDto> {

    @Autowired
    public JsonToCustomerDetailsConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

}
