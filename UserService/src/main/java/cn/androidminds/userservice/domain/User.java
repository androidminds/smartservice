package cn.androidminds.userservice.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class User /*extends AuditRBAC*/ implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 1, max = 64)
    private String name;

    @JsonIgnore
    @NotNull
    @Size(min = 64, max = 64)
    @Column(length = 64)
    private String password;

    @Email
    @Size(min = 5, max = 64)
    @Column(length = 64, unique = true)
    private String email;

    @Size(min = 11, max = 32)
    @Column(name="phone_number", length = 32, unique = true)
    private String phoneNumber;

    @JsonIgnore
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
}
