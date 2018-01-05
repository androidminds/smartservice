package cn.androidminds.userservice.domain;


import cn.androidminds.userserviceapi.domain.UserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class User /*extends AuditRBAC*/{
    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    @Size(min = 60, max = 60)
    @Column(length = 60)
    private String password;

    @Email
    @Size(min = 5, max = 64)
    @Column(length = 64, unique = true)
    private String email;

    @Size(min = 11, max = 32)
    @Column(name="phone_number", length = 32, unique = true)
    private String phoneNumber;

    @Transient
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    private Set<Role> roles = new HashSet<>();

    @Transient
    private Set<GrantedAuthority> authorities = new HashSet<>();

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> allAuthorities = new HashSet<>();
        for(Role role : roles){
            for(Authority authority : role.getAuthorities()){
                allAuthorities.add(new SimpleGrantedAuthority(authority.getValue()));
            }
        }
        return allAuthorities;
    }

    public User(UserInfo userInfo) {
        id = userInfo.getId();
        password = userInfo.getPassword();
        name = userInfo.getName();
        email = userInfo.getEmail();
        phoneNumber = userInfo.getPhoneNumber();
    }

    public UserInfo getUserInfo() {
        return new UserInfo(id, null, name, email, phoneNumber);
    }
}
