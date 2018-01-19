package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userservice.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class repositoryTest {
    @Autowired
    UserService userService;

    String name = "root";

    @Test
    public void getUser() {
        Optional<User> user = userService.getUserByName(name);
        assert(user.isPresent());
        assert(user.get().getName().equals(name));
    }
}
