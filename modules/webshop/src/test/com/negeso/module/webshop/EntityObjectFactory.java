package com.negeso.module.webshop;

import com.negeso.module.webshop.entity.*;
import org.apache.poi.hpsf.HPSFRuntimeException;

import java.sql.Date;
import java.sql.Timestamp;

public class EntityObjectFactory {

    public static IamUser getIamUser () {
        IamUser iamUser = new IamUser();

        iamUser.setId(987654);
        iamUser.setUsername("test@name");
        iamUser.setLogin("test@login");
        iamUser.setPassword("test@pass");
        iamUser.setType("test@type");
        iamUser.setExtra("test@extra");
        iamUser.setSiteId(1);
        iamUser.setPublishDate(new Date(2020, 8, 29));
        iamUser.setExpiredDate(new Date(2020, 8, 30));
        iamUser.setSingleUser(false);
        iamUser.setGuide("test@guide");
        iamUser.setLastActionDate(new Timestamp(2020, 8, 29, 0, 0, 0 ,0));
        iamUser.setToken("test@token");
        iamUser.setVerification(false);

        return iamUser;
    }

    public static IamCustomer getIamCustomer () {
        IamCustomer iamCustomer = new IamCustomer();

        iamCustomer.setId(9876543);
        iamCustomer.setLogin("test@login");



        iamCustomer.setPriceGroupId("test@priceGroupId");
        iamCustomer.setDisplayPrice("test@displayPrice");
        iamCustomer.setPostPayAllowed(false);

        return iamCustomer;
    }

    public static IamContact getIamContact (){
        IamContact iamContact = new IamContact();

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

    public static CustomerDetails getCustomerDetails (){
        CustomerDetails customerDetails = new CustomerDetails();

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
        customerDetails.setCountryCode("ua");
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

    public static IamHairdresser getIamHeirdresser (){
        IamHairdresser hairdresser = new IamHairdresser();

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

    public static IamProduct getIamProduct(){
        IamProduct product = new IamProduct();

        product.setOrderCode("04039");
        product.setProductCode("04039");
        product.setTitle("test MOTIP HITTEBESTENDIGE LAK 800°C 400ML GRIJS");
        product.setEan("8711347040391");
        product.setArticleGroup("M03");
        product.setPriceInc(8.46);
        product.setPriceExc(6.99);
        product.setStock(15);
        product.setStockMin(12);
        product.setVisible(true);
        product.setRetailPriceExc(13.85);
        product.setContent("400ml");
        product.setColor("Grijs");
        product.setCategoryName("hittebestendig-3");
        product.setBrand("MOTIP");
        product.setKeepStock(true);
        product.setMultipleOf(1);
        product.setSale(true);
        product.setDescription("Hittebestendig tot 800°C. Bestand tegen benzine, chemicaliën en weersinvloeden");
        product.setOrderNumber(8);
        product.setPackingUnit("6");

        return product;
    }

    public static IamOrderItem getIamOrderItem(){
        IamOrderItem orderItem = new IamOrderItem();

        orderItem.setQuantity(3);
        orderItem.setPrice(13.11);
        orderItem.setDiscount(13.1);

        return orderItem;
    }

    public static IamOrder getIamOrder(){
        IamOrder iamOrder = new IamOrder();

        iamOrder.setTransactionId("0000000000000113");
        iamOrder.setPrice(123.12);
        iamOrder.setCurrency("EUR");
        iamOrder.setStatus("PAYED");
        iamOrder.setPaymentMethod("CASH");
        iamOrder.setDeliveryType("DDU");
        iamOrder.setOrderDate(new Timestamp(2020, 8, 31, 0, 0, 0, 0));
        iamOrder.setComment("test comment");
        iamOrder.setDeliveryPrice(11.00);
        iamOrder.setVatPrice(1.31);

        return iamOrder;
    }
}
