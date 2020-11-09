package com.negeso.module.webshop.converter.impl.internal;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Class for converting DTO classes to Entity classes and vice-versa
 */

@Component
public class GenericConverter<Entity, DTO> {

    private final ModelMapper modelMapper;

    @Autowired
    public GenericConverter(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }



    public Entity convertDtoToEntity(DTO dto, Class<Entity> clazz) {
        return modelMapper.map(dto, clazz);
    }


    public DTO convertEntityToDto(Entity entity, Class<DTO> clazz) {
        return modelMapper.map(entity, clazz);
    }
}
