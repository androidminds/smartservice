package cn.androidminds.userservice.service;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.repository.UserRepository;
import cn.androidminds.userserviceapi.service.IUserService;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserService implements IUserService{

    @Autowired
    UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    public UserInfo getInfo(@PathVariable("identity") String identity) {
        User user = userRepository.findOneByName(identity);

        if (user != null) {
            return new UserInfo(user.getName());
        }
        return null;
    }

    public boolean verify(String identity, String password)  {
        User user = userRepository.findOneByNameOrEmailOrPhoneNumber(identity, identity, identity);

        if (user != null) {
            if(encoder.matches(password, user.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
