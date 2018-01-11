package cn.androidminds.userservice.controller;

import cn.androidminds.commonapi.rest.StatusResponse;
import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.userservice.domain.Authority;
import cn.androidminds.userservice.domain.Role;
import cn.androidminds.userservice.domain.User;
import cn.androidminds.commonapi.rest.RestResponse;
import cn.androidminds.userservice.feign.JwtServiceProxy;
import cn.androidminds.userservice.service.UserService;
import cn.androidminds.userserviceapi.Error.ErrorCode;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@Transactional
public class UserController implements IUserService {

    @Autowired
    UserService userService;

    @Autowired
    private JwtServiceProxy jwtServiceProxy;

    @Value("${userservice.max-page-count}")
    int maxPageCount = 50;

    byte[] jwtPublicKey;

    private byte[] getJWTPublicKey() {
        if(jwtPublicKey == null){
            ResponseEntity<String> response = jwtServiceProxy.getPubKey();
            if(response != null && response.getBody() != null) {
                jwtPublicKey = response.getBody().getBytes();
            }
        }
        return jwtPublicKey;
    }

    public RestResponse<UserInfo> create(@RequestBody UserInfo userInfo,
                                         @RequestParam int role,
                                         @RequestParam String creator_token) {
        int error;
        RestResponse<UserInfo> result = new RestResponse<>();

        if(userInfo.getName() == null) {
            return result.setStatusCode(ErrorCode.ERR_PARAM_NAME_NULL);
        }

        if((error = Format.checkUserName(userInfo.getName())) != ErrorCode.OK)  {
            return result.setStatusCode(error);
        }

        if (userService.getUserByName(userInfo.getName()).isPresent()) {
            return result.setStatusCode(ErrorCode.ERR_PARAM_NAME_EXISTED);
        }

        if(userInfo.getPassword() == null) {
            return result.setStatusCode(ErrorCode.ERR_PARAM_PASSWORD_NULL);
        }

        if((error = Format.checkPassword(userInfo.getPassword())) != ErrorCode.OK)  {
            return result.setStatusCode(error);
        }

        if (userInfo.getEmail() != null) {
            if((error = Format.checkEmail(userInfo.getEmail())) != ErrorCode.OK)  {
                return result.setStatusCode(error);
            }
            if(userService.getUserByEmail(userInfo.getEmail()).isPresent()) {
                return result.setStatusCode(ErrorCode.ERR_PARAM_EMAIL_EXISTED);
            }
        }

        if (userInfo.getPhoneNumber() != null && userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
            if((error = Format.checkPhoneNumber(userInfo.getPhoneNumber())) != ErrorCode.OK)  {
                return result.setStatusCode(error);
            }
            if(userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
                return result.setStatusCode(ErrorCode.ERR_PARAM_PHONENUMBER_EXISTED);
            }
        }

        try {
            JwtInfo jwtInfo = JwtUtil.getJwtInfo(creator_token, getJWTPublicKey());
            Optional<User> creator = userService.getUserByName(jwtInfo.getUserName());
            if (!creator.isPresent()) return result.setStatusCode(ErrorCode.ERR_OPERATOR_NOT_ALLOWED);

            if(role == Role.ROOT
                    || (role == Role.ADMIN && !creator.get().hasAuthority(Authority.OP_CREATE_ADMIN_USER))
                    || (role == Role.NORMAL && !creator.get().hasAuthority(Authority.OP_CREATE_NORMAL_USER))) {
                return result.setStatusCode(ErrorCode.ERR_OPERATOR_NOT_ALLOWED);
            }
            User user = userService.createUser(new User(userInfo, jwtInfo.getUserName()), role);

            if(user.getId()> 0) {
                return result.setStatusCode(ErrorCode.OK).setData(user.getUserInfo());
            } else {
                return result.setStatusCode(ErrorCode.EXECUTE_ADD_USER_FAIL);
            }
        } catch (Exception e) {
            return result.setStatusCode(ErrorCode.ERR_OPERATOR_NOT_ALLOWED);
        }
    }

