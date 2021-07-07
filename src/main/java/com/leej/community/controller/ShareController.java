package com.leej.community.controller;

import com.leej.community.entity.Event;
import com.leej.community.event.EventProducer;
import com.leej.community.utils.CommunityConstant;
import com.leej.community.utils.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;
    @Value("${community.path.domain}")
    private String domin;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${wk.image.store}")
    private String wkImageStorage;

    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl){
        //生成文件名
        String fileName= CommunityUtil.generateUUID();
        //异步生成长图
        Event event =new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);
        //返回访问路径
        Map<String,Object> map =new HashMap<>();
        map.put("shareUrl",domin+contextPath+"/share/image/"+fileName);
        return CommunityUtil.getJsonString(0,null,map);
    }
    @GetMapping("/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName")String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("非法参数");
        }
        response.setContentType("image/png");
        File file = new File(wkImageStorage+"/"+fileName+".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取输出流失败"+e.getMessage());
        }

    }
}
