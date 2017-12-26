package cn.androidminds.userservice.repository;


import cn.androidminds.userservice.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthotityRepository extends JpaRepository<Authority, Long> {
}
