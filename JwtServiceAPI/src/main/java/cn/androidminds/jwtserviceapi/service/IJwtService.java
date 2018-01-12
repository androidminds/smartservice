package cn.androidminds.jwtserviceapi.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IJwtService {
    @PostMapping("/auth/login")
    ResponseEntity<String> login(@RequestParam(value="identity") String identity,
                                 @RequestParam(value="password") String password);

    @PostMapping("/auth/refresh")
    ResponseEntity<String> refresh(@RequestParam(value = "old-token")String oldToken);

    @GetMapping("/auth/public-key")
    ResponseEntity<String> getPubKey();
}
