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
        web.ignoring().antMatchers("/resources/**");//????????????????????????
    }

//    @Override//??????????????????????????????,BUILDER?????????????????????????????????  ??????????????????
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //?????????????????????
//        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));//??????
//        //?????????????????????
//        //AuthenticationProvider:ProviderManager????????????AuthenticationProvider???????????????????????????
//        //???????????????ProviderManager?????????????????????AuthenticationProvider
//        auth.authenticationProvider(new AuthenticationProvider() {
//            //Authentication:???????????????????????????????????????????????????????????????????????????????????????
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                String username = authentication.getName();
//                String password =(String) authentication.getCredentials();
//                User user=userService.findUserByName(username);
//                if(user==null){
//                    throw new UsernameNotFoundException("???????????????");
//                }
//                password = CommunityUtil.md5(password+user.getSalt());
//                if(!user.getPassword().equals(password)){
//                    throw new BadCredentialsException("???????????????");
//                }
//                //??????????????????????????????
//                return new UsernamePasswordAuthenticationToken(user,user.getPassword(),userService.getAuthorities());
//            }
//            //??????????????????AuthenticationProvider???????????????????????????
//            @Override
//            public boolean supports(Class<?> aClass) {
//                //UsernamePasswordAuthenticationToken ???Authentication?????????????????????????????????????????????????????????
//                return UsernamePasswordAuthenticationToken.class.equals(aClass);
//            }
//        });
//    }

    @Override //????????????
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
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
        .anyRequest().permitAll()
        .and().csrf().disable();//
        //????????????????????????
        http.exceptionHandling()//?????????????????????
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        String header = httpServletRequest.getHeader("x-requested-with");//??????????????????
                        if("XMLHttpRequest".equals(header))//????????????
                        {
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"?????????!"));
                        }else{
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/login");
                        }
                    }
                })
        .accessDeniedHandler(new AccessDeniedHandler() {//?????????????????????
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                String header = httpServletRequest.getHeader("x-requested-with");//??????????????????
                if("XMLHttpRequest".equals(header))//????????????
                {
                    httpServletResponse.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = httpServletResponse.getWriter();
                    writer.write(CommunityUtil.getJsonString(403,"????????????!"));
                }else{
                    httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/denied");
                }
            }
        });
        http.logout().logoutUrl("/securitylogout");
    }
}
