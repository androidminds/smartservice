package cn.androidminds.zuulservice.feign;

import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(value = "UserService")
public interface UserServiceProxy {
}
