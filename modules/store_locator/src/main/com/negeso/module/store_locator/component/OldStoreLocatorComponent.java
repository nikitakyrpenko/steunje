package com.negeso.module.store_locator.component;

import com.negeso.module.store_locator.domain.City;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageComponent;
import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Contact;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Jan 20, 2005
 */

public class OldStoreLocatorComponent implements PageComponent {
    private static Logger logger = Logger.getLogger( OldStoreLocatorComponent.class );
    public Element getElement(Document document, RequestContext request, Map parameters) {
        Element store_locator = Env.createDomElement(document, "store_locator");
        Connection conn = null;
        try{
            conn = DBHelper.getConnection();
            if (request.getParameter("todo")==null ||
                    request.getParameter("todo").equals("")){
                //SEARCH MODE
                renderSearchMode(store_locator, request, conn);
                logger.error("SEARCH_MODE");
            }
            else if (request.getParameter("todo").equals("show")){
                //SHOW MODE
                renderShowMode(store_locator, request, conn);
                logger.error("SHOW_MODE");
            }
            else if (request.getParameter("todo").equals("detail")){
                logger.error("DETAIL_MODE");
                renderDetailMode(store_locator, request, conn);
            }
            else{
                logger.error("ERROR_MODE");
            }
        }
        catch (Exception e){
            logger.error("ERROR IN STORE LOCATOR");
            logger.error(e.getMessage(), e);
        }
        finally{
            try{
                if (conn!=null && !conn.isClosed()){
                    DBHelper.close(conn);
                }
            }
            catch(Exception e){
                logger.error("Can't close connection");
            }
        }
        return store_locator;
    }

    public void renderShowMode(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", "show");
        setAttributes(parent, request);
        createShopListXml(parent,request,conn);
    }

    public void renderSearchMode(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", "search");
        //setAttributes(parent, request);
        createCityListXml(parent,request,conn);
    }

    public void renderDetailMode(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", "detail");
        setAttributes(parent, request);
        createDetailXml(parent,request,conn);
    }

    public void setAttributes(Element parent, RequestContext request)
            throws Exception{
        parent.setAttribute("sl_criteria", request.getParameter("sl_criteria"));
        parent.setAttribute("sl_chosen_1", request.getParameter("sl_chosen_1"));
        parent.setAttribute("sl_chosen_2", request.getParameter("sl_chosen_2"));
    }

    public void createCityListXml(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        Element sl_city_list = Xbuilder.createEl(parent, "sl_city_list", null);
        parent.appendChild(sl_city_list);

        PreparedStatement statement = conn.prepareStatement(
                " SELECT id, title FROM sl_city ORDER BY title ");
        ResultSet result = statement.executeQuery();
        Element sl_city = null;
        while (result.next()){
            sl_city = Xbuilder.createEl(sl_city_list, "sl_city", null);
            sl_city_list.appendChild(sl_city);
            sl_city.setAttribute("title", result.getString("title"));
            sl_city.setAttribute("id", result.getString("id"));
        }
        result.close();
    }

        public void createDetailXml(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        Element sl_detail = Xbuilder.createEl(parent, "sl_detail", null);
        parent.appendChild(sl_detail);

        Contact contact = Contact.findById(conn, new Long(request.getParameter("sl_extra")));
        sl_detail.setAttribute("company_name", contact.getCompanyName());
        sl_detail.setAttribute("city", contact.getCity());
        sl_detail.setAttribute("address_line", contact.getAddressLine());
        sl_detail.setAttribute("phone", contact.getPhone());
        sl_detail.setAttribute("link", contact.getWebLink());
        sl_detail.setAttribute("email", contact.getEmail());
        sl_detail.setAttribute("path", contact.getImageLink());
        sl_detail.setAttribute("zip_code", contact.getZipCode());
    }

    public void createShopListXml(Element parent,
                               RequestContext request, Connection conn)
            throws Exception{
        Element sl_shop_list = Xbuilder.createEl(parent, "sl_shop_list", null);
        parent.appendChild(sl_shop_list);

        Element sl_shop;
        PreparedStatement statement = null;

        if (request.getParameter("sl_criteria").equals("city")){
            sl_shop_list.setAttribute("region", City.findById(conn, new Long(request.getParameter("sl_chosen_1") )).getTitle());
            statement = conn.prepareStatement(
                    " SELECT DISTINCT contact.id, contact.company_name, contact.city, contact.address_line " +
                    " FROM contact, sl_location2zip, sl_zipcode "+
                    " WHERE sl_zipcode.city_id=? AND "+
                    " sl_location2zip.zipcode_id=sl_zipcode.id "+
                    " AND sl_location2zip.contact_id=contact.id ");

            statement.setInt(1, new Integer(request.getParameter("sl_chosen_1")).intValue());
        }
        else if (request.getParameter("sl_criteria").equals("postcode")){
            sl_shop_list.setAttribute("region", request.getParameter("sl_chosen_1")/*+"-"+request.getParameter("sl_chosen_2")*/);
            statement = conn.prepareStatement(
                    " SELECT DISTINCT contact.id, contact.company_name, contact.city, contact.address_line " +
                    " FROM contact, sl_location2zip, sl_zipcode "+
                    " WHERE " +  //((sl_zipcode.min<=? AND sl_zipcode.max>=?)
                    " ((sl_zipcode.min<=? AND sl_zipcode.max>=?) " +
                    //" OR (sl_zipcode.min<=? AND sl_zipcode.max>=?) " +
                    " OR (sl_zipcode.min=? AND sl_zipcode.max IS NULL)) AND" +
                    //" OR (sl_zipcode.min>=? AND sl_zipcode.max<=? )) AND  "+
                    " sl_location2zip.zipcode_id=sl_zipcode.id "+
                    " AND sl_location2zip.contact_id=contact.id ");
            //statement.setInt(1, new Integer(request.getParameter("sl_chosen_1")).intValue());
            //statement.setInt(2, new Integer(request.getParameter("sl_chosen_2")).intValue());
            statement.setInt(1, new Integer(request.getParameter("sl_chosen_1")).intValue());
            statement.setInt(2, new Integer(request.getParameter("sl_chosen_1")).intValue());
            statement.setInt(3, new Integer(request.getParameter("sl_chosen_1")).intValue());
            /*statement.setInt(4, new Integer(request.getParameter("sl_chosen_2")).intValue());
            statement.setInt(5, new Integer(request.getParameter("sl_chosen_1")).intValue());
            statement.setInt(6, new Integer(request.getParameter("sl_chosen_1")).intValue());
            statement.setInt(7, new Integer(request.getParameter("sl_chosen_1")).intValue());
            statement.setInt(8, new Integer(request.getParameter("sl_chosen_2")).intValue());*/
        }
        else{
            throw new Exception("Wrong criteria");
        }
        ResultSet result = statement.executeQuery();
        while(result.next()){
            sl_shop = Xbuilder.createEl(sl_shop_list, "sl_shop", null);
            sl_shop_list.appendChild(sl_shop);
            sl_shop.setAttribute("id", result.getString("id"));
            sl_shop.setAttribute("company_name", result.getString("company_name"));
            sl_shop.setAttribute("city", result.getString("city"));
            sl_shop.setAttribute("address_line", result.getString("address_line"));
        }
        result.close();
    }
}
