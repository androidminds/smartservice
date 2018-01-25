package cn.androidminds.jwttokenserviceapi.feign;


import cn.androidminds.jwttokenserviceapi.histrix.JwtTokenServiceHistrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "JwtService", fallback = JwtTokenServiceHistrix.class)
public interface JwtTokenServiceFeign {

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestParam(value = "identity") String identity,
                                 @RequestParam(value = "password") String password);

    @PostMapping("/refresh-token")
    ResponseEntity<String> refreshToken(@RequestHeader("userName") String userName,
                                        @RequestHeader("userId") String userId);
    //ResponseEntity<String> refresh(HttpServletRequest request);

    @GetMapping("/public-key")
    ResponseEntity<String> getPublicKey();

}
