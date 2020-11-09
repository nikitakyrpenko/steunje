package com.negeso.module.webshop;

import com.negeso.module.webshop.entity.modern.ECustomerDetails;

import java.sql.Date;
import java.sql.Timestamp;

public class EntityObjectFactory {

   /* public static IamUser getIamUser () {
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

    public static ECustomerDetails getCustomerDetails (){
        ECustomerDetails ECustomerDetails = new ECustomerDetails();

        ECustomerDetails.setId(98765432);

        ECustomerDetails.setBatchId(987);
        ECustomerDetails.setName("test@name");
        ECustomerDetails.setSurname("test@surname");
        ECustomerDetails.setPhone("test@phone");
        ECustomerDetails.setEmail("test@email");
        ECustomerDetails.setGender(3);
        ECustomerDetails.setAge(19);
        ECustomerDetails.setBirth("test@birth");
        ECustomerDetails.setSkinCondition(19);
        ECustomerDetails.setAddress("test@addres");
        ECustomerDetails.setCity("test@city");
        ECustomerDetails.setState("test@state");
        ECustomerDetails.setCountryCode("ua");
        ECustomerDetails.setDate("test@date");
        ECustomerDetails.setTime("test@time");
        ECustomerDetails.setLatitude(0.019f);
        ECustomerDetails.setLongitude(0.019f);
        ECustomerDetails.setTemperature(36);
        ECustomerDetails.setHumidity(19);
        ECustomerDetails.setUvIndex(19);
        ECustomerDetails.setCapturingZoneCode(18);
        ECustomerDetails.setScalpHydrationRawValue(0.0019d);
        ECustomerDetails.setScalpHydrationValue(19);
        ECustomerDetails.setScalpHydrationLevel(19);
        ECustomerDetails.setScalpHydrationDesc("test@scalpHydrationDesc");
        ECustomerDetails.setScalpHydrationOrgUrl("test@scalpHydrationOrgUrl");
        ECustomerDetails.setScalpHydrationRstUrl("test@scalpHydrationRstUrl");
        ECustomerDetails.setScalpSebumRawValue(0.0019d);
        ECustomerDetails.setScalpSebumValue(19);
        ECustomerDetails.setScalpSebumLevel(19);
        ECustomerDetails.setScalpSebumDesc("test@scalpSebumDesc");
        ECustomerDetails.setScalpSebumOrgUrl("test@scalpSebumOrgUrl");
        ECustomerDetails.setScalpSebumRstUrl("test@scalpSebumRstUrl");
        ECustomerDetails.setHairDensityRawValue(1.0019d);
        ECustomerDetails.setHairDensityValue(1);
        ECustomerDetails.setHairDensityLevel(2);
        ECustomerDetails.setHairDensityDesc("test@hairDensityDesc");
        ECustomerDetails.setHairDensityOrgUrl("test@hairDensityOrgUrl");
        ECustomerDetails.setHairDensityRstUrl("test@hairDensityRstUrl");
        ECustomerDetails.setDeadSkinCellsRawValue(2.0019d);
        ECustomerDetails.setDeadSkinCellsValue(2);
        ECustomerDetails.setDeadSkinCellsLevel(3);
        ECustomerDetails.setDeadSkinCellsDesc("test@deadSkinCellsDesc");
        ECustomerDetails.setDeadSkinCellsOrgUrl("test@deadSkinCellsOrgUrl");
        ECustomerDetails.setDeadSkinCellsRstValue("test@deadSkinCellsRstUrl");
        ECustomerDetails.setScalpImpuritiesRawValue(3.0019d);
        ECustomerDetails.setScalpImpuritiesValue(3);
        ECustomerDetails.setScalpImpuritiesLevel(4);
        ECustomerDetails.setScalpImpuritiesDesc("test@ScalpImpuritiesDesc");
        ECustomerDetails.setScalpImpuritiesOrgUrl("test@scalpImpuritiesOrgUrl");
        ECustomerDetails.setScalpImpuritiesRstUrl("test@scalpImpuritiesRstUrl");
        ECustomerDetails.setComments("test@comments");

        return ECustomerDetails;
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
    }*/
}
