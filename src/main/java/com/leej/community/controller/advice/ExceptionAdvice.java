package com.leej.community.controller.advice;

import com.leej.community.utils.CommunityUtil;
import jdk.jfr.StackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler({Exception.class})//捕获什么类型异常
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
            logger.error("服务器发生异常"+e.getMessage());
            for(StackTraceElement element: e.getStackTrace()){
                logger.error(element.toString());
            }
            String x = request.getHeader("x-requested-with");
            if("XMLHttpRequest".equals(x)){//这是一个异步请求返回json
                response.setContentType("application/plain;charSet=utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(CommunityUtil.getJsonString(1,"服务器发生异常"));
            }else{
                response.sendRedirect(request.getContextPath()+"/error");
            }
    }
}
