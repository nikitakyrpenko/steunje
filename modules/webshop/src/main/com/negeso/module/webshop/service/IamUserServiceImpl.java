package com.negeso.module.webshop.service;

import com.negeso.module.webshop.converter.impl.internal.GenericConverter;
import com.negeso.module.webshop.dto.UserDto;
import com.negeso.module.webshop.entity.IamUser;
import com.negeso.module.webshop.exception.service.UserRetrievingException;
import com.negeso.module.webshop.service.interfaces.IamUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IamUserServiceImpl implements IamUserService {

    @Autowired
    private IamUserRepository userRepository;

    @Autowired
    private GenericConverter<IamUser, UserDto> converter;

    @Override
    public UserDto findById(Integer id) {
        return userRepository
                .findById(id)
                .map(iamUser -> converter.convertEntityToDto(iamUser, UserDto.class))
                .orElseThrow(() -> new UserRetrievingException("User with such [id] does not exist"));
    }

    @Override
    public void save(UserDto user) {
        userRepository.save(
                converter.convertDtoToEntity(user, IamUser.class)
        );
    }

    @Override
    public Optional<UserDto> findByLogin(String login) {
        return userRepository
                .findByLogin(login)
                .map(iamUser -> converter.convertEntityToDto(iamUser, UserDto.class));
    }

    @Override
    public void update(UserDto userDto) {
        userRepository.update(
                converter.convertDtoToEntity(userDto, IamUser.class)
        );
    }
}
