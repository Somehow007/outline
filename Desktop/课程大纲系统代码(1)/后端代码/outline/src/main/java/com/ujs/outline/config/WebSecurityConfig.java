package com.ujs.outline.config;

import com.alibaba.fastjson.JSONObject;
import com.ujs.outline.common.ResultObj;
import com.ujs.outline.common.filter.MyAuthenticationFilter;
import com.ujs.outline.common.filter.ValidateCodeFilter;
import com.ujs.outline.common.handler.MyAuthenticationFailureHandler;
import com.ujs.outline.common.handler.MyAuthenticationSuccessHandler;
import com.ujs.outline.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Spring Security配置
 * 包含登录、退出接口配置
 * @author wjy
 */

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index", "/js/**", "/css/**", "/fonts/**", "/img/**", "/error/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setMyAuthenticationFailureHandler(myAuthenticationFailureHandler);

        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/outline/download").permitAll()
                .antMatchers("/outline/").permitAll()
                .antMatchers("/outline/searchForOldOutline").permitAll()
                .antMatchers("/outline/user/**").hasAuthority("user")
                .antMatchers("/outline/admin/**").hasAuthority("admin")
                .antMatchers("/outline/mainAdmin/**").hasAuthority("mainAdmin")
                .antMatchers("/code/**").permitAll()
                .antMatchers("/course/").permitAll()
                .antMatchers("/course/searchByOutlineId").permitAll()
                .antMatchers("/course/user/**").hasAuthority("user")
                .antMatchers("/course/admin/**").hasAuthority("admin")
                .antMatchers("/course/mainAdmin/**").hasAuthority("mainAdmin")
                .antMatchers("/college/").permitAll()
                .antMatchers("/college/mainAdmin/**").hasAuthority("mainAdmin")
                .antMatchers("/major/").permitAll()
                .antMatchers("/major/mainAdmin/**").hasAuthority("mainAdmin")
                .antMatchers("/user/").hasAuthority("user")
                .antMatchers("/user/user/**").hasAuthority("user")
                .antMatchers("/user/mainAdmin/**").hasAuthority("mainAdmin")
                .antMatchers("/excel/**").hasAuthority("mainAdmin")
                .anyRequest().authenticated()
                .and()
                .cors().and()//跨域
                .formLogin()
                .loginPage("/index")
                .and()
                //----------退出登录配置-----------
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)//清除认证
                .invalidateHttpSession(true)//使session无效
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse,
                                       Authentication authentication) {
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse,
                                                Authentication authentication) throws IOException, ServletException {

                    }
                })
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(myAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    MyAuthenticationFilter myAuthenticationFilter() throws Exception {
        MyAuthenticationFilter filter = new MyAuthenticationFilter();
        filter.setFilterProcessesUrl("/loginByAccount");
        filter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter out = resp.getWriter();
                resp.setStatus(HttpStatus.UNAUTHORIZED.value());
                e.printStackTrace();
                out.write(JSONObject.toJSONString(ResultObj.LOGIN_ERROR_PASS));
                out.flush();
                out.close();
            }
        });
        filter.setAuthenticationManager(super.authenticationManagerBean());
        return filter;
    }

    /**
     * 将客户端未登录时请求返回的302改为401，以便前端拦截
     */
    static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            PrintWriter out = response.getWriter();
            out.write(JSONObject.toJSONString(ResultObj.UNAUTHORIZED_ERROR));
            out.flush();
            out.close();
        }
    }
}

