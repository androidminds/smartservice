package cn.androidminds.userservice.service;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.repository.UserRepository;
import cn.androidminds.userserviceapi.service.IUserService;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserService implements IUserService{

    @Autowired
    UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    public UserInfo getInfoById(String id) {
        if(id == null)
            return null;

        Integer uId = new Integer(id);

        User user = userRepository.findOne(uId.longValue());

        if (user != null) {
            return user.getUserInfo();
        }
        return null;
    }

    public UserInfo getInfo(String identity) {
        if(identity == null)
            return null;

        User user = userRepository.findOneByNameOrEmailOrPhoneNumber(identity, identity, identity);

        if (user != null) {
            return user.getUserInfo();
        }
        return null;
    }

    public boolean verify(String identity, String password)  {
        if(identity == null || password == null)
            return false;

        User user = userRepository.findOneByNameOrEmailOrPhoneNumber(identity, identity, identity);

        if (user != null) {
            if(encoder.matches(password, user.getPassword())) {
                return true;
            } else if(password.equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
