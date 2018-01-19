package cn.androidminds.zuulservice.controller;

import cn.androidminds.zuulservice.feign.JwtServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Qualifier("JwtServiceFeignClient")
    @Autowired
    JwtServiceProxy jwtServiceProxy;

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    boolean logout(String token) {
        return false;
    }
}
