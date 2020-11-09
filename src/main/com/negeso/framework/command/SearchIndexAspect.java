package com.negeso.framework.command;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import com.negeso.framework.controller.ResponseContext;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.exception.NoSuchParameterException;
import com.negeso.module.core.service.ParameterService;

@Aspect
public class SearchIndexAspect {
	private static Logger logger = Logger.getLogger(SearchIndexAspect.class);
	
	private String SEARCH_INDEX_DIRTY_PARAM = "search.index.dirty";
	
	private ParameterService parameterService;
	
	@AfterReturning(
			pointcut="@annotation(com.negeso.framework.command.SetSearchIndexDirty)", 
			returning="retResponse")
	public void setSearchIndexDirty(Object retResponse){
		if (((ResponseContext)retResponse).getResultName().equals(Command.RESULT_SUCCESS)){
			//setting search index dirty
			ConfigurationParameter parameter = parameterService.
											findParameterByName(SEARCH_INDEX_DIRTY_PARAM);
			if (parameter == null){
				throw new NoSuchParameterException(SEARCH_INDEX_DIRTY_PARAM);
			}
			if (!parameter.getValue().equals("true")){
				parameter.setValue("true");
				parameterService.update(parameter, false);
			}
			
		}
		
	}
	

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
}
