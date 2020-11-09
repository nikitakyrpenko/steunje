package com.negeso.module.webshop.controller.modern;

import com.negeso.framework.util.spring.interceptor.Unsecured;
import com.negeso.module.webshop.service.modern.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.stream.Stream;

@Controller
public class CartItemController {

    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService){
        this.cartItemService = cartItemService;
    }

    @Unsecured
    @RequestMapping(value = "/webshop/api/testcookie", method = RequestMethod.POST)
    public ResponseEntity<String> testCookie(HttpServletRequest request,
                                             HttpServletResponse response,
                                             @RequestParam("productCode") String productCode,
                                             @RequestParam("quantity") Integer quantity) {

     //   User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
        Cookie[] cookies = request.getCookies();

        Cookie cookie = Stream.of(cookies)
                .filter(c -> c != null && c.getName().equals("cartCookie"))
                .findFirst()
                .orElse(new Cookie("cartCookie",String.valueOf(UUID.randomUUID())));

        response.addCookie(cookie);
        String cookieString = cookie.getValue();

        cartItemService.createAndSaveCartItemByProductNumberAndQuantityAndCartOwnerId(productCode,quantity,cookieString);
        return new ResponseEntity<>("Cookie created : " + cookie.getValue(), org.springframework.http.HttpStatus.OK);

    }

}
