package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.service.ProductService;
import com.negeso.module.webshop.service.ProductXmlHelperService;
import com.negeso.module.webshop.util.ProductXmlHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

@Component("webshop-sales_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SalesComponent extends AbstractPageComponent {
	private final static Logger logger = Logger.getLogger(SalesComponent.class);

	private final ProductService productService;
	private final ProductXmlHelperService productXmlHelperService;

	@Autowired
	public SalesComponent(ProductService productService, ProductXmlHelperService productXmlHelperService) {
		this.productService = productService;
		this.productXmlHelperService = productXmlHelperService;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element root = Xbuilder.createEl(document, "sales", null);

		try {
			List<Product> products = this.productService.listSales();
			if (products == null || products.isEmpty())
				return root;

			productXmlHelperService.appendFromCollection(root, products, null);
		} catch (Exception e) {
			logger.error("Failed to build component", e);
			return root;
		}
		return root;
	}
}
