package com.negeso.module.webshop;

import com.negeso.module.webshop.dto.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DtoObjectFactory {

    public static UserDto getUserDto(){
        UserDto iamUser = new UserDto();

        iamUser.setId(987654L);
        iamUser.setUsername("test@name");
        iamUser.setLogin("test@login");
        iamUser.setPassword("test@pass");
        iamUser.setType("test@type");
        iamUser.setPublishDate(Timestamp.valueOf(LocalDateTime.of(2001,1,3,12,31)));
        iamUser.setExpiredDate(Timestamp.valueOf(LocalDateTime.of(2001,1,3,12,31)));
        iamUser.setSingleUser(false);
        iamUser.setToken("test@token");
        iamUser.setVerification(false);

        return iamUser;
    }

    public static IamCustomerDto getCustomerDto(){
        IamCustomerDto iamCustomer = new IamCustomerDto();

        iamCustomer.setId(9876543);
        iamCustomer.setLogin("test@login");

        iamCustomer.setPriceGroupId("test@priceGroupId");
        iamCustomer.setDisplayPrice("test@displayPrice");
        iamCustomer.setPostPayAllowed(false);

        return iamCustomer;
    }

    public static IamContactDto getContactDto(){
        IamContactDto iamContact = new IamContactDto();

        iamContact.setId(98765);
        iamContact.setFirstName("test@firstName");
        iamContact.setSecondName("test@secondName");
        iamContact.setCompanyName("test@companyName");
        iamContact.setAddressLine("test@addresLine");
        iamContact.setZipCode("test@zipCode");
        iamContact.setState("test@state");
        iamContact.setCity("test@city");
        iamContact.setCountry("test@country");
        iamContact.setPhone("test@phone");
        iamContact.setFax("tets@fax");
        iamContact.setWebLink("test@webLink");
        iamContact.setEmail("test@email");
        iamContact.setImageLink("test@imageLink");
        iamContact.setType("test@type");
        iamContact.setDepartment("test@department");
        iamContact.setJobTitle("test@jobTitle");
        iamContact.setBirthDate(new Timestamp(2020, 8, 31, 0, 0, 0, 0));
        iamContact.setNickname("test@nickname");
        iamContact.setEstablishment("test@establishment");
        iamContact.setCheckbox(19);
        iamContact.setManager("test@manager");
        iamContact.setMobile("test@mobile");
        iamContact.setOfficeNumber("test@officeNumber");
        iamContact.setMemo1("test@memo1");
        iamContact.setMemo2("test@memo2");

        return iamContact;
    }


    public static CustomerDetailsDto getCustomerDetailsDto (){
        CustomerDetailsDto customerDetails = new CustomerDetailsDto();

        customerDetails.setId(98765432);

        customerDetails.setBatchId(987);
        customerDetails.setName("test@name");
        customerDetails.setSurname("test@surname");
        customerDetails.setPhone("test@phone");
        customerDetails.setEmail("test@email");
        customerDetails.setGender(3);
        customerDetails.setAge(19);
        customerDetails.setBirth("test@birth");
        customerDetails.setSkinCondition(19);
        customerDetails.setAddress("test@addres");
        customerDetails.setCity("test@city");
        customerDetails.setState("test@state");
        customerDetails.setCountryCode("test@countryCode");
        customerDetails.setDate("test@date");
        customerDetails.setTime("test@time");
        customerDetails.setLatitude(0.019f);
        customerDetails.setLongitude(0.019f);
        customerDetails.setTemperature(36);
        customerDetails.setHumidity(19);
        customerDetails.setUvIndex(19);
        customerDetails.setCapturingZoneCode(18);
        customerDetails.setScalpHydrationRawValue(0.0019d);
        customerDetails.setScalpHydrationValue(19);
        customerDetails.setScalpHydrationLevel(19);
        customerDetails.setScalpHydrationDesc("test@scalpHydrationDesc");
        customerDetails.setScalpHydrationOrgUrl("test@scalpHydrationOrgUrl");
        customerDetails.setScalpHydrationRstUrl("test@scalpHydrationRstUrl");
        customerDetails.setScalpSebumRawValue(0.0019d);
        customerDetails.setScalpSebumValue(19);
        customerDetails.setScalpSebumLevel(19);
        customerDetails.setScalpSebumDesc("test@scalpSebumDesc");
        customerDetails.setScalpSebumOrgUrl("test@scalpSebumOrgUrl");
        customerDetails.setScalpSebumRstUrl("test@scalpSebumRstUrl");
        customerDetails.setHairDensityRawValue(1.0019d);
        customerDetails.setHairDensityValue(1);
        customerDetails.setHairDensityLevel(2);
        customerDetails.setHairDensityDesc("test@hairDensityDesc");
        customerDetails.setHairDensityOrgUrl("test@hairDensityOrgUrl");
        customerDetails.setHairDensityRstUrl("test@hairDensityRstUrl");
        customerDetails.setDeadSkinCellsRawValue(2.0019d);
        customerDetails.setDeadSkinCellsValue(2);
        customerDetails.setDeadSkinCellsLevel(3);
        customerDetails.setDeadSkinCellsDesc("test@deadSkinCellsDesc");
        customerDetails.setDeadSkinCellsOrgUrl("test@deadSkinCellsOrgUrl");
        customerDetails.setDeadSkinCellsRstValue("test@deadSkinCellsRstUrl");
        customerDetails.setScalpImpuritiesRawValue(3.0019d);
        customerDetails.setScalpImpuritiesValue(3);
        customerDetails.setScalpImpuritiesLevel(4);
        customerDetails.setScalpImpuritiesDesc("test@ScalpImpuritiesDesc");
        customerDetails.setScalpImpuritiesOrgUrl("test@scalpImpuritiesOrgUrl");
        customerDetails.setScalpImpuritiesRstUrl("test@scalpImpuritiesRstUrl");
        customerDetails.setComments("test@comments");

        return customerDetails;
    }

    public static IamHairdresserDto getHairdresserDto(){
        IamHairdresserDto hairdresser = new IamHairdresserDto();

        hairdresser.setId(1);
        hairdresser.setName("me");
        hairdresser.setLastname("lastname");
        hairdresser.setBirthdate("123");
        hairdresser.setBtwNumber("123");
        hairdresser.setKvkNumber("123");
        hairdresser.setTelephone("123123123");
        hairdresser.setEmail("hairdresser@test.com");

        return hairdresser;
    }
}
