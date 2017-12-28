package cn.androidminds.userserviceapi.service;


import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserService {
    @RequestMapping(value = "/user/info",method = RequestMethod.GET)
    UserInfo getInfo(@RequestParam(value = "identity")String identity);
    @RequestMapping(value = "/user/verify",method = RequestMethod.GET)
    boolean verify(@RequestParam(value = "identity")String identity,
                   @RequestParam(value = "password")String password);
}
