package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.UserDto;

public interface UserService {

     UserDto findById(Long id);

     UserDto save(UserDto userDto);

     UserDto findByLogin(String login);

     void update(UserDto userDto);
}
