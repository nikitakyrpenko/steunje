package com.negeso.module.webshop.service.interfaces;

import com.negeso.module.webshop.dto.UserDto;
import com.negeso.module.webshop.entity.User;

import java.util.Optional;

public interface IamUserService {

    UserDto findById(Integer id);

    void save(UserDto user);

    Optional<UserDto> findByLogin(String login);

    void update(UserDto userDto);

}
