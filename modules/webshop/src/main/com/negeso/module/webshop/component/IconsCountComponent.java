package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.page.PageComponent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@Component("webshop-icons_count")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IconsCountComponent extends AbstractPageComponent {
	private final static Logger logger = Logger.getLogger(IconsCountComponent.class);

	private final PageComponent cartDetailsComponent;

	@Autowired
	public IconsCountComponent(@Qualifier("webshop-cart_details_component") PageComponent cartDetailsComponent) {
		this.cartDetailsComponent = cartDetailsComponent;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		final String query = "" +
				" SELECT count(DISTINCT cart) as cart_count, count(DISTINCT wish) as wish_count FROM user_list u" +
				" LEFT JOIN ws_cart_item cart on cart.cart_owner_id = u.login" +
				" LEFT JOIN ws_wishlist wish on wish.login = u.login" +
				" WHERE u.login = ?;";
		Element root = Xbuilder.createEl(document, "icons_count", null);
		Element cartDetailsElement = this.appendCartDetailsElement(document, request, parameters);
		if (cartDetailsElement != null){
			root.appendChild(cartDetailsElement);
		}
		User user = request.getSession().getUser();
		if (user == null || user.getLogin() == null)
			return root;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = DBHelper.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, user.getLogin());
			rs = statement.executeQuery();
			if (rs.next()) {
				root.setAttribute("cart", String.valueOf(rs.getInt("cart_count")));
				root.setAttribute("wish", String.valueOf(rs.getInt("wish_count")));
				
			}
		} catch (Exception e) {
			logger.error("Unexpected error", e);
		} finally {
			DBHelper.close(rs, statement, connection);
		}

		return root;
	}

	private Element appendCartDetailsElement(Document document, RequestContext request, Map parameters) {
		try {
			return cartDetailsComponent.getElement(document, request, parameters);
		}catch (Exception e){
			logger.error("error", e);
			return null;
		}
	}
}
