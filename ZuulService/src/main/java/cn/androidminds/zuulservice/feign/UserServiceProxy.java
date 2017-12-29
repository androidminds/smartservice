package cn.androidminds.zuulservice.feign;

import cn.androidminds.userserviceapi.service.IUserService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "UserService")
public interface UserServiceProxy extends IUserService {
}
