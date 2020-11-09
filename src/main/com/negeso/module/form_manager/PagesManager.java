package com.negeso.module.form_manager;

import java.util.ArrayList;
import java.util.List;

public class PagesManager{
	private int current;
	private int totalRecords;
	private int recordsPerPage;

	public PagesManager(int current, int totalRecords){
		this.current = current;
		this.totalRecords = totalRecords;
	}

	public PagesManager(int current, int totalRecords, int recordsPerPage){
		this(current, totalRecords);
		this.recordsPerPage = recordsPerPage;
	}

	public List<Integer> getTenLinks(){
		final int linksPerPage = 10;
		final int totalPages = (totalRecords + recordsPerPage - 1) / recordsPerPage;

		List<Integer> links = new ArrayList<Integer>();
		int firsPage = this.current - linksPerPage / 2 + 1;
		int lastPage = this.current + linksPerPage / 2;

		if(firsPage <= 0){
			firsPage = 1;
			lastPage = linksPerPage > totalPages ? totalPages : linksPerPage;
		} else if(lastPage > totalPages){
			lastPage = totalPages;
			firsPage = totalPages - linksPerPage + 1 > 0 ? totalPages - linksPerPage + 1 : 1;
		}

		for(int i = 0; i < linksPerPage; i++){
			links.add(firsPage++);
			if(firsPage > lastPage){
				break;
			}
		}

		return links;
	}

	public int getCurrent(){
		return current;
	}
}