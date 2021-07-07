package com.leej.community.Config;

import com.leej.community.entity.User;
import com.leej.community.service.UserService;
import com.leej.community.utils.CommunityConstant;
import com.leej.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");//忽略对资源的拦截
    }

//    @Override//参数是认证的核心接口,BUILDER用于构建接口对象的工具  实现认证配置
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //内置的认证规则
//        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));//传入
//        //自定义认证规则
//        //AuthenticationProvider:ProviderManager持有一组AuthenticationProvider，分别负责一种认证
//        //委托模式，ProviderManager将认证委托给了AuthenticationProvider
//        auth.authenticationProvider(new AuthenticationProvider() {
//            //Authentication:用于封装认证信息的接口，不同的实现类代表不同类型的认证信息
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                String username = authentication.getName();
//                String password =(String) authentication.getCredentials();
//                User user=userService.findUserByName(username);
//                if(user==null){
//                    throw new UsernameNotFoundException("账号不存在");
//                }
//                password = CommunityUtil.md5(password+user.getSalt());
//                if(!user.getPassword().equals(password)){
//                    throw new BadCredentialsException("密码不正确");
//                }
//                //主要信息，证书，权限
//                return new UsernamePasswordAuthenticationToken(user,user.getPassword(),userService.getAuthorities());
//            }
//            //返回当前接口AuthenticationProvider支持什么类型的认证
//            @Override
//            public boolean supports(Class<?> aClass) {
//                //UsernamePasswordAuthenticationToken 是Authentication接口的常用实现类，支持账号密码形式认证
//                return UsernamePasswordAuthenticationToken.class.equals(aClass);
//            }
//        });
//    }

    @Override //页面绑定
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/setting"
                ,"/user/upload"
                ,"/discuss/add"
                ,"/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow").hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderfull"
                )
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
        .anyRequest().permitAll()
        .and().csrf().disable();//
        //权限不够时的处理
        http.exceptionHandling()//未登录时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        String header = httpServletRequest.getHeader("x-requested-with");//判断请求类型
                        if("XMLHttpRequest".equals(header))//异步请求
                        {
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"未登录!"));
                        }else{
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/login");
                        }
                    }
                })
        .accessDeniedHandler(new AccessDeniedHandler() {//权限不够的处理
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                String header = httpServletRequest.getHeader("x-requested-with");//判断请求类型
                if("XMLHttpRequest".equals(header))//异步请求
                {
                    httpServletResponse.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = httpServletResponse.getWriter();
                    writer.write(CommunityUtil.getJsonString(403,"权限不足!"));
                }else{
                    httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/denied");
                }
            }
        });
        http.logout().logoutUrl("/securitylogout");
    }
}
