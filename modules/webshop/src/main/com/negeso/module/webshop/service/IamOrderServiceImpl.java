package com.negeso.module.webshop.service;

import com.negeso.module.webshop.converter.impl.internal.GenericConverter;
import com.negeso.module.webshop.dto.*;
import com.negeso.module.webshop.entity.IamOrder;
import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.exception.service.OrderRetrievingException;
import com.negeso.module.webshop.service.interfaces.CustomerDetailsService;
import com.negeso.module.webshop.service.interfaces.IamCustomerService;
import com.negeso.module.webshop.service.interfaces.IamOrderItemService;
import com.negeso.module.webshop.service.interfaces.IamOrderService;
import com.negeso.module.webshop.service.interfaces.IamProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

@Service
public class IamOrderServiceImpl implements IamOrderService {

    private static final int TRANSACTION_ID_TOTAL_DIGITS = 16;

    private final Function<OrderItemDto, Double> PRICE_WITHOUT_VAT_PROVIDER =
            orderItemDto -> orderItemDto.getProduct().getPriceExc();

    private final Function<OrderItemDto, Double> PRICE_WITH_VAT_SUPPLIER =
            orderItemDto -> orderItemDto.getProduct().getPriceInc();

    @Autowired private ModelMapper modelMapper;

    @Autowired private IamCustomerService customerService;

    @Autowired private IamOrderRepository orderRepository;

    @Autowired private CustomerDetailsService customerDetailsService;

    @Autowired private GenericConverter<IamOrder, IamOrderDto> converter;

    @Autowired private IamProductService iamProductService;

    @Autowired
    private IamOrderItemService iamOrderItemService;

    @Override
    public IamOrderDto createOrderByCustomerId(Integer customerId, Integer customerDetailsId) {
        IamCustomerDto customer = customerService.findById(customerId);

        CustomerDetailsDto cdd = customerDetailsService.findById(customerDetailsId);

        IamOrderDto iamOrderDto = initOrderWithCustomer(customer, cdd);

        Integer id = orderRepository.save(modelMapper.map(iamOrderDto, IamOrder.class));

        iamOrderDto.setId(id);
        iamOrderDto.setTransactionId(generateTransactionIdForOrder(id));

        orderRepository.update(modelMapper.map(iamOrderDto, IamOrder.class));

       IamOrderDto result = orderRepository.findById(id)
                .map(order -> modelMapper.map(order, IamOrderDto.class))
                .orElseThrow(() -> new OrderRetrievingException("Cannot find Order by [id]"));

        return result;
    }

    @Override
    public Integer insertOrderItemInOrder(String orderId, String quantity, String productId) {
        IamOrderDto iamOrderDto = findById(Integer.parseInt(orderId));

        ProductDto productDto = iamProductService.findByProductNumber(productId);

        OrderItemDto itemToUpdate = iamOrderDto.getOrderItems()
                .stream()
                .filter(orderItemDto -> orderItemDto.getProduct().equals(productDto))
                .findAny()
                .orElseGet(() -> initOrderItem(productDto,
                        0,
                        productDto.getPriceInc(),
                        iamOrderDto.getId()));


        itemToUpdate.setQuantity(itemToUpdate.getQuantity() + Integer.parseInt(quantity));

        List<OrderItemDto> orderItems = iamOrderDto.getOrderItems();


        orderItems.add(itemToUpdate);
        iamOrderDto.setOrderItems(orderItems);

        return orderRepository
                .update(
                        converter.convertDtoToEntity(iamOrderDto, IamOrder.class)
                );
    }

    @Override
    public void deleteOrderItem(String orderId, String productId) {
        IamOrderDto iamOrderDto = findById(Integer.parseInt(orderId));
        OrderItemDto deleted = null;
        Integer id = null;

        for (OrderItemDto orderItemDto : iamOrderDto.getOrderItems()){
            if(productId.equals(orderItemDto.getProduct().getProductCode())){
                deleted = orderItemDto;
                id = orderItemDto.getId();
            }
        }
        if(deleted != null) {
            iamOrderDto.getOrderItems().remove(deleted);
            orderRepository.update(
                    converter.convertDtoToEntity(iamOrderDto, IamOrder.class)
            );
            iamOrderItemService.deleteOrderItem(id);
        }
    }

