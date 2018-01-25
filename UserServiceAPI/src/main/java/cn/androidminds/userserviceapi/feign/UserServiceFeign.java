package cn.androidminds.userserviceapi.feign;

import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.Hystrix.UserServiceHystric;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "UserService", fallback = UserServiceHystric.class)
public interface UserServiceFeign {
    @PostMapping("/user")
    ResponseEntity<UserInfo> create(@RequestBody UserInfo userInfo);

    @DeleteMapping("/user/{id}")
    ResponseEntity<String> delete(@PathVariable(value = "id")Long id);

    @PutMapping("/user/{id}")
    ResponseEntity<Integer> modify(@RequestBody UserInfo userInfo);

    @GetMapping("/user/{id}")
    ResponseEntity<UserInfo> get(@PathVariable(value = "id")Long id);

    @GetMapping("/users")
    ResponseEntity<UserInfo[]> list(@RequestParam(value = "page")long page,
                                    @RequestParam(value = "page-count")int count);

    @GetMapping("/user/verify")
    ResponseEntity<UserInfo> verify(@RequestParam(value = "identity")String identity,
                                    @RequestParam(value = "password")String password);
}
