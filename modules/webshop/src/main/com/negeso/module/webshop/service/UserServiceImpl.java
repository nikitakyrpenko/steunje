package com.negeso.module.webshop.service;

import com.negeso.framework.util.MD5Encryption;
import com.negeso.module.webshop.dto.UserDto;
import com.negeso.module.webshop.entity.User;
import com.negeso.module.webshop.exception.service.UserRetrievingException;
import com.negeso.module.webshop.service.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id);

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);

        user.setPassword(MD5Encryption.md5(user.getPassword()));

        Long id = userRepository.save(user);

        user.setId(id);

        return userDto;
    }

    @Override
    public UserDto findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(user -> modelMapper.map(user, UserDto.class))
                .orElseThrow(() -> new UserRetrievingException("User with such [login] does not exists; login="+login));
    }

    @Override
    public void update(UserDto userDto) {
        userRepository.update(
                modelMapper.map(userDto, User.class)
        );
    }
}
