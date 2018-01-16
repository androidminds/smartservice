package cn.androidminds.jwtservice.service;

import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

//@Component
public class AuthFilter implements Filter {
    @Autowired
    JwtTokenFactory tokenFactory;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        if (uri.equalsIgnoreCase("/refresh")) {

        }
        chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {

    }
}
