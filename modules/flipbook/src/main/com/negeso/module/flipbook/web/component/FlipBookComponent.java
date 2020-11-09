package com.negeso.module.flipbook.web.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.flipbook.AbstractFilePagesReader;
import com.negeso.module.flipbook.FilePagesReaderFactory;
import com.negeso.module.flipbook.FlipBookModule;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FlipBookComponent implements PageComponent {
	
	private static Logger logger = Logger.getLogger(FlipBookComponent.class);
	
	private static final String COMPONENT_NAME = "flip-book-component";
	
	private static Map<String, PdfFileData> cache = new HashMap<String, PdfFileData>();

	@Override
	public Element getElement(Document document, RequestContext request,
			Map parameters) {
		Element component = Xbuilder.createEl(document, COMPONENT_NAME, null);
		String filePath = request.getString(FlipBookModule.FILE_PATH, null);
		if (StringUtils.isNotBlank(filePath)) {
			PdfFileData pdfFileData = cache.get(filePath);
			if (pdfFileData != null) {
				setAttributes(component, filePath, pdfFileData);
			}else {
				AbstractFilePagesReader reader = null;
				try {
					reader = FilePagesReaderFactory.getReader(FlipBookModule.MODULE_MEDIA_FOLER + filePath);
					int pages = reader.getPagesCount();
					pdfFileData = new PdfFileData(pages, reader.getWidth(), reader.getHeight());
					cache.put(filePath, pdfFileData);
					setAttributes(component, filePath, pdfFileData);
				} catch (Exception e) {
					logger.error(StringUtils.EMPTY, e);
				} finally {
					if (reader != null) {
						reader.finish();
					}
				}
			}
		}
		return component;
	}
	
	public static void resetCache() {
		cache.clear();
	}

	private void setAttributes(Element component, String filePath, PdfFileData data) {
		Xbuilder.setAttr(component, "pages-count", data.pagesCount);
		Xbuilder.setAttr(component, "width", data.width);
		Xbuilder.setAttr(component, "height", data.height);
		Xbuilder.setAttr(component, FlipBookModule.FILE_PATH, filePath);
		for (int i = 1; i <= data.pagesCount; i++) {
			Element page = Xbuilder.addEl(component, "page", null);
			Xbuilder.setAttr(page, "number", i);
		}
	}

}

class PdfFileData {
	int pagesCount;
	int width;
	int height;
	public PdfFileData(int pagesCount, int width, int height) {
		super();
		this.pagesCount = pagesCount;
		this.width = width;
		this.height = height;
	}
}
