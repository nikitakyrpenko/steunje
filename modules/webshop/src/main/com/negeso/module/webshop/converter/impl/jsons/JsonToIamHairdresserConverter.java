package com.negeso.module.webshop.converter.impl.jsons;

import com.negeso.module.webshop.converter.JsonConverter;
import com.negeso.module.webshop.dto.IamHairdresserDto;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonToIamHairdresserConverter extends JsonConverter<IamHairdresserDto> {

    @Autowired
    public JsonToIamHairdresserConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }
}
