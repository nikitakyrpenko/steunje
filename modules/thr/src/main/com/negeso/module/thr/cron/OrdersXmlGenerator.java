package com.negeso.module.thr.cron;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.module.thr.bo.ThrOrder;
import com.negeso.module.thr.utils.CurrencyFormatter;

public class OrdersXmlGenerator {
	
	public static Element buildXml(List<ThrOrder> list) {
		Element element = Xbuilder.createTopEl("orders");
		element.setAttribute("host", "http://" + SiteUrlCache.getMain().getUrl() + "/");
		Double totalPerOrders = 0d;
		for (ThrOrder thrOrder : list) {
			Xbuilder.addBeanJAXB(element, thrOrder);
			totalPerOrders += thrOrder.getTotal();
		}
		Xbuilder.setAttr(element, "total", CurrencyFormatter.format(totalPerOrders));
		return element;
	}

	public static String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
