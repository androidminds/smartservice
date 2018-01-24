package cn.androidminds.zuulservice.filter;

import cn.androidminds.commonapi.jwt.JwtInfo;
import cn.androidminds.commonapi.jwt.JwtUtil;
import cn.androidminds.zuulservice.feign.JwtServiceProxy;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;


@Component
public class AuthPreFilter extends ZuulFilter {
    byte[] jwtPublicKey;

    @Autowired
    JwtServiceProxy jwtServiceProxy;

    @Autowired
    AuthUriManager authUriManager;

    @Value("${authority.token.remain-minutes}")
    int remainMinutes;

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

        if(authUriManager.isAnonymousAccessUri(requestUri))
            return null;

        // check token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        JwtInfo jwtInfo = null ;

        try {
            jwtInfo = JwtUtil.getJwtInfo(token, getJwtPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(jwtInfo == null ||
                StringUtils.isEmpty(jwtInfo.getUserName()) ||
                StringUtils.isEmpty(jwtInfo.getUserId()) ||
                jwtInfo.getExpireDate() == null) {
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            context.setSendZuulResponse(false);
            return null;
        }

        if(jwtInfo.getExpireDate().toInstant().isBefore(Instant.now())) {
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            context.setSendZuulResponse(false);
            return null;
        }

        // change the almost expired token
        if(!authUriManager.isRequireTokenUri(requestUri)) {
            if(jwtInfo.getExpireDate().toInstant().isBefore(Instant.now().plus(remainMinutes,MINUTES))) {
                ResponseEntity<String> response = jwtServiceProxy.refreshToken(jwtInfo.getUserName(), jwtInfo.getUserId());
                if(response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    context.addZuulResponseHeader(HttpHeaders.AUTHORIZATION, response.getBody());
                }
            }
        }

        // attach new param
        context.addZuulRequestHeader("userName", jwtInfo.getUserName());
        context.addZuulRequestHeader("userId", jwtInfo.getUserId());
        return null;
    }

    private byte[] getJwtPublicKey() {
        if(jwtPublicKey == null) {
            ResponseEntity<String> response = jwtServiceProxy.getPublicKey();
            if(response != null && response.getBody() != null) {
                jwtPublicKey = Base64Utils.decodeFromString(response.getBody());
            }
        }
        return jwtPublicKey;
    }
}
