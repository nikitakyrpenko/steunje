package com.negeso.framework.command;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.module.engine.ModuleEngine;
import com.negeso.framework.security.SecurityGuard;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityAspect {
	private static Logger logger = Logger.getLogger(SecurityAspect.class);
	
	private String stage;
	
	private final String DEV_STAGE = "dev";
	
	@Around("@annotation(com.negeso.framework.command.SuperuserRequired)")
	public ResponseContext checkIfUserIsSuperuser(ProceedingJoinPoint joinPoint) throws Throwable{
		RequestContext request = ((Command)joinPoint.getTarget()).getRequestContext();
		
		User user = request.getSession().getUser();
		if (user!=null){
			if (stage==null || !stage.equals(DEV_STAGE)){
				if (user.isSuperUser()){
					return (ResponseContext)joinPoint.proceed();
				}
			}else{
				if (SecurityGuard.isAdministrator(user)){
					return (ResponseContext)joinPoint.proceed();
				}
			}
			
		}
		ResponseContext response = new ResponseContext();
		response.setResultName(Command.RESULT_ACCESS_DENIED);
		return response;
	}
	
	@Around("@annotation(com.negeso.framework.command.ActiveModuleRequired)")
	public ResponseContext checkIfModuleActive(ProceedingJoinPoint joinPoint) throws Throwable{
		String moduleName = ((Command)joinPoint.getTarget()).getModuleName();
		ResponseContext response = null;
		if (!ModuleEngine.getInstance().isModuleExpired(moduleName)){
			response = (ResponseContext)joinPoint.proceed();
		}else{
			response = new ResponseContext();
			response.setResultName(Command.RESULT_ACCESS_DENIED);
            logger.debug("- Access to disabled module denied");
		}
		return response;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

}
