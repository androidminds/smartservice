package cn.androidminds.userservice.service;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.repository.UserRepository;
import cn.androidminds.userserviceapi.service.IUserService;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Iterable<User> list(long start, int count) {
        ArrayList<Long> idList = new ArrayList<>();
        for(long i = start; i < start + count; i++) {
            idList.add(i);
        }
        return userRepository.findAll(idList);
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findOne(id.longValue());
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findOneByName(name);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findOneByPhoneNumber(phoneNumber);
    }

    public boolean modify(User user, boolean modifyPass) {
        if(modifyPass && user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        user = userRepository.saveAndFlush(user);
        return user.getId() > 0;
    }

    public void delete(long id) {
        userRepository.delete(new Long(id));
    }

    public boolean verify(String identity, String password)  {
        Optional<User> user = userRepository.findOneByNameOrEmailOrPhoneNumber(identity, identity, identity);
        return user.map(u->{return (encoder.matches(password, u.getPassword()) ||
                password.equals(u.getPassword()));})
                .orElse(false);
    }

}
