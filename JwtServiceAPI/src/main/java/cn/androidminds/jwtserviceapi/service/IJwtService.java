package cn.androidminds.jwtserviceapi.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface IJwtService {
    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    String login(@RequestParam(value = "identity") String identity,
                 @RequestParam(value = "password") String password);

    @RequestMapping(value = "/auth/refresh", method = RequestMethod.POST)
    String refresh(@RequestParam(value = "old-token")String oldToken);

    @RequestMapping(value = "/auth/public-key", method = RequestMethod.GET)
    String getPubKey();
}
