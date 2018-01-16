package cn.androidminds.jwtservice.service;


import cn.androidminds.jwtservice.feign.UserServiceProxy;
import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.service.IJwtService;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@Service
public class JwtService implements IJwtService {
    @Autowired
    private UserServiceProxy userServiceProxy;

    @Autowired
    JwtTokenFactory tokenFactory;


    public ResponseEntity<String> login(String identity, String password) {
        ResponseEntity<UserInfo> response = userServiceProxy.verify(identity, password);
        if(response != null && response.getStatusCode() == HttpStatus.OK) {
            if(response.getBody() != null) {
                try {
                    return ResponseEntity.ok(tokenFactory.generateToken(response.getBody()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.badRequest().body("");
    }

    @Override
    public ResponseEntity<String> refresh(@RequestBody JwtInfo jwtInfo) {
        try {
            return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity<String> getPubKey() {
        return ResponseEntity.ok(Base64Utils.encodeToString(tokenFactory.getPublicKey()));
    }
}
