package cn.androidminds.jwtservice.service;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.service.IJwtService;
import cn.androidminds.jwtservice.feign.UserServiceProxy;
import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@Service
public class JwtService implements IJwtService {
    @Autowired
    private UserServiceProxy userServiceProxy;

    JwtTokenFactory tokenFactory;

    public JwtService() {
        try {
            tokenFactory = new JwtTokenFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String login(String identity, String password) {
        boolean passed = userServiceProxy.verify(identity, password);
        if(passed) {
            UserInfo userInfo = userServiceProxy.getInfo(identity);
            if(userInfo != null) {
                try {
                    return tokenFactory.generateToken(userInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public String refresh(String oldToken) {
        try {
            JwtInfo jwtInfo = JwtUtil.getJwtInfo(oldToken, tokenFactory.getPublicKey());
            return tokenFactory.generateToken(jwtInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPubKey() {
        return new String(tokenFactory.getPublicKey());
    }
}
