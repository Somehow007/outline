package com.ujs.outline.common.filter;

import com.ujs.outline.common.exception.ValidateCodeException;
import com.ujs.outline.common.handler.MyAuthenticationFailureHandler;
import com.ujs.outline.controller.ValidateCodeController;
import com.ujs.outline.domain.ImageCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * 验证码拦截器
 * 验证前端登录请求验证码参数 imageCode
 */
public class ValidateCodeFilter extends OncePerRequestFilter {

    private MyAuthenticationFailureHandler demoAuthenticationFailureHandler;


    private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (StringUtils.equals("/loginByAccount", request.getRequestURI()) &&
                StringUtils.endsWithIgnoreCase(request.getMethod(), "post")) {
            try {
                validate(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                demoAuthenticationFailureHandler.onAuthenticationFailure(request,response,e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    //具体的验证流程
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY);
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInSession.isExpried()) {
            sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }
        sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
    }

    public MyAuthenticationFailureHandler getMyAuthenticationFailureHandler() {
        return demoAuthenticationFailureHandler;
    }

    public void setMyAuthenticationFailureHandler(MyAuthenticationFailureHandler demoAuthenticationFailureHandler) {
        this.demoAuthenticationFailureHandler = demoAuthenticationFailureHandler;
    }
}