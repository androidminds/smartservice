package cn.androidminds.jwtservice.service;

import cn.androidminds.jwtserviceapi.service.IJwtService;
import cn.androidminds.jwtservice.feign.UserServiceProxy;
import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JwtService implements IJwtService {
    @Autowired
    private UserServiceProxy userServiceProxy;

    JwtTokenFactory tokenFactory = new JwtTokenFactory();

    public String login(String identity, String password) {
        boolean passed = userServiceProxy.verify(identity, password);
        if(passed) {
            UserInfo info = userServiceProxy.getInfo(identity);
            if(info != null) {
                return tokenFactory.generateToken(info);
            }
        }
        return "";
    }

    public  boolean logout (String token) {
        return true;
    }

    public String refresh(String oldToken) {
        return null;
    }

    public String getPubKey() {
        return new String(tokenFactory.getPublicKey());
    }
}
