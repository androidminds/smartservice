package cn.androidminds.jwttokenserviceapi.histrix;

import cn.androidminds.jwttokenserviceapi.feign.JwtTokenServiceFeign;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenServiceHistrix implements JwtTokenServiceFeign {
    @Override
    public ResponseEntity<String> login(String identity, String password) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> refreshToken(String userName, String userId) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
