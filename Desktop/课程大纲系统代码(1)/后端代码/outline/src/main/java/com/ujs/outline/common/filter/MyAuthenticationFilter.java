package com.ujs.outline.common.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujs.outline.domain.User;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 拦截器
 * 用于Spring Security 登录验证时额外解析前端JSON格式数据
 *
 * @author wjy
 */
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //查看请求传入的是否为JSON数据
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)
                || request.getContentType().contains("json/application")) {
            String username = null;
            String password = null;
            try {
                Map<String, String> map = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                username = map.get("user_id");
                password = map.get("user_password");
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }
            username = username.trim();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        //非JSON数据用父类原有方式判断
        else {
            System.out.println(1);
            return super.attemptAuthentication(request, response);
        }
    }
}