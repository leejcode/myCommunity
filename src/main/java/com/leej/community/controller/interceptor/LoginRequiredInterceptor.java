package com.leej.community.controller.interceptor;

import com.leej.community.annotation.LoginRequired;
import com.leej.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            Method method = handlerMethod.getMethod();//获取对应的方法
            LoginRequired annotation = method.getAnnotation(LoginRequired.class); //获取方法的修饰注解
            if(annotation !=null &&hostHolder.getUser()==null){
                response.sendRedirect(request.getContextPath()+"/login");//重定向
                return false;//拦截访问
            }
        }
        return true;
    }
}
