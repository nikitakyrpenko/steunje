package com.negeso.module.job.component;

import com.negeso.module.job.State;
import com.negeso.module.job.ApplicationSaverUtil;
import com.negeso.module.job.Configuration;
import com.negeso.module.job.generator.SearchState;
import com.negeso.module.job.domain.Department;
import com.negeso.module.job.domain.Region;
import com.negeso.module.job.domain.Vacancy;
import com.negeso.module.job.generator.DepartmentXmlBuilder;
import com.negeso.module.job.generator.VacancyXmlBuilder;
import com.negeso.module.job.generator.ApplicationXmlBuilder;
import com.negeso.module.job.service.RegionService;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.AbstractPageComponent;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.Map;
import java.sql.Connection;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 23, 2005
 */

public class JobModuleComponent extends AbstractPageComponent{
    private static Logger logger = Logger.getLogger( JobModuleComponent.class );
    private State state = null;

    public Element getElement(Document document, RequestContext request, Map parameters) {
		Element job_module_component = null;
        Connection conn = null;
        try{
            job_module_component = Xbuilder.createEl(document, "job_module_component", null);
            conn = DBHelper.getConnection();
            state = new State(request);
            job_module_component.setAttribute("start_page", "");

			if (request.getParameter("mode")==null || request.getParameter("mode").equals(""))
            {
                String start = getStringParameter(parameters, "start", "DepartmentsView");
                if (start.equals(Configuration.START_PAGE_DEPARTMENTS_VIEW)){
                    state.setMode(State.GENERAL_MODE);
                }
                else if (start.equals(Configuration.START_PAGE_ALL_VACANCIES_VIEW)){
                    job_module_component.setAttribute("start_page", Configuration.START_PAGE_ALL_VACANCIES_VIEW);
                    state.setMode(State.SHOW_ALL_VACANCIES_MODE);
                }
                else if (start.equals(Configuration.START_PAGE_GENERAL_APPLICATION_FORM)){
                    job_module_component.setAttribute("start_page", Configuration.START_PAGE_GENERAL_APPLICATION_FORM);
                    state.setMode(State.POST_GEN_APPLICATION_MODE);
                }
            }
            if (state.getMode().equals(State.GENERAL_MODE)){
                renderGeneralMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.DEPARTMENT_VACANCIES_MODE)){
                renderDepartmentVacanciesMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.REGION_VACANCIES_MODE)){
                renderRegionVacanciesMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.POST_APPLICATION_MODE)){
                renderPostApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SAVE_APPLICATION_MODE)){
                renderSaveApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SHOW_ALL_VACANCIES_MODE)){
                renderShowAllVacanciesMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.VACANCY_DETAILS_MODE)){
                renderVacancyDetailsMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.POST_VAC_APPLICATION_MODE)){
                renderPostVacApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SAVE_VAC_APPLICATION_MODE)){
                renderSaveVacApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.POST_GEN_APPLICATION_MODE)){
                renderPostGenApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SAVE_GEN_APPLICATION_MODE)){
                renderSaveGenApplicationMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SEARCH_MODE)){
                renderSearchMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.DO_SEARCH_MODE)){
                renderDoSearchMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SEARCH_RESULT_MODE)){
                renderSearchResultMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.POST_SEARCH_RESULT_MODE)){
                renderPostSearchResultMode(job_module_component, request, conn);
            }
            else if (state.getMode().equals(State.SAVE_SEARCH_RESULT_MODE)){
                renderSaveSearchResultMode(job_module_component, request, conn);
            }
        }
        catch(Exception e){
            logger.error("Error while rendering job module component");
            logger.error(e.getMessage(), e);
            if (job_module_component!=null)
                job_module_component.setAttribute("mode", State.ERROR_MODE);
        }
        finally{
            DBHelper.close(conn);
        }
        return job_module_component;
    }

    public void renderGeneralMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.GENERAL_MODE);
        DepartmentXmlBuilder.buildVisitorDepartments(conn, parent, request.getSession().getLanguage().getId().intValue());
    }

    public void renderDepartmentVacanciesMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.DEPARTMENT_VACANCIES_MODE);
        Department dep = Department.findById(conn, state.getDepartmentId());
        parent.setAttribute("department", dep.getTitle());
        parent.setAttribute("dep_id", ""+state.getDepartmentId().longValue());
        VacancyXmlBuilder.buildVisitorVacanciesByDepartment(state.getDepartmentId(), parent, conn, request.getSession().getLanguage().getId());
    }
    
    public void renderRegionVacanciesMode(Element parent, RequestContext request, Connection conn)
    throws Exception{
    	parent.setAttribute("mode", State.REGION_VACANCIES_MODE);
    	if (state.getRegionId() != null) {
    		Region region = RegionService.getInstance().findById(state.getRegionId());
    		if (region  != null) {
    			buildRegionVacancies(parent, request, conn, region);
    			return;
    		}
    	}
    	for (Region region : RegionService.getInstance().list()) {
    		buildRegionVacancies(parent, request, conn, region);
		}
    }

	private void buildRegionVacancies(Element parent, RequestContext request,
			Connection conn, Region region) {
		Element regionEl = Xbuilder.addBeanJAXB(parent, region);
    	VacancyXmlBuilder.buildVisitorVacanciesByRegion(region.getId(), regionEl, conn, request.getSession().getLanguage().getId());
	}
    
   

    public void renderPostApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.POST_APPLICATION_MODE);
        parent.setAttribute("dep_id", ""+state.getDepartmentId().longValue());

        String vc = "";
        if (state.getVacancyId()!= null)
            vc = ""+state.getVacancyId().longValue();
        parent.setAttribute("vac_id", vc);

        if (state.getVacancyId()== null){
            ApplicationXmlBuilder.buildDepartmentApplicationForm(
                conn, parent, state.getDepartmentId(),
                request.getSession().getLanguage().getId());
        }
        else{
            ApplicationXmlBuilder.buildVacancyApplicationForm(conn,
                    parent, state.getVacancyId(),
                    request.getSession().getLanguage().getId());
        }
    }

    public void renderSaveApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SAVE_APPLICATION_MODE);
        parent.setAttribute("dep_id", ""+state.getDepartmentId().longValue());

        String vc = "";
        if (state.getVacancyId()!= null)
            vc = ""+state.getVacancyId().longValue();
        parent.setAttribute("vac_id", vc);
        ApplicationSaverUtil.saveApplication(request, conn);
    }

    public void renderShowAllVacanciesMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SHOW_ALL_VACANCIES_MODE);
        DepartmentXmlBuilder.buildDepartmentsAndVacancies(conn, parent, request.getSession().getLanguage().getId().intValue());
    }

    public void renderVacancyDetailsMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.VACANCY_DETAILS_MODE);
        VacancyXmlBuilder.buildVacancyDetails(conn, parent, state.getVacancyId(),
                request.getSession().getLanguage().getId(), false);
    }

    public void renderPostVacApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.POST_VAC_APPLICATION_MODE);
        parent.setAttribute("dep_id", ""+state.getDepartmentId().longValue());
        parent.setAttribute("vac_id", ""+state.getVacancyId().longValue());

        ApplicationXmlBuilder.buildVacancyApplicationForm(conn,
                    parent, state.getVacancyId(),
                    request.getSession().getLanguage().getId());
    }

    public void renderSaveVacApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SAVE_VAC_APPLICATION_MODE);
        parent.setAttribute("dep_id", ""+state.getDepartmentId().longValue());
        parent.setAttribute("vac_id", ""+state.getVacancyId().longValue());

        ApplicationSaverUtil.saveApplication(request, conn);
    }

    public void renderPostGenApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.POST_GEN_APPLICATION_MODE);
        DepartmentXmlBuilder.buildVisitorDepartments(conn, parent,
                request.getSession().getLanguage().getId().intValue());
        ApplicationXmlBuilder.buildGeneralApplicationForm(conn, parent,
                request.getSession().getLanguage().getId());
    }

    public void renderSaveGenApplicationMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SAVE_GEN_APPLICATION_MODE);
        ApplicationSaverUtil.saveGeneralApplication(request, conn);
    }

    public void renderSearchMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SEARCH_MODE);
    }

    public void renderDoSearchMode(Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.DO_SEARCH_MODE);
        SearchState sState = new SearchState(request);
        if (sState.getSearchWord()!=null)
            parent.setAttribute("search_word", sState.getSearchWord());
        if (sState.getSearchSalary()!=null)
            parent.setAttribute("search_salary", ""+sState.getSearchSalary().longValue());
        VacancyXmlBuilder.buildVacancySearchXml(request, conn, parent,
                request.getSession().getLanguage().getId());
    }

    public void renderSearchResultMode (Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SEARCH_RESULT_MODE);
        SearchState sState = new SearchState(request);
        if (sState.getSearchWord()!=null)
            parent.setAttribute("search_word", sState.getSearchWord());
        if (sState.getSearchSalary()!=null)
            parent.setAttribute("search_salary", ""+sState.getSearchSalary().longValue());
        VacancyXmlBuilder.buildFoundVacancyXml(request, conn, parent,
                request.getSession().getLanguage().getId());
    }

    public void renderPostSearchResultMode (Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.POST_SEARCH_RESULT_MODE);
        SearchState sState = new SearchState(request);
        if (sState.getSearchWord()!=null)
            parent.setAttribute("search_word", sState.getSearchWord());
        if (sState.getSearchSalary()!=null)
            parent.setAttribute("search_salary", ""+sState.getSearchSalary().longValue());
        parent.setAttribute("vac_id", ""+state.getVacancyId().longValue());
        parent.setAttribute("dep_id", ""+Vacancy.findById(conn, state.getVacancyId()).getDepartmentId().longValue());
        ApplicationXmlBuilder.buildVacancyApplicationForm(conn,
                    parent, state.getVacancyId(),
                    request.getSession().getLanguage().getId());
    }

    public void renderSaveSearchResultMode (Element parent, RequestContext request, Connection conn)
            throws Exception{
        parent.setAttribute("mode", State.SAVE_SEARCH_RESULT_MODE);
        SearchState sState = new SearchState(request);
        if (sState.getSearchWord()!=null)
            parent.setAttribute("search_word", sState.getSearchWord());
        if (sState.getSearchSalary()!=null)
            parent.setAttribute("search_salary", ""+sState.getSearchSalary().longValue());
        parent.setAttribute("vac_id", ""+state.getVacancyId().longValue());

        ApplicationSaverUtil.saveApplication(request, conn);
    }
}
