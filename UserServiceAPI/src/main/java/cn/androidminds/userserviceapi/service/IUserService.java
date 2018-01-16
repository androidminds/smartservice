package cn.androidminds.userserviceapi.service;


import cn.androidminds.userserviceapi.domain.UserInfo;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


public interface IUserService {
    @PostMapping("/users")
    ResponseEntity<UserInfo> create(@RequestBody UserInfo userInfo);

    @GetMapping("/users")
    ResponseEntity<UserInfo[]> list(@RequestParam(value = "page")long page,
                                    @RequestParam(value = "page-count")int count);

    @GetMapping("/users/{id}")
    ResponseEntity<UserInfo> get(@PathVariable(value = "id")Long id);

    @PutMapping("/users/{id}")
    ResponseEntity<Integer> modify(@RequestBody UserInfo userInfo);

    @DeleteMapping("/users/{id}")
    ResponseEntity<String> delete(@PathVariable(value = "id")Long id);

    @GetMapping("/verify")
    ResponseEntity<UserInfo> verify(@RequestParam(value = "identity")String identity,
                                    @RequestParam(value = "password")String password);
}
