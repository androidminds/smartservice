package cn.androidminds.userservice.feign;

import cn.androidminds.jwtserviceapi.service.IJwtService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "JwtService")
public interface JwtServiceProxy extends IJwtService {
}
