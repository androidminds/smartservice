package cn.androidminds.jwtservice.service;


import cn.androidminds.commonapi.rest.RestResponse;
import cn.androidminds.jwtservice.feign.UserServiceProxy;
import cn.androidminds.jwtservice.jwt.JwtTokenFactory;
import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.service.IJwtService;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.userserviceapi.Error.ErrorCode;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Service
public class JwtService implements IJwtService {
    @Autowired
    private UserServiceProxy userServiceProxy;

    @Autowired
    JwtTokenFactory tokenFactory;

    public ResponseEntity<String> login(String identity, String password) {
        RestResponse<UserInfo> response = userServiceProxy.verify(identity, password);
        if(response != null && response.getStatusCode() == ErrorCode.OK) {
            if(response.getData() != null) {
                try {
                    return ResponseEntity.ok(tokenFactory.generateToken(response.getData()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.badRequest().body("");
    }

    public ResponseEntity<String> refresh(@RequestParam(value = "old-token")String oldToken) {
        try {
            JwtInfo jwtInfo = JwtUtil.getJwtInfo(oldToken, tokenFactory.getPublicKey());
            if(jwtInfo.getUserName() != null) {
                return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("");
    }

    public ResponseEntity<String> getPubKey() {
        return ResponseEntity.ok(tokenFactory.getPublicKey().toString());
    }
}
