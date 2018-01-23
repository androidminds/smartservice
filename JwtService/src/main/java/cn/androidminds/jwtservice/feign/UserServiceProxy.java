package cn.androidminds.jwtservice.feign;

import cn.androidminds.userserviceapi.service.IUserService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "UserService", fallback = UserServiceHystric.class)
public interface UserServiceProxy extends IUserService {
}
