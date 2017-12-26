package cn.androidminds.userservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Role /*extends AuditRBAC*/ {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @JsonIgnore
    @ManyToMany(targetEntity = Authority.class,fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    private Set<Authority> authorities = new HashSet<>();
}
