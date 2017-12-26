package cn.androidminds.userservice.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public  class Authority /*extends AuditRBAC*/ {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String value;
}
