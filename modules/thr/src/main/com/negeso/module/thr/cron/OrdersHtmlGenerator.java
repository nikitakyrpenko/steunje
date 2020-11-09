package com.negeso.module.thr.cron;

import javax.xml.transform.Templates;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.XmlHelper;

public class OrdersHtmlGenerator {
	
	private static final String THEMPLATE_XSL = "/template/thr_orders.xsl";
	
	public static String buildHtml(Element el) {
		Templates template = Env.getSite().getXslTemplates(THEMPLATE_XSL);
		String shopBody = XmlHelper.transformToString(
				el.getOwnerDocument(), 
			    template, 
			    "html"
		);
		return shopBody;
	} 
}
