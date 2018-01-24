package cn.androidminds.zuulservice.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AuthUriManager {
    @Value("${authority.token.uri}")
    String tokenUris;

    @Value("${authority.anonymous.uri}")
    String anonymousUris;

    String[] tokenUriList;

    String[] anonymousUriList;

    @PostConstruct
    public void init() {
        tokenUriList = tokenUris.split(",");
        anonymousUriList = anonymousUris.split(",");
    }

    public boolean isAnonymousAccessUri(String uri) {
        if(anonymousUriList != null) {
            for (String s : anonymousUriList) {
                if (s.equalsIgnoreCase(uri))
                    return true;
            }
        }
        return false;
    }

    public boolean isRequireTokenUri(String uri) {
        if(tokenUriList != null) {
            for (String s : tokenUriList) {
                if (s.equalsIgnoreCase(uri))
                    return true;
            }
        }
        return false;
    }
}
