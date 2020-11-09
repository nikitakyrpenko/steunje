package com.negeso.module.webshop.search;

import com.negeso.module.search.SearchEntryProducer;

public abstract class AbstractSearchEntryProducer implements SearchEntryProducer {

	protected String separateBySpaces(String... args) {
		StringBuilder sb = new StringBuilder();

		for (String arg : args) {
			if (arg != null)
				sb.append(arg).append(' ');
		}

		return sb.toString();
	}
}
