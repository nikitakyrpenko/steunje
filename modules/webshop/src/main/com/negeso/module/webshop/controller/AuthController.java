package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.mailer.MailClient;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.core.service.ParameterService;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.service.AuthService;
import com.negeso.module.webshop.service.CustomerService;
import com.restfb.json.JsonObject;
import org.apache.log4j.Logger;
import org.apache.commons.codec.digest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

import static com.negeso.framework.controller.SessionData.USER_ATTR_NAME;

@Controller
@SessionAttributes(USER_ATTR_NAME)
public class AuthController  extends JsonSupportForController {

    private static final Logger logger = Logger.getLogger(AuthController.class);

    private AuthService authService;
    private CustomerService customerService;
    private ParameterService parameterService;

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String NO_VERIFICATED_USER_ID = "user_id";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String subject = "VERIFY";
    private static final int SESSION_TIMEOUT = 3600;
    private static final String INPUT_LANGUAGE = "interfaceLanguage";
    private static final String INTERFACE_LANGUAGE = "interface-language";

    @Autowired
    public AuthController(AuthService authService, CustomerService customerService, ParameterService parameterService){
        this.authService=authService;
        this.customerService = customerService;
        this.parameterService = parameterService;
    }

//    @RequestMapping(value = "/webshop/auth", method = RequestMethod.POST)
    public void register(HttpServletRequest request,
                      HttpServletResponse response) {
         logger.debug("Adding new User");
         boolean success = false;
         String login = request.getParameter(LOGIN);
         String password = request.getParameter(PASSWORD);
         String passwordHash = DigestUtils.shaHex(password);
         String token = DigestUtils.md5Hex(login + password);
         JsonObject toResponse = new JsonObject();
         User user = null;
         Connection con = null;
         try {
             con = DBHelper.getConnection();
             user = User.findByLogin(login);
             if (user != null && user.isVerification()) {
                 toResponse.put("success", success);
                 toResponse.put("message", "User with login " + login + " already exists");
                 writeToResponse(response, toResponse);
                 return;
             } else if(user != null && (!user.isVerification())) {
                 if(!user.getPassword().equals(password)) {
                     user.setPassword(password);
                     user.setToken(token);
                     user.update();
                 }
                 Customer customer = null;
                 try {
                     customer = customerService.findByUser(user);
                 } catch (Exception e) {
                     logger.error("can not find customer by User id " + user.getId());
                 }
                 if(customer == null) {
                     customer = new Customer();
                     customer.setUserCode(user.getLogin());
                     customer.setUser(user);
                     customerService.createSafety(customer);
                 }
                 customer.setEmail(user.getLogin());

                 customerService.update(customer);
                 request.getSession().setAttribute(NO_VERIFICATED_USER_ID, user.getId());
                 toResponse.put("success", true);
                 toResponse.put("message", "Successful registration");
                 writeToResponse(response, toResponse);
                 return;
             }
         } catch (ObjectNotFoundException | IOException | SQLException e) {
             logger.error(e.getMessage());
             logger.error(Arrays.toString(e.getStackTrace()));
         } finally {
             DBHelper.close(con);
         }
         try {
             con = DBHelper.getConnection();
             user = new User();
             user.setName(login);
             user.setLogin(login);
             user.setPassword(password);
             user.setToken(token);
             Long id = user.insert();
             Customer customer = new Customer();
             customer.setUser(user);
             customer.setUserCode(user.getLogin());
             customer.setEmail(user.getLogin());
             customerService.createSafety(customer);
             request.getSession().setAttribute(NO_VERIFICATED_USER_ID, id);
             toResponse.put("success", true);
             toResponse.put("message", "Successful registration");
         } catch (IllegalArgumentException | SQLException ex) {
             toResponse.put("success", false);
             toResponse.put("message", "Wrong credentials");
         } finally {
             DBHelper.close(con);
         }
         try {
             writeToResponse(response, toResponse);
         } catch (IOException e) {
             logger.error("Could not write to response");
         }
    }
     public void verify(HttpServletRequest request,
                      HttpServletResponse response) {

    }

//    @RequestMapping(value = "/webshop/auth")
     public void fullRegistration(HttpServletRequest request,
                                  HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User userM) {
         Connection connection = null;
         JsonObject toResponse = new JsonObject();
         try {
             connection = DBHelper.getConnection();
             String name = request.getParameter("name");
             String lastName = request.getParameter("last_name");
             String postCode = request.getParameter("postcode");
             String phone = request.getParameter("phone");
             String birth = request.getParameter("birth");
             String type = request.getParameter("type");
             String company = request.getParameter("company");
             String country = request.getParameter("country");
             String street = request.getParameter("street");
             String house = request.getParameter("house");
             String city = request.getParameter("town");

             String delivery_country = request.getParameter("delivery_country");
             String delivery_name = request.getParameter("delivery_name");
             String delivery_last_name = request.getParameter("delivery_last_name");
             String delivery_postcode = request.getParameter("delivery_postcode");
             String delivery_house = request.getParameter("delivery_house");
             String delivery_street = request.getParameter("delivery_street");
             String delivery_city = request.getParameter("delivery_town");
             String delivery_company = request.getParameter("delivery_company");
             User user = userM;
             Long id = null;
             boolean sendMail = false;
             if (user != null) {
                 //TODO: to write right update
                 user.update();
                 toResponse.put("success", true);
                 toResponse.put("message", "Successfully saved contact information ");
                 writeToResponse(response, toResponse);
                 return;
             }
             Object userId = request.getSession().getAttribute(NO_VERIFICATED_USER_ID);
             if (userId != null) {
                 id = Long.valueOf(String.valueOf(userId));
                 user = User.findById(id);
                 try {
                     if (type != null && (type.equals("c") || type.equals("b"))) {
                         user.setType(type);
                         user.update();
                     }
                 } catch (NullPointerException e) {
                     ;
                 }
                 sendMail = true;
             }
             PreparedStatement stat = null;
             Long billingId = null;
             ResultSet set = null;
             Long shippingId = null;
             try {

                 stat = connection.prepareStatement("INSERT INTO contact " +
                         "(first_name, second_name, zip_code, phone, email, birth_date" +
                         ", type, company_name, country, address_line, city) " +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id");
                 if (name != null) stat.setString(1, name);
                 else stat.setString(1, "");
                 if (lastName != null) stat.setString(2, lastName);
                 else stat.setString(2, "");
                 if (postCode != null) stat.setString(3, postCode);
                 else stat.setString(3, "");
                 if (phone != null) stat.setObject(4, phone);
                 else stat.setString(4, "");
                 if (user.getLogin() != null) stat.setObject(5, user.getLogin());
                 else stat.setString(5, "");
                 if (birth != null && (!birth.isEmpty())) stat.setObject(6, Date.valueOf(LocalDate.from(FORMATTER.parse(birth))));
                 else stat.setObject(6, Date.valueOf(LocalDate.now()));
                 if (type != null) stat.setObject(7, type);
                 else stat.setObject(7, "");
                 if (company != null) stat.setObject(8, company);
                 else stat.setString(8, "");
                 if (country != null) stat.setObject(9, country);
                 else stat.setString(9, "");
                 String address = "";
                 if (street != null) address = address + street.toString().trim();
                 if (house != null) address = address + "," + house.toString().trim();
                 stat.setObject(10, address);
                 if (city != null) stat.setObject(11, city);
                 else stat.setObject(11, "");
                 set = stat.executeQuery();

                 if (set.next()) {
                     billingId = set.getLong(1);
                 }
             } catch (Exception e) {
                 logger.error(e.getMessage());
                 logger.error(Arrays.toString(e.getStackTrace()));
             } finally {
                 if (stat != null) stat.close();
             }

             try {
                 stat = connection.prepareStatement("INSERT INTO contact (first_name, second_name, zip_code, phone, email, birth_date" +
                         ", type, company_name, country, address_line, city) " +
                         " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id");
                 if (delivery_name != null) stat.setString(1, delivery_name);
                 else stat.setString(1, "");
                 if (delivery_last_name != null) stat.setString(2, delivery_last_name);
                 else stat.setString(2, "");
                 if (delivery_postcode != null) stat.setString(3, delivery_postcode);
                 else stat.setString(3, "");
                 if (phone != null) stat.setObject(4, phone);
                 else stat.setString(4, "");
                 if (user.getLogin() != null) stat.setObject(5, user.getLogin());
                 else stat.setString(5, "");
                 if (birth != null && (!birth.isEmpty())) stat.setObject(6, Date.valueOf(LocalDate.from(FORMATTER.parse(birth))));
                 else stat.setObject(6, Date.valueOf(LocalDate.now()));
                 if (type != null) stat.setObject(7, type);
                 else stat.setString(7, "");
                 if (delivery_company != null) stat.setObject(8, delivery_company);
                 else stat.setString(8, "");
                 if (delivery_country != null) stat.setObject(9, delivery_country);
                 else stat.setString(9, "");
                 String address = "";
                 if (delivery_street != null) address = address + delivery_street.toString().trim();
                 if (delivery_house != null) address = address + "," + delivery_house.toString().trim();
                 stat.setObject(10, address);
                 if (delivery_city != null) stat.setObject(11, delivery_city);
                 else stat.setObject(11, "");
                 set = stat.executeQuery();
                 if (set.next()) {
                     shippingId = set.getLong(1);
                 }
             } catch (Exception e) {
                 logger.error(e.getMessage());
                 logger.error(Arrays.toString(e.getStackTrace()));
             } finally {
                 stat.close();
             }
             try {
                 Customer customerToCreate = super.buildPojoFromRequest(request, Customer.class);
                 //TODO: to write right customer update method
                 Customer customer = customerService.findByUser(user);
                 customer.setEmail(user.getLogin());
                 customerService.update(customer);

                 if (sendMail) {
                     sendVerification(request, response, user);
                 }

                 toResponse.put("success", true);
                 toResponse.put("message", "Successful saving contact information");
             } catch (Exception e) {
                 logger.error(e.getMessage());
                 logger.error(Arrays.toString(e.getStackTrace()));
             }

         } catch (NullPointerException e) {
             System.out.println();
         } catch (SQLException | IOException | ObjectNotFoundException | IllegalArgumentException e) {
             toResponse.put("success", false);
             toResponse.put("message", "Some info was not received or illegal arguments");
             logger.error("SQLException : " + e.getMessage());
             logger.error(Arrays.toString(e.getStackTrace()));
         } finally {
             DBHelper.close(connection);
         }
         try {
             writeToResponse(response, toResponse);
             request.getSession().removeAttribute(NO_VERIFICATED_USER_ID);
         } catch (IOException e) {
             logger.error("Could not write to response");
         }
    }

//    @RequestMapping(value = "/webshop/auth/verify/user/*")
    public ModelAndView fullRegistration(HttpServletRequest request){
        Connection con = null;
        try {
            con = DBHelper.getConnection();
            String login = request.getParameter("login");
            User user = User.findByLogin(login);
            String token = request.getParameter("token");
            JsonObject toResponse = new JsonObject();
            toResponse.put("success", false);
            if (token.equals(user.getToken())) {
                user.setVerification(true);
                //TODO: to embed
//                Customer customer = Customer.findByUserId(con, user.getId());
//                customer.setVerification(true);
//                customer.update(con);
                user.update();
            }
        } catch (ObjectNotFoundException | SQLException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        } finally {
            DBHelper.close(con);
        }
        return new ModelAndView("redirect:/mail-confirm_en.html");
    }

    public void writeToResponse(HttpServletResponse response, String body) throws IOException {
        PrintWriter out = response.getWriter();
        out.print(body);
        out.close();
    }

    private void writeToResponse(HttpServletResponse response, JsonObject obj) throws IOException {
        this.writeToResponse(response, new Gson().toJson(obj));
    }

    private void writeToResponse(HttpServletResponse response, Map<String, Object> map) throws IOException {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        this.writeToResponse(response, gson.toJson(map));
    }

    private boolean  sendVerification(HttpServletRequest request,
                                      HttpServletResponse response, User user) {
        String msg = "";
        MailClient mailClient = new MailClient();
        String mailTo = null;
        String mailFrom = parameterService.findParameterByName("VERIFICATION_MAIL_FROM").getValue();
        mailTo = user.getLogin();
        String link = parameterService.findParameterByName("VERIFICATION_AUTH_LINK_WLG").getValue();
        link = link
                .concat("")
                .concat("&login=" + user.getLogin().replace("@", "%40"))
                .concat("&token=" + user.getToken());
        msg = msg + "New user with\n";
        msg = "Login : " + user.getLogin();
        msg = msg + "\nVERIFY IT : CMS -> Payment -> Customers\n";
        msg = msg + "TEMP Verification link : "
                .concat(link);
        boolean success = false;
        try {
            mailClient.sendTextPlainMessage(mailTo, mailFrom, subject, msg);
            success = true;
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            logger.error(e.getStackTrace());
        } finally {
            return success;
        }
    }
}
