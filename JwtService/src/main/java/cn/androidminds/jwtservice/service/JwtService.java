package cn.androidminds.jwtservice.service;


import cn.androidminds.commonapi.Exception.ExceptionOutput;
import cn.androidminds.jwtservice.feign.UserServiceProxy;
import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.commonapi.jwt.JwtInfo;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


@RestController
@Service
public class JwtService implements IJwtService {
    private Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Qualifier("UserServiceFeignClient")
    @Autowired
    UserServiceProxy userServiceProxy;

    @Autowired
    JwtTokenFactory tokenFactory;

    public ResponseEntity<String> login(@RequestParam(value = "identity") String identity,
                                        @RequestParam(value = "password") String password) {
        ResponseEntity<UserInfo> response = userServiceProxy.verify(identity, password);
        if (response != null && response.getStatusCode() == HttpStatus.OK
                && response.getBody() != null) {
            try {
                UserInfo userInfo = response.getBody();
                JwtInfo jwtInfo = new JwtInfo(userInfo.getName(),
                        userInfo.getId().toString(), null);
                return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
            } catch (InvalidKeySpecException e) {
                logger.error(ExceptionOutput.toString(e));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } catch (NoSuchAlgorithmException e) {
                logger.error(ExceptionOutput.toString(e));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.badRequest().body("");
    }

    public ResponseEntity<String> refreshToken(@RequestHeader("userName")String userName,
                                          @RequestHeader("userId")String userId) {
        try {
            if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                JwtInfo jwtInfo = new JwtInfo(userName, userId, null);
                return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
            }
        } catch (Exception e) {
            logger.error(ExceptionOutput.toString(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> getPublicKey() {
        try {
            return ResponseEntity.ok(Base64Utils.encodeToString(tokenFactory.getPublicKey()));
        } catch (Exception e) {
                logger.error(ExceptionOutput.toString(e));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
