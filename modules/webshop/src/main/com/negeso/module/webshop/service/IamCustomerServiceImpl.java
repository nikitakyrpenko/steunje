package com.negeso.module.webshop.service;

import com.negeso.module.webshop.converter.impl.internal.GenericConverter;
import com.negeso.module.webshop.dto.IamCustomerDto;
import com.negeso.module.webshop.entity.ECustomer;
import com.negeso.module.webshop.exception.RetrieveException;
import com.negeso.module.webshop.exception.service.CustomerRetrievingException;
import com.negeso.module.webshop.service.interfaces.IamCustomerService;
import com.negeso.module.webshop.service.interfaces.IamUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IamCustomerServiceImpl implements IamCustomerService {

    @Autowired
    private IamCustomerRepository customerRepository;

    @Autowired
    private IamUserService userService;

    @Autowired
    private IamHairdresserRepository hairdresserRepository;

    @Autowired
    private GenericConverter<ECustomer, IamCustomerDto> converter;

    @Override
    public IamCustomerDto findById(Integer id) {
        return customerRepository
                .findById(id)
                .map(customer -> converter.convertEntityToDto(customer, IamCustomerDto.class))
                .orElseThrow(() -> new CustomerRetrievingException("Customer with such [id] does not exists"));
    }

    @Override
    public IamCustomerDto findByLogin(String login) {
        return customerRepository
                .findByLogin(login)
                .map(customer -> converter.convertEntityToDto(customer, IamCustomerDto.class))
                .orElseThrow(() -> new CustomerRetrievingException("Customer with such [login] does not exists"));
    }

    @Override
    public void save(IamCustomerDto customer) {
        /*UserDto creator = userService.findById(userCreatorId);

        customer.setCreator(creator);
*/
        customerRepository.save(
                converter.convertDtoToEntity(customer, ECustomer.class)
        );
    }

    @Override
    public void update(IamCustomerDto customer) {
        customerRepository.update(
                converter.convertDtoToEntity(customer, ECustomer.class)
        );
    }

    @Override
    public Optional<IamCustomerDto> findByIdOptional(Integer id) {
        return customerRepository
                .findById(id)
                .map(customer -> converter.convertEntityToDto(customer, IamCustomerDto.class));
    }

    @Override
    public List<IamCustomerDto> findAllByUserId(Integer userId) {
        IamHairdresser hairdresser = hairdresserRepository.findByUserId(userId)
                .orElseThrow(() -> new RetrieveException("Cannot find Hairdresser with such [creator_id]"+userId));

        return customerRepository.findAllByHairdresserId(hairdresser.getId())
                .stream()
                .map(entity -> converter.convertEntityToDto(entity, IamCustomerDto.class))
                .collect(Collectors.toList());
    }


}
