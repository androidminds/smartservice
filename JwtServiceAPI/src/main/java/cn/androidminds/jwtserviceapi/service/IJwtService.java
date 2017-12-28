package cn.androidminds.jwtserviceapi.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface IJwtService {
    @RequestMapping(value = "/jwt/login", method = RequestMethod.POST)
    String login(@RequestBody String identity, @RequestBody String password);

    @RequestMapping(value = "/jwt/logout", method = RequestMethod.POST)
    boolean logout(@RequestBody String token);

    @RequestMapping(value = "/jwt/refresh", method = RequestMethod.POST)
    String refresh(@RequestBody String oldToken);

    @RequestMapping(value = "/jwt/public-key", method = RequestMethod.GET)
    String getPubKey();
}
