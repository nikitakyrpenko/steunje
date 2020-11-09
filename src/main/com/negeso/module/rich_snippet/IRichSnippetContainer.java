package com.negeso.module.rich_snippet;

import java.util.Set;

import com.negeso.framework.dao.Entity;
import com.negeso.module.rich_snippet.bo.RichSnippet;

public interface IRichSnippetContainer extends Entity{
	public Set<RichSnippet> getRichSnippets();
}
