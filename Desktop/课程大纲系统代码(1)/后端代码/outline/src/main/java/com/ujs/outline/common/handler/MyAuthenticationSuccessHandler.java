package com.ujs.outline.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.ujs.outline.common.ResultObj;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        resp.setStatus(200);
        out.write(JSONObject.toJSONString(ResultObj.LOGIN_SUCCESS));
        out.flush();
        out.close();
    }
}
