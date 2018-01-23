package cn.androidminds.zuulservice.feign;

import cn.androidminds.jwtservice.service.IJwtService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "JwtService")
public interface JwtServiceProxy extends IJwtService {


}
