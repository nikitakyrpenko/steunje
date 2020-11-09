package com.negeso.framework.io;

import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.log.SystemLogService;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConsumerRC2<T> implements Runnable{
	private final static Logger logger = Logger.getLogger(ConsumerRC2.class);

	private final BlockingQueue<T> queue;
	private final SessionFactory sessionFactory;
	private final Thread producer;

	private SystemLogService systemLogService;
	private String moduleKey;
	private String serviceType;

	public ConsumerRC2(BlockingQueue<T> queue, SessionFactory sessionFactory, Thread producer){
		this.queue = queue;
		this.sessionFactory = sessionFactory;
		this.producer = producer;
	}

	public ConsumerRC2(BlockingQueue<T> queue, SessionFactory sessionFactory, Thread producer, SystemLogService systemLogService, String moduleKey, String serviceType) {
		this.queue = queue;
		this.sessionFactory = sessionFactory;
		this.producer = producer;

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
				if (poll != null){
					try {
						this.saveDependencyIfNeeded(session, poll);
						session.merge(poll);
						if (i%20 == 0){
							session.flush();
							session.clear();
						}
					}catch (NonUniqueObjectException ex){
						String msg = String.format("Object with the same identifier has been parsed already near line %d.", i + 1);
						logger.error(msg);
						if (this.systemLogService != null)
							systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), msg, SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
					}catch (ConstraintViolationException ex){
						String msg = String.format("Object with the same identifier has been parsed already near line %d. ", i + 1);
						logger.error(msg + poll, ex);
						if (this.systemLogService != null)
							systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), msg, SystemLogConstants.Result.ERROR, this.moduleKey, this.serviceType);
					}
				}
			}
			transaction.commit();

		}catch (Exception e){
			logger.error("crit", e);
			if (transaction != null)
				transaction.rollback();
			if (this.systemLogService != null)
				systemLogService.addEvent(SystemLogConstants.Event.CREATE, Thread.currentThread().getName(), e.getMessage(), SystemLogConstants.Result.FATAL, this.moduleKey, this.serviceType);
		}finally {
			if (session != null)
				session.close();
		}
	}

	private void saveDependencyIfNeeded(Session session, T poll) throws NonUniqueObjectException {
		Field[] declaredFields = poll.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(HibernateDependency.class)){
				Behavior behavior = field.getAnnotation(HibernateDependency.class).behavior();
				if (behavior == Behavior.PERSIST){
					try {
						Object property = new PropertyDescriptor(field.getName(), poll.getClass()).getReadMethod().invoke(poll);
						if (property != null)
							session.persist(field.getClass().getSimpleName(), property);
					} catch (Exception e) {
						logger.error("Unable to save dependency");
					}
				}else if (behavior == Behavior.MERGE){
					try {
						Object property = new PropertyDescriptor(field.getName(), poll.getClass()).getReadMethod().invoke(poll);
						if (property != null){
							Class<?> depClass = property.getClass();
							if (!field.getAnnotation(HibernateDependency.class).id().equals("")){
								String id = (String) new PropertyDescriptor(field.getAnnotation(HibernateDependency.class).id(), depClass).getReadMethod().invoke(property);
								Object o = session.get(depClass, id);
								if (o != null){
									BeanUtils.copyProperties(property, o, field.getAnnotation(HibernateDependency.class).exclude());
									property = o;
									new PropertyDescriptor(field.getName(), poll.getClass()).getWriteMethod().invoke(poll, property);
								}
							}

							session.merge(field.getClass().getSimpleName(), property);
						}
					} catch (Exception e) {
						logger.error("Unable to save dependency");
					}
				}
			}
		}
	}
}
