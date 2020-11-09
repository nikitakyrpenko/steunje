package com.negeso.module.job;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 23, 2005
 */

public class State {
	private String mode = GENERAL_MODE;
	private Long departmentId = null;
	private Long regionId = null;
	private Long vacancyId = null;

	public static String GENERAL_MODE = "general";
	public static String DEPARTMENT_VACANCIES_MODE = "department_vacancies";
	public static String REGION_VACANCIES_MODE = "region_vacancies";
	public static String POST_APPLICATION_MODE = "post_application";
	public static String SAVE_APPLICATION_MODE = "save_application";
	public static String SHOW_ALL_VACANCIES_MODE = "show_all_vacancies";
	public static String VACANCY_DETAILS_MODE = "vacancy_details";
	public static String POST_VAC_APPLICATION_MODE = "post_vac_application";
	public static String SAVE_VAC_APPLICATION_MODE = "save_vac_application";
	public static String POST_GEN_APPLICATION_MODE = "post_gen_application";
	public static String SAVE_GEN_APPLICATION_MODE = "save_gen_application";
	public static String SEARCH_MODE = "search";
	public static String DO_SEARCH_MODE = "do_search";
	public static String SEARCH_RESULT_MODE = "search_result";
	public static String POST_SEARCH_RESULT_MODE = "post_search_result";
	public static String SAVE_SEARCH_RESULT_MODE = "save_search_result";
	public static String ERROR_MODE = "error";

	public State(RequestContext request) {
		String m = request.getParameter("mode");
		
		if (DEPARTMENT_VACANCIES_MODE.equals(m)) {
			mode = DEPARTMENT_VACANCIES_MODE;
		} else if (POST_APPLICATION_MODE.equals(m)) {
			mode = POST_APPLICATION_MODE;
		} else if (SAVE_APPLICATION_MODE.equals(m)) {
			mode = SAVE_APPLICATION_MODE;
		} else if (SHOW_ALL_VACANCIES_MODE.equals(m)) {
			mode = SHOW_ALL_VACANCIES_MODE;
		} else if (VACANCY_DETAILS_MODE.equals(m)) {
			mode = VACANCY_DETAILS_MODE;
		} else if (POST_VAC_APPLICATION_MODE.equals(m)) {
			mode = POST_VAC_APPLICATION_MODE;
		} else if (SAVE_VAC_APPLICATION_MODE.equals(m)) {
			mode = SAVE_VAC_APPLICATION_MODE;
		} else if (POST_GEN_APPLICATION_MODE.equals(m)) {
			mode = POST_GEN_APPLICATION_MODE;
		} else if (SAVE_GEN_APPLICATION_MODE.equals(m)) {
			mode = SAVE_GEN_APPLICATION_MODE;
		} else if (SEARCH_MODE.equals(m)) {
			mode = SEARCH_MODE;
		} else if (DO_SEARCH_MODE.equals(m)) {
			mode = DO_SEARCH_MODE;
		} else if (SEARCH_RESULT_MODE.equals(m)) {
			mode = SEARCH_RESULT_MODE;
		} else if (POST_SEARCH_RESULT_MODE.equals(m)) {
			mode = POST_SEARCH_RESULT_MODE;
		} else if (SAVE_SEARCH_RESULT_MODE.equals(m)) {
			mode = SAVE_SEARCH_RESULT_MODE;
		} else if(REGION_VACANCIES_MODE.equals(m)) {
			mode = REGION_VACANCIES_MODE;
		}

		departmentId = request.getLong("dep_id");
		vacancyId = request.getLong("vac_id");
		regionId = request.getLong("reg_id");

	}

	public String getMode() {
		return mode;
	}

	public void setMode(String newMode) {
		mode = newMode;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public Long getVacancyId() {
		return vacancyId;
	}

	public Long getRegionId() {
		return regionId;
	}
}
