package com.negeso.module.webshop.service;

import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.entity.OrderItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

@Service
public class GenerateFileService implements ServletContextAware{
	private final static Logger log = Logger.getLogger(GenerateFileService.class);

	@Autowired
	private OrderService orderService;
	private ServletContext servletContext;

	private final String DEFAULT_DIRECTORY_TO_STORE = "/customers/site/media/generated/orders";

	@PostConstruct
	public void init(){
		try {
			this.createDirectoriesIfNeeded(DEFAULT_DIRECTORY_TO_STORE);
		} catch (IOException e) {
			log.error("Failed to create default directory");
		}
	}

	public File generateCSVFromOrder(Integer orderId) throws IOException {
		return this.generateCSVFromOrder(orderId, (String) null);
	}

	public File generateCSVFromOrder(Integer orderId, String outputDirectory) throws IOException {
		Assert.notNull(orderId, "Id of order is null");

		Order order = orderService.findOne(orderId);
		return this.generateCSVFromOrder(order, outputDirectory);
	}

	public File generateCSVFromOrder(Order order) throws IOException {
		return this.generateCSVFromOrder(order, (String) null);
	}

	public File generateCSVFromOrder(Order order, String outputDirectory) throws IOException {
		Assert.notNull(order, "Order is null");
		Assert.hasLength(order.getTransactionId(), "The given order doesn't have transaction identifier");

		if (outputDirectory == null){
			outputDirectory = DEFAULT_DIRECTORY_TO_STORE;
		}

		File directory = new File(servletContext.getRealPath(outputDirectory));
		if (!directory.exists()){
			this.createDirectoriesIfNeeded(outputDirectory);
		}

		File file = this.createUniqueFile(directory, order.getTransactionId(), ".csv");
		return this.generateCSVFromOrder(order, file);
	}

	private File generateCSVFromOrder(Order order, File outputFile) throws IOException {
		Assert.notNull(order, "Order is null");

		if (outputFile == null || !outputFile.exists()) {
			throw new IOException("File doesn't exist");
		}

		StringBuilder sb = new StringBuilder();
		char delimiter = ',';
		String lineSeparator = System.getProperty("line.separator");
		sb.append("Debiteurcode").append(delimiter).append("Artikelcode").append(delimiter).append("Count").append(delimiter).append("Omschrijving").append(lineSeparator);

		String userCode = order.getCustomer().getUserCode();
		for (OrderItem orderItem : order.getOrderItems()) {
			Integer quantity = orderItem.getQuantity();
			String id = orderItem.getProduct().getId();
			sb.append(userCode).append(delimiter).append(id).append(delimiter).append(quantity).append(delimiter).append("").append(lineSeparator);
		}
		if (StringUtils.isNotBlank(order.getComment())){
			sb.append(userCode).append(delimiter).append(5).append(delimiter).append(1).append(delimiter)
					.append("\"").append(order.getComment()).append("\"")
					.append(lineSeparator);
		}
		sb.append(userCode).append(delimiter).append(2).append(delimiter).append(order.getDeliveryPrice()==null || order.getDeliveryPrice().equals(BigDecimal.ZERO) ? 0 : 1).append(delimiter).append("").append(lineSeparator);
		OutputStream os = new FileOutputStream(outputFile);
		os.write(sb.toString().getBytes());
		os.flush();
		os.close();

		return outputFile;
	}

	private void createDirectoriesIfNeeded(String path) throws IOException{
		String directoriesNames[] = path.split("/");
		String wwwPath = "";
		for (String directoryName : directoriesNames) {
			if ("".equals(directoryName)) continue;

			wwwPath = wwwPath + "/" + directoryName;
			File directory = new File(this.servletContext.getRealPath(wwwPath));
			if (!directory.exists() || !directory.isDirectory()){
				if(!directory.mkdir()){
					throw new IOException("Can't create directory");
				}
			}
		}
	}

	private File createUniqueFile(File directory, String fileName, String fileExtension) throws IOException{
		Assert.isTrue(directory != null && directory.exists() && directory.isDirectory());
		Assert.hasLength(fileName);
		Assert.hasLength(fileExtension);
		Assert.isTrue(fileExtension.startsWith(".") && fileExtension.length() > 1);

		String filePathWithoutExtension = directory.getAbsolutePath() + "/" + fileName;
		int i = 0;
		File file = new File(filePathWithoutExtension + fileExtension);
		while (file.exists()){
			i++;
			file = new File(filePathWithoutExtension + "(" + i + ")" + fileExtension);
		}

		file.createNewFile();

		return file;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
