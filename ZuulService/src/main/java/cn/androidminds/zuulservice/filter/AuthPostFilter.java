package cn.androidminds.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;


@Component
public class AuthPostFilter extends ZuulFilter {

    @Autowired
    AuthUriManager authUriManager;

    @Override
    public String filterType() {
        return "post";
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
        final String requestUri = context.getRequest().getRequestURI();

        if(authUriManager.isRequireTokenUri(requestUri)) {
            if(context.getResponseStatusCode() == HttpStatus.OK.value()) {
                try {
                    InputStream in = context.getResponseDataStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    context.setResponseBody(null);
                    context.addZuulResponseHeader(HttpHeaders.AUTHORIZATION, new String(buffer));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if(!authUriManager.isAnonymousAccessUri(requestUri)) {


        }
        return null;
    }

}
