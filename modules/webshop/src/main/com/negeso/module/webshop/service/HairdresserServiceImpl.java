package com.negeso.module.webshop.service;

import com.negeso.framework.util.MD5Encryption;
import com.negeso.module.webshop.converter.impl.internal.GenericConverter;
import com.negeso.module.webshop.dto.*;
import com.negeso.module.webshop.entity.CustomerDetails;
import com.negeso.module.webshop.entity.IamUser;
import com.negeso.module.webshop.exception.RetrieveException;
import com.negeso.module.webshop.exception.SavingException;
import com.negeso.module.webshop.exception.service.HairdresserRetrievingException;
import com.negeso.module.webshop.service.interfaces.HairdresserService;
import com.negeso.module.webshop.service.interfaces.IamCustomerService;
import com.negeso.module.webshop.service.interfaces.IamUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class HairdresserServiceImpl implements HairdresserService {
    private static final Logger logger = Logger.getLogger(HairdresserServiceImpl.class);


    private final GenericConverter<CustomerDetails, CustomerDetailsDto> genericConverter;
    private final GenericConverter<IamHairdresser, IamHairdresserDto> genericConverterForHairdresser;

    private final IamUserService iamUserService;
    private final IamCustomerService iamCustomerService;
    private final IamHairdresserRepository iamHairdresserRepository;
    private final CustomerDetailsRepository customerDetailsRepository;

    @Autowired
    public HairdresserServiceImpl(CustomerDetailsRepository customerDetailsRepository,
                                  IamHairdresserRepository iamHairdresserRepository,
                                  IamUserService iamUserService,
                                  IamCustomerService iamCustomerService,
                                  GenericConverter<IamHairdresser, IamHairdresserDto> genericConverterForHairdresser,
                                  GenericConverter genericConverter) {

        this.customerDetailsRepository = customerDetailsRepository;
        this.iamHairdresserRepository = iamHairdresserRepository;
        this.iamCustomerService = iamCustomerService;
        this.iamUserService = iamUserService;
        this.genericConverter = genericConverter;
        this.genericConverterForHairdresser = genericConverterForHairdresser;
    }

    //TODO SET SESSION HAIRDRESSER
    /* If customer with such id is exists - then save customerDetails with this customer id ;
     *  Else if customer not exists - then create new customer and save customer details to this customer*/
    @Override
    public CustomerDetailsDto saveCustomerDetails(CustomerDetailsDto dto, Integer creatorId) {

        Integer savedCustomerDetailsIdToRetrieve;

        Optional<IamCustomerDto> customer = iamCustomerService.findByIdOptional(dto.getCustomerId());

        IamHairdresserDto creator = iamHairdresserRepository
                .findByUserId(creatorId)
                .map(hairdresser -> genericConverterForHairdresser.convertEntityToDto(hairdresser, IamHairdresserDto.class))
                .orElseThrow(() -> new HairdresserRetrievingException("Hairdresser with [id]=" + creatorId + "not exists"));

        if (customer.isPresent()) {
            IamCustomerDto customerDto = customer.get();
            UserDto userDto = iamUserService.findByLogin(customerDto.getLogin()).get();

            customerDto.setCreator(creator);

            updateAllInfoFromCustomerDetailsDto(userDto, customerDto.getBillingContact(), customerDto.getShippingContact() ,customerDto, dto, false);

            iamUserService.update(userDto);

            iamCustomerService.update(customerDto);

            savedCustomerDetailsIdToRetrieve = customerDetailsRepository.save(genericConverter.convertDtoToEntity(dto, CustomerDetails.class));
        } else {
            UserDto userDto = new UserDto();
            IamContactDto billingContact = new IamContactDto();
            IamContactDto shippingContact = new IamContactDto();
            IamCustomerDto customerDto = new IamCustomerDto();

            updateAllInfoFromCustomerDetailsDto(userDto, billingContact,shippingContact,customerDto, dto, true);

            customerDto.setBillingContact(billingContact);
            customerDto.setShippingContact(shippingContact);
            customerDto.setCreator(creator);

            iamUserService.save(userDto);

            iamCustomerService.save(customerDto);

            CustomerDetails entity = genericConverter.convertDtoToEntity(dto, CustomerDetails.class);

            savedCustomerDetailsIdToRetrieve = customerDetailsRepository.save(entity);
        }

        Optional<CustomerDetails> customerDetails = customerDetailsRepository.findById(savedCustomerDetailsIdToRetrieve);

        return customerDetails
                .map(resultEntity -> genericConverter.convertEntityToDto(resultEntity, CustomerDetailsDto.class))
                .orElseThrow(() ->
                        new RetrieveException("Cannot find [CustomerDetails] with such id"));
    }

    @Override
    public IamHairdresserDto save(IamHairdresserDto hairdresserDto) {
        IamHairdresser hairdresser = genericConverterForHairdresser.convertDtoToEntity(hairdresserDto, IamHairdresser.class);
        if(iamUserService.findByLogin(hairdresser.getEmail()).isPresent()){
            ex();
            return new IamHairdresserDto();
        }

        IamUser iamUser = new IamUser();
        iamUser.setLogin(hairdresser.getEmail());
        iamUser.setUsername(hairdresser.getName());
        iamUser.setPassword(MD5Encryption.md5(hairdresser.getEmail()));
        iamUser.setType("hairdresser");
        iamUser.setSiteId(1);
        iamUser.setVerification(false);

        hairdresser.setUser(iamUser);

        Optional<IamHairdresser> result = iamHairdresserRepository.findById(iamHairdresserRepository.save(hairdresser));

        return result
                .map(entity -> genericConverterForHairdresser.convertEntityToDto(entity, IamHairdresserDto.class))
                .orElseThrow(() -> {
                    logger.error("Cannot find IamHairdresser with such [id]");
                    return new SavingException("Cannot find IamHairdresser with such [id]");
                });
    }

    @Override
    public IamHairdresserDto findByUserId(Integer userId) {
        return iamHairdresserRepository.findByUserId(userId)
                .map(entity -> genericConverterForHairdresser.convertEntityToDto(entity, IamHairdresserDto.class))
                .orElseThrow(() -> new HairdresserRetrievingException("Cannot find IamHairdresser with such [userId]"+userId));
    }


    private void updateAllInfoFromCustomerDetailsDto(UserDto userDto,
                                                     IamContactDto billingContact,
                                                     IamContactDto shippingContact,
                                                     IamCustomerDto customerDto,
                                                     CustomerDetailsDto dto,
                                                     boolean isPasswordChanges){
        updateUserFromCustomerDetails(userDto, dto, isPasswordChanges);
        updateContactFromCustomerDetails(billingContact, dto);
        updateContactFromCustomerDetails(shippingContact, dto);
        updateCustomerFromCustomerDetails(customerDto, dto);
    }


    private void updateContactFromCustomerDetails(IamContactDto contact, CustomerDetailsDto customerDetails) {
        contact.setPhone(customerDetails.getPhone());
        contact.setCity(customerDetails.getCity());
        contact.setPhone(customerDetails.getPhone());
        contact.setEmail(escapeEmailEncoding(customerDetails.getEmail()));
        contact.setFirstName(customerDetails.getName());
        contact.setSecondName(customerDetails.getSurname());
        contact.setAddressLine(customerDetails.getAddress());
        //billingContact.setBirthDate(LocalDate.customerDetails.getDate());
        contact.setAddressLine(customerDetails.getAddress());
    }

    private void updateCustomerFromCustomerDetails(IamCustomerDto customer, CustomerDetailsDto customerDetailsDto) {
        customer.setLogin(escapeEmailEncoding(customerDetailsDto.getEmail()));
        customer.setEmail(escapeEmailEncoding(customerDetailsDto.getEmail()));
        customer.setId(customerDetailsDto.getCustomerId());
        customer.setPostPayAllowed(false);
    }

    private void updateUserFromCustomerDetails(UserDto user, CustomerDetailsDto customerDetails, boolean shouldChangePassword){
        user.setLogin(escapeEmailEncoding(customerDetails.getEmail()));
        user.setUsername(customerDetails.getName() + " "+ customerDetails.getSurname());
        user.setType("visitor");
        user.setVerification(false);
        user.setSingleUser(false);

        if (shouldChangePassword)
            user.setPassword(MD5Encryption.md5(escapeEmailEncoding(customerDetails.getEmail())));
    }

    private void ex ()throws RetrieveException{
        new RetrieveException("This user with this login/email is exist!");
    }

    private String escapeEmailEncoding(String email){
        return email.replace("%40", "@");
    }
}
