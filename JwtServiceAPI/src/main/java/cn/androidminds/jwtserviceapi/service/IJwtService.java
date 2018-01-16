package cn.androidminds.jwtserviceapi.service;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IJwtService {
    @PostMapping("/login")
    ResponseEntity<String> login(@RequestParam(value="identity") String identity,
                                 @RequestParam(value="password") String password);

    @PostMapping("/refresh")
    ResponseEntity<String> refresh(@RequestBody JwtInfo jwtInfo);

    @GetMapping("/public-key")
    ResponseEntity<String> getPubKey();
}
