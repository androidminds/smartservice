package cn.androidminds.userserviceapi.service;


import cn.androidminds.commonapi.rest.RestResponse;
import cn.androidminds.commonapi.rest.StatusResponse;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

public interface IUserService {
    @PostMapping("/users")
    RestResponse<UserInfo> create(@RequestBody UserInfo userInfo,
                                  @RequestParam(value = "role", required = true) int role,
                                  @RequestParam(value = "creator_token", required = true) String creator_token);

    @GetMapping("/users")
    RestResponse<ArrayList<UserInfo>> list(@RequestParam(value = "start",required = true)long start,
                                           @RequestParam(value = "count",required = true)int count,
                                           @RequestParam(value = "creator_token", required = true) String creator_token);

    @GetMapping("/users/{id}")
    RestResponse<UserInfo> get(@PathVariable(value = "id",required = true)Long id,
                               @RequestParam(value = "creator_token", required = true) String creator_token);

    @PutMapping("/users/{id}")
    StatusResponse modify(@RequestBody UserInfo userInfo,
                          @RequestParam(value = "creator_token", required = true) String creator_token);

    @DeleteMapping("/users/{id}")
    StatusResponse delete(@PathVariable(value = "id",required = true)Long id,
                          @RequestParam(value = "creator_token", required = true) String creator_token);

    @GetMapping("/verify")
    RestResponse<UserInfo> verify(@RequestParam(value = "identity",required = true)String identity,
                @RequestParam(value = "password",required = true) String password);
}
