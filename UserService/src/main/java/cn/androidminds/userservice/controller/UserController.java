package cn.androidminds.userservice.controller;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    @RequestMapping(value = "/info/{identity}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInfo(@PathVariable("identity") String identity) throws Exception {
        User user = userRepository.findOneByName(identity);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @RequestMapping(value = "verify", method = RequestMethod.GET)
    public ResponseEntity<?> verify(String identity, String password) throws Exception {
        User user = userRepository.findOneByNameOrEmailOrPhoneNumber(identity, identity, identity);

        if (user != null) {
            if(encoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok(null);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}
