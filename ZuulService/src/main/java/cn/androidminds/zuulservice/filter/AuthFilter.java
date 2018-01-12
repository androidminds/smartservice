package cn.androidminds.zuulservice.filter;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.zuulservice.feign.JwtServiceProxy;
import cn.androidminds.zuulservice.feign.UserServiceProxy;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class AuthFilter extends ZuulFilter {

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
        final String method = request.getMethod();
        System.out.println("requestUri: " + requestUri + "; method: " + method);
/*
        if (isNonAuthUri(requestUri)) {
            return null;
        }
*/
        // check token
        JwtInfo jwtInfo = null;
        try {
            jwtInfo = getJwtInfoFromToken(request, context);
        } catch (Exception e) {
            //setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
            return null;
        }

        // check permission

        //context.addZuulRequestHeader(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken());

        return null;
    }

    private JwtInfo getJwtInfoFromToken(HttpServletRequest request, RequestContext context) throws Exception {
        String authToken = request.getHeader("token-header");
        if (StringUtils.isBlank(authToken)) {
            authToken = request.getParameter("token");
        }
        context.addZuulRequestHeader("token-header", authToken);

        return JwtUtil.getJwtInfo(authToken, getJWTPublicKey());
    }

    private byte[] getJWTPublicKey() {
        return jwtServiceProxy.getPubKey().getBody().getBytes();
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
