package com.negeso.module.baten.service;

import com.negeso.module.baten.dao.ArticleInfoDao;
import com.negeso.module.baten.entity.ArticleInfo;

import java.util.List;

public class ArticleInfoService {
	private ArticleInfoDao articleInfoDao;

	public ArticleInfo read(Long id){
		return articleInfoDao.read(id);
	}

	public List<ArticleInfo> readAll(){
		return articleInfoDao.readAll();
	}

	public void create(ArticleInfo newInstance){
		articleInfoDao.create(newInstance);
	}

	public void saveOrUpdate(ArticleInfo articleInfo){
		articleInfoDao.saveOrUpdate(articleInfo);
	}

	public void delete(ArticleInfo articleInfo){
		articleInfoDao.delete(articleInfo);
	}

	public void setArticleInfoDao(ArticleInfoDao articleInfoDao) {
		this.articleInfoDao = articleInfoDao;
	}
}
