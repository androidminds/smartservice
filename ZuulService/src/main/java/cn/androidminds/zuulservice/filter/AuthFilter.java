package cn.androidminds.zuulservice.filter;



import cn.androidminds.jwtcommon.JwtInfo;
import cn.androidminds.jwtcommon.JwtUtil;
import cn.androidminds.zuulservice.feign.IJwtTokenService;
import cn.androidminds.zuulservice.feign.IUserService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private IUserService userService;
    @Autowired
    private IJwtTokenService jwtTokenService;

    /*
    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Value("${zuul.prefix}")
    private String zuulPrefix;
*/

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
        final String requestUri = request.getRequestURI();//.substring(zuulPrefix.length());
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


        // 申请客户端密钥头
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
        return jwtTokenService.getPubKey().getBytes();
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
