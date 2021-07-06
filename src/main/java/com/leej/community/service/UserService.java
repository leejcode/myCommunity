package com.leej.community.service;

import com.leej.community.dao.LoginTicketMapper;
import com.leej.community.dao.UserMapper;
import com.leej.community.entity.LoginTicket;
import com.leej.community.entity.User;
import com.leej.community.utils.CommunityConstant;
import com.leej.community.utils.CommunityUtil;
import com.leej.community.utils.MailClient;
import com.leej.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id){
//        return userMapper.selectById(id);
        User user=getCache(id);
        if(user==null){
            user = initCache(id);
        }
        return user;
    }
    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证账号是否已被注册
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setStatus(0);//未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //激活邮件
        Context context =new Context();
        context.setVariable("email",user.getEmail());
        //
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);//处理对应的界面中的thymeleaf
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            deleteCache(userId);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAILURE;
        }
    }
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user=userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账户未创建");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));
        loginTicket.setStatus(0);
        loginTicket.setUserId(user.getId());
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);//redis会自动把对象序列化为json
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket o = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        o.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,o);
//        loginTicketMapper.updateStatus(ticket,1);
    }
    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
//        return loginTicketMapper.selectByTicket(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    public int updateHeader(int userId,String headerUrl){
//        return userMapper.updateHeader(userId,headerUrl);
        int rows = userMapper.updateHeader(userId,headerUrl);
        deleteCache(userId);
        return rows;
    }
    public User findUserByName(String name){
        return userMapper.selectByName(name);
    }
    //优先从缓存中取
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(userKey);
    }
    // 取不到时初始化缓存
    private User initCache(int userId){
        User user =userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //数据变更时删除缓存
    private void deleteCache(int userId){
        System.out.println("更新cache");
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
    //
    public Collection<? extends GrantedAuthority> getAuthority(int userId){
        User user = userMapper.selectById(userId);
        List<GrantedAuthority> list =new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {//获取用户权限
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
