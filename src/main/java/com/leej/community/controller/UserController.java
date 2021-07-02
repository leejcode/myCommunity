package com.leej.community.controller;

import com.leej.community.annotation.LoginRequired;
import com.leej.community.entity.User;
import com.leej.community.service.UserService;
import com.leej.community.utils.CommunityUtil;
import com.leej.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @LoginRequired
    @GetMapping("setting")
    public String getSettingPage(){
        return "/site/setting";
    }
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","未选择图片");
            return "/site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();//获取图片名字
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); //获取图像后缀
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机的文件名
        String s = CommunityUtil.generateUUID() + suffix;
        //确定路径
        File dest = new File(uploadPath+"/"+s);
        try {
            headerImage.transferTo(dest);//存到本地路径中
        } catch (IOException e) {
            System.out.println("上传文件失败");
            e.printStackTrace();
            throw new RuntimeException("上传文件失败，服务器发生异常");
        }
        //更新当前用户的头像的路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+s;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";
    }
    @GetMapping("/header/{fileName}")//这里提供图像访问方式  关联web地址和本地地址图片
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (FileInputStream fis = new FileInputStream(fileName)){
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            System.out.println("读取图像失败");
            e.printStackTrace();
        }

    }
}
