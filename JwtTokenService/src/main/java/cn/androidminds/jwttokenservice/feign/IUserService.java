package cn.androidminds.jwttokenservice.feign;


import cn.androidminds.jwtcommon.JwtInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "UserService")
public interface IUserService {
  @RequestMapping(value = "/user/info",method = RequestMethod.GET)
  JwtInfo getInfo(@RequestParam(value = "identity")String identity);
  @RequestMapping(value = "/user/verify",method = RequestMethod.GET)
  boolean verify(@RequestParam(value = "identity")String identity,
                 @RequestParam(value = "password")String password);
}
