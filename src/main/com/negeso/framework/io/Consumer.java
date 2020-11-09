package com.negeso.framework.io;

import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.log.SystemLogService;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer<T extends PrimaryKey> implements Runnable{
	private final static Logger logger = Logger.getLogger(Consumer.class);

	private final BlockingQueue<T> queue;
	private final SessionFactory sessionFactory;
	private final Thread producer;
	private boolean removeOld = false;

	private SystemLogService systemLogService;
	private String moduleKey;
	private String serviceType;

	public Consumer(BlockingQueue<T> queue, SessionFactory sessionFactory, Thread producer){
		this.queue = queue;
		this.sessionFactory = sessionFactory;
		this.producer = producer;
	}

	public Consumer(BlockingQueue<T> queue, SessionFactory sessionFactory, Thread producer, SystemLogService systemLogService, String moduleKey, String serviceType) {
		this.queue = queue;
		this.sessionFactory = sessionFactory;
		this.producer = producer;

		this.systemLogService = systemLogService;
		this.moduleKey = moduleKey;
		this.serviceType = serviceType;
	}

	public Consumer(BlockingQueue<T> queue, SessionFactory sessionFactory, Thread producer, boolean removeOld, SystemLogService systemLogService, String moduleKey, String serviceType) {
		this.queue = queue;
		this.sessionFactory = sessionFactory;
		this.producer = producer;
		this.removeOld = removeOld;

		this.systemLogService = systemLogService;
		this.moduleKey = moduleKey;
		this.serviceType = serviceType;
	}

	@Override
	public void run() {
		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			int i = 0;
			while (!queue.isEmpty() || producer.isAlive()){
				SystemLogService.progress.incrementAndGet();
				i++;
				T poll = queue.poll(4, TimeUnit.SECONDS);
				if (removeOld){
					Query query = session.createQuery("delete from " + poll.getClass().getSimpleName());
					query.executeUpdate();
					removeOld = false;
				}
				if (poll != null){
					try {
						int size = poll.getJoined().length;
						for (int j = 0; j < size; j++){
							Object related = session.get(poll.getJoined()[j].getClass(), poll.getJoinedPrimaryKey()[j]);

							if (related != null)
								poll.setJoinedObject(related, j);
							else{
								Object primary;
								primary = poll.getJoined()[j];

								if (primary instanceof PrimaryKey){
									while (primary != null){
										Object sessionPrimaryOfPrimary = null;
										Object primaryOfPrimary = ((PrimaryKey) primary).getJoined()[0];
										if (primaryOfPrimary != null)
											sessionPrimaryOfPrimary = session.get(Hibernate.getClass(primaryOfPrimary), ((PrimaryKey) primary).getJoinedPrimaryKey()[0]);

										if (sessionPrimaryOfPrimary == null){
											session.save(primary);
										}else {
											((PrimaryKey) primary).setJoinedObject(sessionPrimaryOfPrimary, 0);
											session.save(primary);
											break;
										}

										primary = primaryOfPrimary;
									}
								}else {
									session.save(primary);
								}
							}
						}

						session.saveOrUpdate(poll);
						if (i%20 == 0){
							session.flush();
							session.clear();
						}
					}catch (NonUniqueObjectException ex){
						String msg = String.format("Object with the same identifier has been parsed already near line %d. %s", i + 1, Arrays.toString(poll.getJoinedPrimaryKey()));
						logger.error(msg);
						if (this.systemLogService != null)
							systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), msg, SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
					}catch (ConstraintViolationException ex){
						String msg = String.format("Object with the same identifier has been parsed already near line %d. %s", i + 1, Arrays.toString(poll.getJoinedPrimaryKey()));
						logger.error(msg);
						if (this.systemLogService != null)
							systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), msg, SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
					}
				}
			}
			transaction.commit();

		}catch (Exception e){
			if (transaction != null)
				transaction.rollback();
			if (this.systemLogService != null)
				systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), e.getMessage() == null ? "NPE": e.getMessage(), SystemLogConstants.Result.FATAL, this.moduleKey, this.serviceType);
		}finally {
			if (session != null)
				session.close();
		}
	}
}
