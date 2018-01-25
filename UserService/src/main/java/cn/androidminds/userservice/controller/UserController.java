package cn.androidminds.userservice.controller;

import cn.androidminds.userservice.domain.Authority;
import cn.androidminds.userservice.domain.Role;
import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.service.UserService;
import cn.androidminds.userserviceapi.Error.ErrorCode;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Transactional
public class UserController implements UserServiceFeign {

    @Autowired
    UserService userService;

    @Value("${userservice.max-page-count:100}")
    int maxPageCount;

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
            Optional<User> creator = userService.getUserByName(userInfo.getOperator());
            if (!creator.isPresent()) return ResponseEntity.badRequest().build();

            if(userInfo.getRole() == Role.ROOT
                    || (userInfo.getRole() == Role.ADMIN && !creator.get().hasAuthority(Authority.OP_CREATE_ADMIN_USER))
                    || (userInfo.getRole() == Role.NORMAL && !creator.get().hasAuthority(Authority.OP_CREATE_NORMAL_USER))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = userService.createUser(new User(userInfo),userInfo.getRole());

            if(user.getId()> 0) {
                return ResponseEntity.ok(user.getUserInfo());
            } else {
                return ResponseEntity.status(ErrorCode.EXECUTE_ADD_USER_FAIL).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<UserInfo[]> list(@RequestParam(value = "page")long page,
                                           @RequestParam(value = "page-count")int count) {
        if(page < 0 || count <= 0 || count > maxPageCount) {
            return ResponseEntity.badRequest().build();
        }

        List<User> userList = userService.list(page*count, count);
        if(userList.size() > 0) {
            UserInfo[] arrayInfo = new UserInfo[userList.size()];
            for (int i = 0; i < userList.size(); i++) {
                arrayInfo[i] = userList.get(i).getUserInfo();
            }
            return ResponseEntity.ok(arrayInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseEntity<UserInfo> get(@PathVariable(value = "id",required = true)Long id){
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

    public ResponseEntity<String> delete(@PathVariable(value = "id",required = true)Long id) {

        if(id <= 0) return ResponseEntity.badRequest().build();

        if(userService.getUserById(id) != null) {
            if(userService.delete(id)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.badRequest().build();
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
