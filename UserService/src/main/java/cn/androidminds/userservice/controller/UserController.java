package cn.androidminds.userservice.controller;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.userservice.domain.Authority;
import cn.androidminds.userservice.domain.Role;
import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.feign.JwtServiceProxy;
import cn.androidminds.userservice.service.UserService;
import cn.androidminds.userserviceapi.Error.ErrorCode;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

@RestController
@Transactional
public class UserController implements IUserService {

    @Autowired
    UserService userService;

    @Autowired
    private JwtServiceProxy jwtServiceProxy;

    @Value("${userservice.max-page-count:100}")
    int maxPageCount;

    byte[] jwtPublicKey;

    private byte[] getJWTPublicKey() {
        if(jwtPublicKey == null){
            ResponseEntity<String> response = jwtServiceProxy.getPubKey();
            if(response != null && response.getBody() != null) {
                jwtPublicKey = Base64Utils.decodeFromString(response.getBody());
            }
        }
        return jwtPublicKey;
    }

    public ResponseEntity<UserInfo> create(@RequestBody UserInfo userInfo) {
        int error;

        if(userInfo.getName() == null) {
            return ResponseEntity.status(ErrorCode.ERR_PARAM_NAME_NULL).build();
        }

        if((error = Format.checkUserName(userInfo.getName())) != 0)  {
            return ResponseEntity.status(error).build();
        }

        if (userService.getUserByName(userInfo.getName()).isPresent()) {
            return ResponseEntity.status(ErrorCode.ERR_PARAM_NAME_EXISTED).build();
        }

        if(userInfo.getPassword() == null) {
            return ResponseEntity.status(ErrorCode.ERR_PARAM_PASSWORD_NULL).build();
        }

        if((error = Format.checkPassword(userInfo.getPassword())) != 0)  {
            return ResponseEntity.status(error).build();
        }

        if (userInfo.getEmail() != null) {
            if((error = Format.checkEmail(userInfo.getEmail())) != 0)  {
                return ResponseEntity.status(error).build();
            }
            if(userService.getUserByEmail(userInfo.getEmail()).isPresent()) {
                return ResponseEntity.status(ErrorCode.ERR_PARAM_EMAIL_EXISTED).build();
            }
        }

        if (userInfo.getPhoneNumber() != null && userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
            if((error = Format.checkPhoneNumber(userInfo.getPhoneNumber())) != 0)  {
                return ResponseEntity.status(error).build();
            }
            if(userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
                return ResponseEntity.status(ErrorCode.ERR_PARAM_PHONENUMBER_EXISTED).build();
            }
        }

        try {
            JwtInfo jwtInfo = JwtUtil.getJwtInfo(userInfo.getCreatorToken(), getJWTPublicKey());
            Optional<User> creator = userService.getUserByName(jwtInfo.getUserName());
            if (!creator.isPresent()) return ResponseEntity.badRequest().build();

            if(userInfo.getRole() == Role.ROOT
                    || (userInfo.getRole() == Role.ADMIN && !creator.get().hasAuthority(Authority.OP_CREATE_ADMIN_USER))
                    || (userInfo.getRole() == Role.NORMAL && !creator.get().hasAuthority(Authority.OP_CREATE_NORMAL_USER))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userService.createUser(new User(userInfo, jwtInfo.getUserName()),userInfo.getRole());

            if(user.getId()> 0) {
                return ResponseEntity.ok(user.getUserInfo());
            } else {
                return ResponseEntity.status(ErrorCode.EXECUTE_ADD_USER_FAIL).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<UserInfo[]> list(@RequestParam(value = "start")long start,
                                           @RequestParam(value = "count")int count,
                                           @RequestHeader(value = "Authentication")String token) {
        if(start <= 0 || count <= 0 || count > maxPageCount) {
            return ResponseEntity.badRequest().build();
        }

        Iterable<User> userList = userService.list(start, count);
        UserInfo[] arrayInfo = new UserInfo[count];
        int i = 0;
        for(User user : userList) {
            arrayInfo[i++] = (user.getUserInfo());
        }
        return ResponseEntity.ok(arrayInfo);
    }

    public ResponseEntity<UserInfo> get(@PathVariable(value = "id",required = true)Long id,
                                      @RequestParam String token){
         if(id <= 0) return ResponseEntity.status(ErrorCode.ERR_PARAM_ID_NOT_EXISTED).build();

        Optional<User> user =  userService.getUserById(id);
        return user.map(u->{ return ResponseEntity.ok(u.getUserInfo());})
                .orElse(ResponseEntity.badRequest().build());
    }

    public ResponseEntity<Integer> modify(@RequestBody UserInfo userInfo) {
        int error;
        boolean modifyPass = false;

        if(userInfo.getName() == null) {
            return ResponseEntity.status(ErrorCode.ERR_PARAM_NAME_NULL).build();
        }

        Optional<User> user = userService.getUserByName(userInfo.getName());
        if(!user.isPresent()) {
            return ResponseEntity.status(ErrorCode.ERR_PARAM_NAME_NOT_EXISTED).build();
        }

        if(userInfo.getPassword() != null) {
            if((error = Format.checkPassword(userInfo.getPassword())) != 0) {
                return ResponseEntity.status(error).build();
            } else {
                user.get().setPassword(userInfo.getPassword());
                modifyPass = true;
            }
        }

        if (userInfo.getEmail() != null && !userInfo.getEmail().equalsIgnoreCase(user.get().getEmail())) {
            if((error = Format.checkEmail(userInfo.getEmail())) != 0)  {
                return ResponseEntity.status(error).build();
            }

            try {
                if (userService.getUserByEmail(userInfo.getEmail()).isPresent()) {
                    return ResponseEntity.status(ErrorCode.ERR_PARAM_EMAIL_EXISTED).build();
                }
            } catch(Exception e) {

            }
            user.get().setEmail(userInfo.getEmail());
        }

        if (userInfo.getPhoneNumber() != null && !userInfo.getPhoneNumber().equalsIgnoreCase(user.get().getPhoneNumber()) ) {
            if((error = Format.checkPhoneNumber(userInfo.getPhoneNumber())) != 0)  {
                return ResponseEntity.status(error).build();
            }
            try {
                if (userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
                    return ResponseEntity.status(ErrorCode.ERR_PARAM_PHONENUMBER_EXISTED).build();
                }
            }catch(Exception e) {

            }
            user.get().setPhoneNumber(userInfo.getPhoneNumber());
        }
        return userService.modify(user.get(), modifyPass)?ResponseEntity.ok(0):ResponseEntity.badRequest().build();
    }

    public boolean delete(@PathVariable(value = "id",required = true)Long id,
                                 @RequestParam String token) {
        if(id < 0) return false;
        if(userService.getUserById(id) != null) {
            userService.delete(id);
            return true;
        } else {
            return true;
        }
    }

    public ResponseEntity<UserInfo> verify(@RequestParam(value = "identity",required = true)String identity,
                          @RequestParam(value = "password",required = true)String password)  {
        if(userService.verify(identity, password)) {
            Optional<User> user = userService.getUserByIdentity(identity);
            if(user.isPresent()) {
                return ResponseEntity.ok(user.get().getUserInfo());
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
