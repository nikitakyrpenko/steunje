package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

@Service
public class GeneratePdfService implements ServletContextAware {

	private StreamSource transformSource;
	private FopFactory fopFactory;

	private ServletContext servletContext;

	@PostConstruct
	public void onInit() throws IOException, SAXException {
		String realPath = servletContext.getRealPath("/WEB-INF/modules/webshop/template/fop.xconf");
		this.fopFactory = FopFactory.newInstance(new File(realPath));
//		File xslFile = new File(Env.getRealPath("/WEB-INF/modules/webshop/template/notification.xsl"));
//		this.transformSource = new StreamSource(xslFile);
	}


	public void generatePdf() throws Exception {
		StreamSource source = new StreamSource(new File(Env.getRealPath("/WEB-INF/modules/webshop/template/order.xml")));
		this.generatePdf(source, "/WEB-INF/generated/pdf/test.pdf");
	}

	public void generatePdf(Source source, String outputPath) throws Exception {
		File xslFile = new File(Env.getRealPath("/WEB-INF/modules/webshop/template/notification.xsl"));
		this.transformSource = new StreamSource(xslFile); //todo make them global after test

		FOUserAgent foUserAgent = this.fopFactory.newFOUserAgent();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(transformSource);

			Fop fop;
			try {
				fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);
				Result res = new SAXResult(fop.getDefaultHandler());
				try {
					transformer.transform(source, res);
					File pdffile = new File(Env.getRealPath(outputPath));
					OutputStream out = new FileOutputStream(pdffile);
					out = new BufferedOutputStream(out);
					FileOutputStream str = new FileOutputStream(pdffile);
					str.write(outStream.toByteArray());
					str.close();
					out.close();
				} catch (TransformerException e) {
					throw e;
				}
			} catch (FOPException e) {
				throw e;
			}
		} catch (TransformerConfigurationException e) {
			throw e;
		} catch (TransformerFactoryConfigurationError e) {
			throw e;
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
