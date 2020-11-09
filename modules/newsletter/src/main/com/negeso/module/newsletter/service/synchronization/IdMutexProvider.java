/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service.synchronization;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class IdMutexProvider {

	private final Map<Mutex, WeakReference<Mutex>> mutexMap = new WeakHashMap<IdMutexProvider.Mutex, WeakReference<Mutex>>();

	/** Get a mutex object for the given (non-null) id. */
	public Mutex getMutex(String id) {
		if (id == null) {
			throw new NullPointerException();
		}

		Mutex key = new MutexImpl(id);
		synchronized (mutexMap) {
			WeakReference<Mutex> ref = mutexMap.get(key);
			if (ref == null) {
				mutexMap.put(key, new WeakReference<IdMutexProvider.Mutex>(key));
				return key;
			}
			Mutex mutex = (Mutex) ref.get();
			if (mutex == null) {
				mutexMap.put(key, new WeakReference<IdMutexProvider.Mutex>(key));
				return key;
			}
			return mutex;
		}
	}

	/** Get the number of mutex objects being held */
	public int getMutexCount() {
		synchronized (mutexMap) {
			return mutexMap.size();
		}
	}

	public static interface Mutex {
	}

	private static class MutexImpl implements Mutex {
		private final String id;

		protected MutexImpl(String id) {
			this.id = id;
		}

		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (this.getClass() == o.getClass()) {
				return this.id.equals(o.toString());
			}
			return false;
		}

		public int hashCode() {
			return id.hashCode();
		}

		public String toString() {
			return id;
		}
	}

}
