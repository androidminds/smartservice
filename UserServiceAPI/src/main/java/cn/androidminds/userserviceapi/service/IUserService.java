package cn.androidminds.userserviceapi.service;


import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

public interface IUserService {
    @PostMapping("/users")
    ResponseEntity<UserInfo> create(@RequestBody UserInfo userInfo);

    @GetMapping("/users")
    ResponseEntity<ArrayList<UserInfo>> list(@RequestParam(value = "start")long start,
                                             @RequestParam(value = "count")int count,
                                             @RequestParam(value = "creator-token")String creatorToken);

    @GetMapping("/users/{id}")
    ResponseEntity<UserInfo> get(@PathVariable(value = "id")Long id,
                                 @RequestParam(value = "creator-token")String creatorToken);

    @PutMapping("/users/{id}")
    ResponseEntity<Integer> modify(@RequestBody UserInfo userInfo);

    @DeleteMapping("/users/{id}")
    boolean delete(@PathVariable(value = "id")Long id,
                   @RequestParam(value = "creator-token")String creatorToken);

    @GetMapping("/verify")
    ResponseEntity<UserInfo> verify(@RequestParam(value = "identity")String identity,
                                    @RequestParam(value = "password")String password);
}
