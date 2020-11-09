package com.negeso.module.rich_snippet.service;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.rich_snippet.bo.RichSnippet;
import com.negeso.module.rich_snippet.bo.RichSnippet.RichSnippetType;
import com.negeso.module.rich_snippet.dao.RichSnippetDao;

public class RichSnippetService {

	private RichSnippetDao richSnippetDao;

	@Transactional
	public void createOrUpdate(RichSnippet richSnippet) {
		richSnippetDao.createOrUpdate(richSnippet);
	}

	public List<RichSnippet> list(RichSnippetType discriminator) {
		return richSnippetDao.readByCriteria(Restrictions.eq("discrim",
				discriminator.toString()));
	}
	
	public List<RichSnippet> list() {
		return richSnippetDao.readAll();
	}

	public RichSnippet findById(Long id) {
		return richSnippetDao.read(id);
	}

	@Transactional
	public void delete(RichSnippet richSnippet) {
		richSnippetDao.delete(richSnippet);
	}

	public RichSnippetDao getRichSnippetDao() {
		return richSnippetDao;
	}

	public void setRichSnippetDao(RichSnippetDao richSnippetDao) {
		this.richSnippetDao = richSnippetDao;
	}
}
