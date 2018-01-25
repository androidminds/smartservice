package cn.androidminds.jwttokenservice.service;


import cn.androidminds.commonapi.Exception.ExceptionOutput;
import cn.androidminds.commonapi.jwt.JwtInfo;
import cn.androidminds.jwttokenservice.jwt.JwtTokenFactory;
import cn.androidminds.jwttokenserviceapi.feign.JwtTokenServiceFeign;
import cn.androidminds.permission.annotation.OpenAPI;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.feign.UserServiceFeign;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.spec.InvalidKeySpecException;

@Api(value = "API - JwtTokenService", description = "登录")
@RestController
public class JwtTokenService implements JwtTokenServiceFeign {
    private Logger logger = LoggerFactory.getLogger(JwtTokenService.class);

    @Autowired
    UserServiceFeign userServiceFeign;

    @Autowired
    JwtTokenFactory tokenFactory;

    @ApiOperation("登录")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "identity", value = "", required = true, dataType = "String"),
                    @ApiImplicitParam(name = "password", value = "", required = true, dataType = "String"),
            }
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Successful — 请求已完成"),
                    @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),

            }
    )
    @OpenAPI(permission = false)
    public ResponseEntity<String> login(@RequestParam(value = "identity") String identity,
                                        @RequestParam(value = "password") String password) {
        ResponseEntity<UserInfo> response = userServiceFeign.verify(identity, password);
        if (response != null && response.getStatusCode() == HttpStatus.OK
                && response.getBody() != null) {
            try {
                UserInfo userInfo = response.getBody();
                JwtInfo jwtInfo = new JwtInfo(userInfo.getName(),
                        userInfo.getId().toString(), null);
                return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
            } catch (InvalidKeySpecException e) {
                logger.error(ExceptionOutput.toString(e));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        return ResponseEntity.badRequest().body("");
    }

    @ApiOperation("更新Token")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userName", value = "", required = true, dataType = "String"),
                    @ApiImplicitParam(name = "userId", value = "", required = true, dataType = "String"),
            }
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Successful — 请求已完成"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 500, message = "服务器发生内部错误"),
            }
    )
    @OpenAPI
    public ResponseEntity<String> refreshToken(@RequestHeader("userName")String userName,
                                          @RequestHeader("userId")String userId) {
        try {
            if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                JwtInfo jwtInfo = new JwtInfo(userName, userId, null);
                return ResponseEntity.ok(tokenFactory.generateToken(jwtInfo));
            }
        } catch (Exception e) {
            logger.error(ExceptionOutput.toString(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @ApiOperation("获取公钥")
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Successful — 请求已完成"),
                    @ApiResponse(code = 500, message = "服务器发生内部错误"),
            }
    )
    public ResponseEntity<String> getPublicKey() {
        try {
            return ResponseEntity.ok(Base64Utils.encodeToString(tokenFactory.getPublicKey()));
        } catch (Exception e) {
                logger.error(ExceptionOutput.toString(e));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
