package cn.androidminds.userservice.repository;


import cn.androidminds.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findOneByName(String name);
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByPhoneNumber(String phoneNumber);
    Optional<User> findOneByNameOrEmailOrPhoneNumber(String name, String email, String phoneNumber);
    Optional<User> findOne(long id);
    //Long save(User user);
    Long saveAndFlush(User user);
}
