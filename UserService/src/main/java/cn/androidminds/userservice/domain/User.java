package cn.androidminds.userservice.domain;


import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.domain.UserState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class User /*extends AuditRBAC*/{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    @Size(min = 60, max = 60)
    @Column(length = 60)
    @JsonIgnore
    private String password;

    @Size(min = 5, max = 64)
    @Column(length = 64, unique = true)
    private String email;

    @Size(min = 11, max = 32)
    @Column(name="phone_number", length = 32, unique = true)
    private String phoneNumber;

    private int state;

    @JsonIgnore
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @Transient
    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        if(authorities == null) {
            Set<Authority> allAuthorities = new HashSet<>();
            for (Role role : roles) {
                for (Authority authority : role.getAuthorities()) {
                    allAuthorities.add(authority);
                }
            }
            if(allAuthorities.size() > 0) authorities = allAuthorities;
        }
        return authorities;
    }

    public boolean hasAuthority(int value) {
        getAuthorities();
        if(authorities != null) {
            for(Authority authority: authorities) {
                if(authority.getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public User(UserInfo userInfo) {
        id = userInfo.getId();
        password = userInfo.getPassword();
        name = userInfo.getName();
        email = userInfo.getEmail();
        phoneNumber = userInfo.getPhoneNumber();
        state = userInfo.getState();
        //setCreatedBy(creator);
    }

    public UserInfo getUserInfo() {
        return new UserInfo(id, name, null, email, phoneNumber, -1,state, null);
    }
}
