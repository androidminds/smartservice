package cn.androidminds.userserviceapi.Hystrix;

import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.feign.UserServiceFeign;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHystric implements UserServiceFeign {
    @Override
    public ResponseEntity<UserInfo> create(UserInfo userInfo) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<UserInfo[]> list(long page, int count) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<UserInfo> get(Long id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<Integer> modify(UserInfo userInfo) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<UserInfo> verify(String identity, String password) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
