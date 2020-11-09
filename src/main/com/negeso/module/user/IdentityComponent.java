package com.negeso.module.user;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageComponent;

/**
 * Returns user info as <b>&lt;identity name="some user"/&gt;</b>
 * If the user is not uathenticated, attribute <b>name</b> is omitted
 * 
 * @author stanislav Demchneko
 */
public class IdentityComponent implements PageComponent {

	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element elidentity = Xbuilder.createEl(document, "identity", null);
		User user = request.getSession().getUser();
		if (user != null) Xbuilder.setAttr(elidentity, "name", user.getName());
		return elidentity;
	}

}
