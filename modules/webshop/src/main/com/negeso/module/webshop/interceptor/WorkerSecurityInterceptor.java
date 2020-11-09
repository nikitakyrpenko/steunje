package com.negeso.module.webshop.interceptor;

import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WorkerSecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);

        if (user.getType().equals("hairdresser")){
            return true;
        }else {
            throw new HttpException(403, "Forbidden path to such User type");
        }
    }
}
