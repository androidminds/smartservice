package cn.androidminds.userserviceapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    Long id;
    String password;
    String name;
    String email;
    String phoneNumber;
}
