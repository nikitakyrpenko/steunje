package com.negeso.framework.io;

import com.negeso.framework.exception.ParseFileException;
import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.log.SystemLogService;
import com.negeso.framework.util.ReflectionUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Producer<T> implements Runnable {
	private final static Logger logger = Logger.getLogger(Producer.class);

	private final Workbook book;
	private final BlockingQueue<T> queue;
	private final Class<T> type;

	private SystemLogService systemLogService;
	private String moduleKey;
	private String serviceType;

	public Producer(Workbook book, BlockingQueue<T> queue, Class<T> type) {
		this.book = book;
		this.queue = queue;
		this.type = type;
	}

	public Producer(Workbook book, BlockingQueue<T> queue, Class<T> type, SystemLogService systemLogService, String moduleKey, String serviceType) {
		this.book = book;
		this.queue = queue;
		this.type = type;

		this.systemLogService = systemLogService;
		this.moduleKey = moduleKey;
		this.serviceType = serviceType;
	}

	@Override
	public void run() {
		Sheet sheetAt = book.getSheetAt(0);
		SystemLogService.rows = sheetAt.getPhysicalNumberOfRows();
		Iterator<Row> iterator = sheetAt.iterator();
		iterator.next();//skip first row
		SystemLogService.progress.incrementAndGet();
//		int i = 0;
		while (iterator.hasNext() ) {
			SystemLogService.buffer.incrementAndGet();
//			i++; if (i > 300) break;
			Row row = iterator.next();
			try {
				T entry = ReflectionUtil.toObject(row, type);
				boolean isAdded = queue.offer(entry, 4, TimeUnit.SECONDS);
				if (!isAdded)
					throw new Exception("The rows are producing but this ones aren't consuming");
			} catch (ParseFileException e){
				SystemLogService.progress.incrementAndGet();
				logger.error(e.getMessage());
				if (this.systemLogService != null)
					systemLogService.addEvent(SystemLogConstants.Event.READ, Thread.currentThread().getName(), e.getMessage(), SystemLogConstants.Result.WARNING, this.moduleKey, this.serviceType);
			}catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				if (this.systemLogService != null)
					systemLogService.addEvent(SystemLogConstants.Event.READ, Thread.currentThread().getName(), e.getMessage(), SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
				break;
			}catch (Exception e){
				logger.error(e.getMessage(), e);
				if (this.systemLogService != null)
					systemLogService.addEvent(SystemLogConstants.Event.READ, Thread.currentThread().getName(), e.getMessage(), SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
				this.queue.clear();
				return;
			}
		}

	}
}
