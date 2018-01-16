package cn.androidminds.zuulservice.filter;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.zuulservice.feign.JwtServiceProxy;
import cn.androidminds.zuulservice.feign.UserServiceProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


@Component
public class AuthPreFilter extends ZuulFilter {

    @Autowired
    private UserServiceProxy userServiceProxy;
    @Autowired
    private JwtServiceProxy jwtServiceProxy;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        final String requestUri = request.getRequestURI();

        if(requestUri.equalsIgnoreCase("/auth/refresh")) {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            try {
                JwtInfo jwtInfo = JwtUtil.getJwtInfo(token, getJWTPublicKey());
                ObjectMapper mapper = new ObjectMapper();
                final byte[] reqBodyBytes = mapper.writeValueAsBytes(jwtInfo);

                context.setRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }
                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }
                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
                context.addZuulRequestHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_UTF8.toString());
            } catch (Exception e) {
                e.printStackTrace();
                setFailedRequest("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
            }
        }
        return null;
    }


    private byte[] getJWTPublicKey() {
        return Base64Utils.decodeFromString(jwtServiceProxy.getPubKey().getBody());
    }


    private void setFailedRequest(String body, int code) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
    }

}
