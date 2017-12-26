package cn.androidminds.userservice.repository;


import cn.androidminds.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByName(String name);
    User findOneByEmail(String email);
    User findOneByPhoneNumber(String phoneNumber);
    User findOneByNameOrEmailOrPhoneNumber(String name, String email, String phoneNumber);
    //Long save(User user);
}
