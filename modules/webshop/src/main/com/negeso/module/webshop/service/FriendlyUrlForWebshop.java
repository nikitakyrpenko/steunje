package com.negeso.module.webshop.service;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlDescriptor;
import com.negeso.framework.friendly_url.FriendlyUrlHandler;
import com.negeso.framework.friendly_url.UrlEntityType;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendlyUrlForWebshop {

	public FriendlyUrlForWebshop() {
		List<FriendlyUrlDescriptor> list = new ArrayList<FriendlyUrlDescriptor>(3);
		list.add(new FriendlyUrlDescriptor("/**/*.htm*", "/**/*.xml", "pmProductId", "webshop", "webshop-main_component", UrlEntityType.PRODUCT, true, true, true));
		list.add(new FriendlyUrlDescriptor("/*.htm*", "-xml/**/", "pmCatId", "webshop", "webshop-main_component", UrlEntityType.PRODUCT_CATEGORY, true, false, false));
		list.add(new FriendlyUrlDescriptor("/**/*", null, "pmProductId", "webshop", "webshop-main_component", UrlEntityType.PRODUCT, true, true, true));
		list.add(new FriendlyUrlDescriptor("/", "-xml/", "webshop", "webshop-main_component"));
		FriendlyUrlHandler.put("WS_MODULE_ROOT_FRIENDLY_URL_PART", list);
	}

	public void insertAllProductUrls() {
		String query = "" +
				"insert into friendly_url(entity_id, lang_id, entity_type_id, link)" +
				"  SELECT p.order_code, 1, 1," +
				"    concat(" +
				"        substr(c.name, 0, length(c.name) - 1)," +
				"        '/'," +
				"        p.order_code" +
				"    )" +
				"  from ws_products p" +
				"    left join ws_category c on c.name = p.category_name" +
				"    left join friendly_url url on url.entity_id = p.order_code" +
				"  where url.entity_id is null;";

		this.execute(query);
	}

	public void insertAllCategoryUrls() {
		String query = "" +
				"insert into friendly_url(entity_id, lang_id, entity_type_id, link)" +
				"   select c.name, 1, 0," +
				"       substr(c.name, 0, length(c.name) - 1) from ws_category c" +
				"   left join friendly_url url on url.entity_id = c.name" +
				"   where url.entity_id is null and c.visible = TRUE;";

		this.execute(query);
	}

	public void deleteAllUrlsByType(int typeId){
		String query = "delete from friendly_url where entity_type_id = " + typeId;
		this.execute(query);
	}

	public void deleteAllUrlsByType(UrlEntityType type){
		this.deleteAllUrlsByType(type.ordinal());
	}

	private void execute(String query){
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = DBHelper.getConnection();
			stmt = conn.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.close(conn);
			DBHelper.close(stmt);
		}
	}
}
