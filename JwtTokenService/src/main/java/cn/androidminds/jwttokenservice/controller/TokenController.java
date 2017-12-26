package cn.androidminds.jwttokenservice.controller;


import cn.androidminds.jwttokenservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class TokenController {

    //@Value("${jwt.token.header}")
    private String tokenHeader = "token";

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody String identity, @RequestBody String password) throws Exception {
        final String token = "token-5555";//authService.login(identity, password);
        return ResponseEntity.ok(token);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestHeader("access-token") String token){
        authService.logout(token);
        return ResponseEntity.ok(true);
    }

    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(
            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshedToken = authService.refresh(token);
        if(refreshedToken == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(refreshedToken);
        }
    }

    @RequestMapping(value = "public-key", method = RequestMethod.GET)
    public ResponseEntity<?> verify(String token) {
        byte[] publicKey = authService.getPubKey();
        return ResponseEntity.ok(publicKey);
    }
}
