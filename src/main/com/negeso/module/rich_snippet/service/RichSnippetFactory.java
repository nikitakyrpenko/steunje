package com.negeso.module.rich_snippet.service;

import com.negeso.framework.domain.CriticalException;
import com.negeso.module.rich_snippet.bo.AggregateReview;
import com.negeso.module.rich_snippet.bo.ProductRichSnippet;
import com.negeso.module.rich_snippet.bo.Review;
import com.negeso.module.rich_snippet.bo.RichSnippet;
import com.negeso.module.rich_snippet.bo.RichSnippet.RichSnippetType;
import com.negeso.module.rich_snippet.bo.Video;

public class RichSnippetFactory {

	public static RichSnippet getObject(RichSnippetType richSnippetType) {
		switch (richSnippetType) {
		case review:
			return new Review();
		case product:
			return new ProductRichSnippet();
		case video:
			return new Video();
		case aggregate_review:
			return new AggregateReview();
		}
		throw new CriticalException("Unsupported type: " + richSnippetType.toString());
	}
}
