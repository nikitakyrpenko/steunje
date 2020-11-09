package com.negeso.module.webshop.controller;


import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.spring.interceptor.Unsecured;

import com.negeso.module.webshop.converter.impl.jsons.JsonToCustomerDetailsConverter;
import com.negeso.module.webshop.converter.impl.jsons.JsonToIamHairdresserConverter;
import com.negeso.module.webshop.dto.*;
import com.negeso.module.webshop.service.interfaces.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class HairdresserController {
    private static final Logger logger = Logger.getLogger(HairdresserController.class);

    private final HairdresserService hairdresserService;
    private final IamCustomerService customerService;
    private final IamProductService productService;
    private final IamOrderService orderService;
    private final CustomerDetailsService customerDetailsService;
    private final JsonToCustomerDetailsConverter jsonToCustomerDetailsConverter;
    private final JsonToIamHairdresserConverter jsonToIamHairdresserConverter;
    private final IamOrderService iamOrderService;

    @Autowired
    public HairdresserController(HairdresserService hairdresserService,
                                 IamCustomerService customerService,
                                 IamProductService productService,
                                 IamOrderService orderService,
                                 CustomerDetailsService customerDetailsService,
                                 JsonToCustomerDetailsConverter jsonToCustomerDetailsConverter,
                                 JsonToIamHairdresserConverter jsonToIamHairdresserConverter,
                                 IamOrderService iamOrderService) {
        this.hairdresserService = hairdresserService;
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
        this.customerDetailsService = customerDetailsService;
        this.jsonToCustomerDetailsConverter = jsonToCustomerDetailsConverter;
        this.jsonToIamHairdresserConverter = jsonToIamHairdresserConverter;
        this.iamOrderService = iamOrderService;
    }

    @RequestMapping(value = "/Chowis/GetCustomer", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<String> save(@RequestBody String content, HttpServletRequest request){

        User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);

        String jsonContent = paramJson(content);

        CustomerDetailsDto customerDetail = jsonToCustomerDetailsConverter.convertStringJsonToDTO(jsonContent, CustomerDetailsDto.class);

        CustomerDetailsDto result = hairdresserService.saveCustomerDetails(customerDetail, user.getId().intValue());

        String response = jsonToCustomerDetailsConverter.convertDtoToJsonString(result);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    /*
     * Method saves hairdresser details
     */
    @RequestMapping(value = "/webshop/api/hairdresser/save", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    @Unsecured
    public ResponseEntity<String> registerHairdresser (@RequestBody String content) {
        IamHairdresserDto hairdresser = jsonToIamHairdresserConverter.convertStringJsonToDTO(content, IamHairdresserDto.class);
        IamHairdresserDto result = hairdresserService.save(hairdresser);
        if(result.getId()==null){
            return new ResponseEntity<>("This Login/email exists!", HttpStatus.BAD_REQUEST);
        }
        String resp = jsonToIamHairdresserConverter.convertDtoToJsonString(result);

        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /*
     * Method returns all customers of the given hairdresser
     */
    @RequestMapping(value = "/webshop/api/hairdresser/customers", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<List<IamCustomerDto>> getAllCustomersForHairdresser(HttpServletRequest request){

        User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);

        List<IamCustomerDto> result = customerService.findAllByUserId(user.getId().intValue());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/webshop/api/hairdresser/customer/{id}/details", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<List<CustomerDetailsDto>> getAllCustomerDetailsByCustomerId(@PathVariable("id") Integer id){
        List<CustomerDetailsDto> all = customerDetailsService.findAllByCustomerId(id);

        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @RequestMapping(value = "/webshop/api/hairdresser/products", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> allProducts = productService.findAll();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @RequestMapping(value = "/webshop/api/hairdresser/customer/order", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<IamOrderDto> saveOrderForGivenCustomer(@RequestBody Map<String, Object> parameters, HttpServletRequest request){
        Integer customerId = (Integer) parameters.get("customerId");
        Integer customerDetailsId = (Integer) parameters.get("scan_id");

        IamOrderDto result = orderService.createOrderByCustomerId(customerId, customerDetailsId);

        HttpSession session = request.getSession();

        session.setAttribute("order_id", result.getId());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/webshop/api/hairdresser/order/order_item", method = RequestMethod.GET)
    public ModelAndView addOrderItem (@RequestParam("productId") String productId,
                                      @RequestParam("quantity") String quantity,
                                      HttpServletRequest request){
        String orderId = request
                    .getSession()
                    .getAttribute("order_id")
                    .toString();

        iamOrderService.insertOrderItemInOrder(orderId, quantity, productId);


        return new ModelAndView("forward:/webshop/api/hairdresser/order/order_items");
    }
    /*
     * Process order to it's final state: compute total price, init order date, set DELIVERY TYPE
     */
    @RequestMapping(value = "/webshop/api/hairdresser/order/confirm", method = RequestMethod.GET)
    public ModelAndView commitOrderByOrderId(HttpServletRequest request,
                                             @RequestParam(value = "payment_method") String paymentMethod){
        HttpSession session = request.getSession();

        Integer orderId = (Integer) session.getAttribute("order_id");

        orderService.commitOrderByOrderId(orderId, paymentMethod);

        session.removeAttribute("order_id");

        return new ModelAndView("redirect:/webshop/api/hairdresser/order/order_items?orderId="+orderId);
    }

    @RequestMapping(value = "/webshop/api/hairdresser/order/delete_item", method = RequestMethod.GET)
    public ModelAndView deleteOrderItem (@RequestParam("productId") String productId,
                                                   HttpServletRequest request){
        String orderId = request
                .getSession()
                .getAttribute("order_id")
                .toString();

        iamOrderService.deleteOrderItem(orderId, productId);

        request.removeAttribute("productId");

        return new ModelAndView("forward:/webshop/api/hairdresser/order/order_items");
    }

    /*
        Get full information about order by id; If orderId is not present in request -> get order from session
     */
    @RequestMapping(value = "/webshop/api/hairdresser/order/order_items", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<IamOrderDto> getOrder (HttpServletRequest request,
                                                 @RequestParam(value = "orderId", required = false) String orderId){

        String retrievedOrderId;

        if (Objects.nonNull(orderId)){
            retrievedOrderId = orderId;
        }else {
            retrievedOrderId = request
                    .getSession()
                    .getAttribute("order_id")
                    .toString();
        }

        IamOrderDto iamOrderDto = iamOrderService
                .findById(Integer.parseInt(retrievedOrderId));

        return new ResponseEntity<>(iamOrderDto, HttpStatus.OK);
    }

    /*Parse ulr-encoding to json */
    public  String paramJson(String paramIn) {
        paramIn = paramIn.replaceAll("=", "\":\"");
        paramIn = paramIn.replaceAll("&", "\",\"");
        return "{\"" + paramIn + "\"}";
    }

    private String generateSuccessResponse(Integer id, String text){
        return String.format("{\"success\" : true, \"%s\" : %s}", text, id);
    }

}
