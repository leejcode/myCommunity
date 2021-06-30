package com.leej.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController //相当于controller+responsebody注解的组合，前者将该类对象注入到容器中，
                // 后者说明该类所有的API接口返回的数据都以json形式返回给客户端 String除外
public class test {
    @GetMapping("/hello")
    public String sayhello(){
        System.out.println("qee");
        return "Hello Spring boot";
    }
    @GetMapping
    public Map testGet() {
        return new HashMap<String, String>(){{
            put("name", "springboot");
        }};
    }
    @GetMapping("/http") //不使用返回值，通过response返回结果  默认是get请求
    public void http(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());//输出请求的方式
        System.out.println(request.getServletPath());//请求的路径
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+' '+value);
        }
        System.out.println(request.getParameter("code"));
        //response返回响应数据
        response.setContentType("text/html;charset=utf-8");//设置内容类型
        try {
            PrintWriter writer = response.getWriter();//写入响应体的内容
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    public String getStudents(@RequestParam(name = "current",required = false) int current,
                              @RequestParam(name = "limit",required = false) int limit){//名字一致可以直接获取到参数
                                // 或者注解更详细的设置 获取?后的参数
                                //获取地址上的参数/students/current      ，
        System.out.println(current);
        System.out.println(limit);
        return "s";
    }
    // /students/123
    @GetMapping(path = "/students/{id}")
    public String getStudent(@PathVariable(name = "id") int id
                             ){//获取地址上的参数/students/{id}
        System.out.println(id);
        return "s";
    }
    @PostMapping(path = "/student")
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
    //响应html数据
    @GetMapping(path="/teather")
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",14);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path = "/school")
    public String getSchool(ModelMap model){//无法搭配responseBody一起使用
        model.addAttribute("name","xx大学");
        model.addAttribute("age",14);
        return "/demo/view";
    }
    //响应json数据，异步
    //java对象->json字符串->js对象，json作为不同语言间沟通的桥梁
    @GetMapping(path = "/emp")
    public Map<String,Object> getEmp(){
        Map<String,Object> map=new HashMap<>();
        map.put("name","张三");
        map.put("age",22);
        map.put("salary",9000);
        return map;
    }
}
