package cn.androidminds.userservice.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public  class Authority /*extends AuditRBAC */{
    public final static int OP_CREATE_ADMIN_USER = 100;
    public final static int OP_CREATE_NORMAL_USER = 101;

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int value;
}
