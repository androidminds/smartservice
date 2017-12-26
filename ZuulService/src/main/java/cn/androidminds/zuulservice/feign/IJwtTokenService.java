package cn.androidminds.zuulservice.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "JwtTokenService")
public interface IJwtTokenService {
    @RequestMapping(value = "/jwt/public-key", method = RequestMethod.GET)
    String getPubKey();
}
