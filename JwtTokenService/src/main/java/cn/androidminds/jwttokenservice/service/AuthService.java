package cn.androidminds.jwttokenservice.service;

import cn.androidminds.jwtcommon.JwtInfo;
import cn.androidminds.jwttokenservice.feign.IUserService;
import cn.androidminds.jwttokenservice.jwt.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class  AuthService {
    @Autowired
    private IUserService userService;

    JwtTokenFactory tokenFactory = new JwtTokenFactory();

    public String login(String identity, String password) throws Exception {
        boolean passed = userService.verify(identity, password);
        if(passed) {
            JwtInfo info = userService.getInfo(identity);
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

    public byte[] getPubKey() {
        return tokenFactory.getPublicKey();
    }
}
