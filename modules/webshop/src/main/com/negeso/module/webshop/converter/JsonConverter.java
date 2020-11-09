package com.negeso.module.webshop.converter;

import com.negeso.module.webshop.exception.JsonToDtoParsingException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class JsonConverter<DTO> {
    private final Logger logger = Logger.getLogger(JsonConverter.class);
    private final ObjectMapper objectMapper;

    protected Class<DTO> clazz;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DTO convertStringJsonToDTO(String json, Class<DTO> clazz) {
        DTO result;
        try {
            result = objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new JsonToDtoParsingException(e.getMessage(), e.getCause());
        }
        return result;
    }

    public String convertDtoToJsonString(DTO dto){
        String json = null;

        try {
            json = objectMapper.writeValueAsString(dto);
        } catch (IOException e) {
            logger.error(e.getMessage(), e.getCause());
            throw new JsonToDtoParsingException(e.getMessage(), e.getCause());
        }

        return json;
    }
}
