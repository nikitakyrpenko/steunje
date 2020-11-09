package com.negeso.module.search;

import com.negeso.framework.ApplicationContextProvider;
import com.negeso.framework.dao.Callback;
import com.negeso.framework.dao.SessionTemplate;

public class BuildIndexExecutor {

	public static void build() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SessionTemplate template = (SessionTemplate) ApplicationContextProvider.getApplicationContext().getBean("sessionTemplate");
				template.execute(new Callback() {
					@Override
					public void process() {
						SearchProducer.buildIndex();
					}
				});

			}
		}).start();
	}
}
