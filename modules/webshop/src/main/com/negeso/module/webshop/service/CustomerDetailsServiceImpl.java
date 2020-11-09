package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dto.CustomerDetailsDto;
import com.negeso.module.webshop.exception.RetrieveException;
import com.negeso.module.webshop.service.interfaces.CustomerDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerDetailsServiceImpl implements CustomerDetailsService {

    private final IamCustomerRepository iamCustomerRepository;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomerDetailsServiceImpl(IamCustomerRepository iamCustomerRepository,
                                      CustomerDetailsRepository customerDetailsRepository,
                                      ModelMapper modelMapper) {
        this.iamCustomerRepository = iamCustomerRepository;
        this.customerDetailsRepository = customerDetailsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerDetailsDto findById(Integer id) {
        return customerDetailsRepository.findById(id)
                .map(entity -> modelMapper.map(entity, CustomerDetailsDto.class))
                .orElseThrow(() -> new RetrieveException("Cannot find CustomerDetails by [id]"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDetailsDto> findCustomerDetailsByCustomerLogin(String login) {

     /*   IamCustomer iamCustomer = iamCustomerRepository
                .findByLogin(login)
                .orElseThrow(() -> new CustomerRetrievingException("Customer with such [login] does not exists"));

        List<CustomerDetailsDto> customerDetailsDtoList =
                iamCustomer.getCustomerDetails()
                .stream()
                .map((customerDetails -> new GenericConverter<CustomerDetails, CustomerDetailsDto>(modelMapper).convertEntityToDto(customerDetails, CustomerDetailsDto.class)))
                .collect(Collectors.toList());

        return customerDetailsDtoList
                .stream()
                .map(this::checkUrl)
                .collect(Collectors.toList());*/

        return new ArrayList<>();
    }

    @Override
    public List<CustomerDetailsDto> findAllByCustomerId(Integer customerId) {
        /*IamCustomer customer = iamCustomerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerRetrievingException("Cannot find Customer by [id]"));

        return customer
                .getCustomerDetails()
                .stream()
                .map(entity -> modelMapper.map(entity, CustomerDetailsDto.class))
                .map(this::checkUrl)
                .sorted(this::compareByDate)
                .collect(Collectors.toList());*/
        return new ArrayList<>();
    }

    private int compareByDate(CustomerDetailsDto o1, CustomerDetailsDto o2){
        Date dateOne = convertStringToDate(o1.getDate());
        Date dateTwo = convertStringToDate(o2.getDate());

        return dateOne.compareTo(dateTwo);
    }

    private Date convertStringToDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse [date]="+date);
        }
    }

    private CustomerDetailsDto checkUrl (CustomerDetailsDto customerDetailsDto){
        CustomerDetailsDto customerDetails = customerDetailsDto;

        String checkedUrl = customerDetails.getScalpSebumOrgUrl();
        if(checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setScalpSebumOrgUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getScalpSebumRstUrl();
        if(checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setScalpSebumRstUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getHairDensityOrgUrl();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setHairDensityOrgUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getHairDensityRstUrl();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setHairDensityRstUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getDeadSkinCellsOrgUrl();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setDeadSkinCellsOrgUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getDeadSkinCellsRstValue();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setDeadSkinCellsRstValue(checkedUrl);
        }

        checkedUrl = customerDetails.getScalpImpuritiesOrgUrl();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setScalpImpuritiesOrgUrl(checkedUrl);
        }

        checkedUrl = customerDetails.getScalpImpuritiesRstUrl();
        if (checkedUrl != null){
            checkedUrl = replaceSymbol(checkedUrl);
            customerDetails.setScalpImpuritiesRstUrl(checkedUrl);
        }

        return customerDetails;
    }

    private String replaceSymbol (String url){
        url = url.replaceAll("%3A", ":");
        url = url.replaceAll("%2F", "/");
        return url;
    }
}