    @Override
    public IamOrderDto findById(Integer id) {
        IamOrderDto iamOrderDto = orderRepository
                .findById(id)
                .map(iamOrder -> converter.convertEntityToDto(iamOrder, IamOrderDto.class))
                .orElseThrow(() -> new OrderRetrievingException("Order with such [id] does not exist"));

        Double totalPrice = checkIsCountryVatPayableAndComputeOrderTotalPrice(iamOrderDto);

        iamOrderDto.setPrice(totalPrice);

        return iamOrderDto;
    }

    @Override
    public IamOrderDto commitOrderByOrderId(Integer orderId, String paymentMethod) {
        IamOrderDto iamOrderDto = orderRepository.findById(orderId)
                .map(order -> converter.convertEntityToDto(order, IamOrderDto.class))
                .orElseThrow(() -> new OrderRetrievingException("Order with such [id] does not exist"));

        commitOrder(iamOrderDto, paymentMethod);

        Integer id = orderRepository.update(converter.convertDtoToEntity(iamOrderDto, IamOrder.class));

        return orderRepository.findById(id)
                .map(order -> converter.convertEntityToDto(order, IamOrderDto.class))
                .orElseThrow(() -> new OrderRetrievingException("Order with such [id] does not exist"));

    }

    private void commitOrder(IamOrderDto orderDto, String paymentMethod) {
        Double totalPrice = checkIsCountryVatPayableAndComputeOrderTotalPrice(orderDto);

        orderDto.setOrderDate(new Timestamp(System.currentTimeMillis()));
        orderDto.setStatus(Order.Status.CLOSED.name());
        orderDto.setPaymentMethod(paymentMethod);
        orderDto.setPrice(totalPrice);
    }

    private Double checkIsCountryVatPayableAndComputeOrderTotalPrice(IamOrderDto iamOrderDto){
        Double totalPrice;

        if (extractCountryNameFromOrderDto(iamOrderDto).equals("NL")){
             totalPrice = computeOrderTotalPrice(iamOrderDto, PRICE_WITHOUT_VAT_PROVIDER);
        }else {
            totalPrice = computeOrderTotalPrice(iamOrderDto, PRICE_WITH_VAT_SUPPLIER);
        }
         return totalPrice;
    }

    /*
      Compute total order price by the given function
     */
    private Double computeOrderTotalPrice(IamOrderDto orderDto, Function<OrderItemDto, Double> productPriceProvider){
        return orderDto.getOrderItems()
                .stream()
                .reduce(0.0,
                        (sum, orderItem) -> sum + (orderItem.getQuantity() * productPriceProvider.apply(orderItem)),
                        Double::sum);
    }

    private String extractCountryNameFromOrderDto(IamOrderDto iamOrderDto){
        return iamOrderDto.getUserName().getShippingContact().getCountry();
    }

    private IamOrderDto initOrderWithCustomer(IamCustomerDto customer, CustomerDetailsDto cdd) {
        IamOrderDto order = new IamOrderDto();

        order.setUserName(customer);
        order.setCustomerDetails(cdd);
        order.setStatus(Order.Status.OPENED.name());
        order.setCurrency("EUR");
        order.setDeliveryType("DDU");
        order.setPrice(0.0);

        return order;
    }

    private OrderItemDto initOrderItem (ProductDto productDto,
                                        Integer quantity,
                                        Double price,
                                        Integer order){
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProduct(productDto);
        orderItemDto.setQuantity(quantity);
        orderItemDto.setPrice(price);
        orderItemDto.setDiscount(0d);
        orderItemDto.setOrder(order);
        return orderItemDto;
    }

    private String generateTransactionIdForOrder(Integer id) {
        return appendLeadingZerosToTransactionId(id.toString());
    }

    private String appendLeadingZerosToTransactionId(String strId){
        StringBuilder result = new StringBuilder(strId);

        for (int i = strId.length(); i < TRANSACTION_ID_TOTAL_DIGITS; i++){
            result.insert(0, "0");
        }

        return result.toString();
    }
}