    public RestResponse<ArrayList<UserInfo>> list(@RequestParam(value = "start",required = true)long start,
                                                  @RequestParam(value = "count",required = true)int count,
                                                  @RequestParam String token) {
        RestResponse<ArrayList<UserInfo>> result = new RestResponse<>();
        if(start <= 0 || count <= 0) {
            return result.setStatusCode(ErrorCode.FAIL);
        }
        if(count > maxPageCount) {
            return result.setStatusCode(ErrorCode.PAGE_COUNT_EXCESS_LIMIT);
        }
        ArrayList<UserInfo> infoList = new ArrayList<>();
        Iterable<User> userList = userService.list(start, count);
        for(User user : userList) {
            infoList.add(user.getUserInfo());
        }
        return result.setStatusCode(ErrorCode.OK).setData(infoList);
    }

    public RestResponse<UserInfo> get(@PathVariable(value = "id",required = true)Long id,
                                      @RequestParam String token){
        RestResponse<UserInfo> result = new RestResponse<>();

        if(id <= 0) return result.setStatusCode(ErrorCode.ERR_PARAM_ID_NOT_EXISTED);

        Optional<User> user =  userService.getUserById(id);
        return user.map(u->{ return result.setStatusCode(ErrorCode.OK)
                .setData(u.getUserInfo());})
                .orElse(result.setStatusCode(ErrorCode.FAIL));
    }

    public StatusResponse modify(@RequestBody UserInfo userInfo,
                                 @RequestParam String token) {
        int error;
        StatusResponse result = new StatusResponse();
        boolean modifyPass = false;

        if(userInfo.getName() == null) {
            return result.setStatusCode(ErrorCode.ERR_PARAM_NAME_NULL);
        }

        Optional<User> user = userService.getUserByName(userInfo.getName());
        if(!user.isPresent()) {
            return result.setStatusCode(ErrorCode.ERR_PARAM_NAME_NOT_EXISTED);
        }

        if(userInfo.getPassword() != null) {
            if((error = Format.checkPassword(userInfo.getPassword())) != ErrorCode.OK) {
                return result.setStatusCode(error);
            } else {
                user.get().setPassword(userInfo.getPassword());
                modifyPass = true;
            }
        }

        if (userInfo.getEmail() != null && !userInfo.getEmail().equalsIgnoreCase(user.get().getEmail())) {
            if((error = Format.checkEmail(userInfo.getEmail())) != ErrorCode.OK)  {
                return result.setStatusCode(error);
            }

            try {
                if (userService.getUserByEmail(userInfo.getEmail()).isPresent()) {
                    return result.setStatusCode(ErrorCode.ERR_PARAM_EMAIL_EXISTED);
                }
            } catch(Exception e) {

            }
            user.get().setEmail(userInfo.getEmail());
        }

        if (userInfo.getPhoneNumber() != null && !userInfo.getPhoneNumber().equalsIgnoreCase(user.get().getPhoneNumber()) ) {
            if((error = Format.checkPhoneNumber(userInfo.getPhoneNumber())) != ErrorCode.OK)  {
                return result.setStatusCode(error);
            }
            try {
                if (userService.getUserByPhoneNumber(userInfo.getPhoneNumber()).isPresent()) {
                    return result.setStatusCode(ErrorCode.ERR_PARAM_PHONENUMBER_EXISTED);
                }
            }catch(Exception e) {

            }
            user.get().setPhoneNumber(userInfo.getPhoneNumber());
        }
        return result.setStatusCode(userService.modify(user.get(), modifyPass)?ErrorCode.OK:ErrorCode.FAIL);
    }

    public StatusResponse delete(@PathVariable(value = "id",required = true)Long id,
                                 @RequestParam String token) {
        StatusResponse result = new StatusResponse();
        if(id < 0) return result.setStatusCode(ErrorCode.ERR_PARAM_ID_NOT_EXISTED);
        if(userService.getUserById(id) != null) {
            userService.delete(id);
            return result.setStatusCode(ErrorCode.OK);
        } else {
            return result.setStatusCode(ErrorCode.EXCCUTE_DELETE_FAIL);
        }
    }

    public RestResponse<UserInfo> verify(@RequestParam(value = "identity",required = true)String identity,
                          @RequestParam(value = "password",required = true)String password)  {
        RestResponse<UserInfo> result = new RestResponse<>();
        if(userService.verify(identity, password)) {
            Optional<User> user = userService.getUserByIdentity(identity);
            if(user.isPresent()) {
                return result.setStatusCode(ErrorCode.OK).setData(user.get().getUserInfo());
            }
        }
        return result.setStatusCode(ErrorCode.FAIL);
    }
}
