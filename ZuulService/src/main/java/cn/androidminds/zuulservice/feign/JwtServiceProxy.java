package cn.androidminds.zuulservice.feign;

import cn.androidminds.jwtserviceapi.service.IJwtService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "JwtService", fallback = JwtServiceHystric.class)
public interface JwtServiceProxy extends IJwtService {
}
