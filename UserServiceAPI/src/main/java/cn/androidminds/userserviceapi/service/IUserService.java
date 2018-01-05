package cn.androidminds.userserviceapi.service;


import cn.androidminds.commonapi.rest.RestResponse;
import cn.androidminds.commonapi.rest.StatusResponse;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

public interface IUserService {
    @PostMapping("/users")
    RestResponse<UserInfo> create(@RequestBody UserInfo userInfo);

    @GetMapping("/users")
    RestResponse<ArrayList<UserInfo>> list(@RequestParam(value = "start",required = true)long start,
                                           @RequestParam(value = "count",required = true)int count);

    @GetMapping("/users/{id}")
    RestResponse<UserInfo> get(@PathVariable(value = "id",required = true)Long id);

    @PutMapping("/users/{id}")
    StatusResponse modify(@RequestBody UserInfo userInfo);

    @DeleteMapping("/users/{id}")
    StatusResponse delete(@PathVariable(value = "id",required = true)Long id);

    @GetMapping("/verify")
    StatusResponse verify(@RequestParam(value = "identity",required = true)String identity,
                @RequestParam(value = "password",required = true)String password);
}
