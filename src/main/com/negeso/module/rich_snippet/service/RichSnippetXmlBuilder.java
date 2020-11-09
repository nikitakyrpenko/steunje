package com.negeso.module.rich_snippet.service;

import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.rich_snippet.IRichSnippetContainer;
import com.negeso.module.rich_snippet.bo.RichSnippet;

public class RichSnippetXmlBuilder {
	public static void buildXml(Element parent, IRichSnippetContainer richSnippetContainer)  {
		if (richSnippetContainer != null &&  !(richSnippetContainer.getRichSnippets().isEmpty()) ) {
			Element richSnippetsEl = Xbuilder.addEl(parent, "rich-snippets", null);
			for (RichSnippet richSnippet : richSnippetContainer.getRichSnippets()) {
				Element el = Xbuilder.addBeanJAXB(richSnippetsEl, richSnippet);
				richSnippetsEl.appendChild(el);
			}
		}
	}
}
