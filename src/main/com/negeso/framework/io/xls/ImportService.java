package com.negeso.framework.io.xls;

import com.negeso.framework.io.Consumer;
import com.negeso.framework.io.ConsumerRC2;
import com.negeso.framework.io.PrimaryKey;
import com.negeso.framework.io.Producer;
import com.negeso.framework.log.SystemLogService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ImportService<T> {

	private SystemLogService systemLogService;
	private String moduleKey;
	private String serviceType;

	public ImportService(){}

	public ImportService(SystemLogService systemLogService, String moduleKey, String serviceType) {
		this.systemLogService = systemLogService;
		this.moduleKey = moduleKey;
		this.serviceType = serviceType;
	}

	public T importFromFile(String fileName, XlsCallback<T> callback) throws IOException {
		FileInputStream stream = null;
		T result = null;
		try {
			stream = new FileInputStream(new File(fileName));
			XSSFWorkbook book = new XSSFWorkbook(stream);
			result = callback.process(book);
		}catch (IOException ex){
			ex.printStackTrace();
		}finally {
			if (stream != null)
				stream.close();
		}

		return result;
	}

	public T importFromFile(Workbook book, XlsCallback<T> callback) throws IOException {

		return callback.process(book);
	}

	public Workbook resolveFile(MultipartFile file) throws IOException{
		InputStream stream = null;
		Workbook result = null;
		try {
			stream = file.getInputStream();
			result = new XSSFWorkbook(stream);
		}catch (IOException ex){
			ex.printStackTrace();
		}finally {
			if (stream != null)
				stream.close();
		}

		return result;
	}

	public <C extends PrimaryKey> Thread[] export(Workbook book, Class<C> sd, SessionFactory sessionFactory){
		return this.export(book, sd, sessionFactory, false);
	}

	public <C extends PrimaryKey> Thread[] export(Workbook book, Class<C> sd, SessionFactory sessionFactory, boolean removeOld){
		int randomNumber = new Random().nextInt(Integer.MAX_VALUE);
		BlockingQueue<C> queue = new ArrayBlockingQueue<C>(80);
		Thread producer = new Thread(new Producer<C>(book, queue, sd, this.systemLogService, this.moduleKey, this.serviceType));
		producer.setName(producer.getName()+randomNumber);
		Thread consumer
				= sd.getSimpleName().equals("Product") || sd.getSimpleName().equals("Customer")
				? new Thread(new ConsumerRC2<C>(queue, sessionFactory, producer, this.systemLogService, this.moduleKey, this.serviceType))
				: new Thread(new Consumer<C>(queue, sessionFactory, producer, removeOld, this.systemLogService, this.moduleKey, this.serviceType))
				;
		consumer.setName(consumer.getName()+randomNumber);
		producer.start();
		consumer.start();

		return new Thread[]{producer, consumer};
	}
}
