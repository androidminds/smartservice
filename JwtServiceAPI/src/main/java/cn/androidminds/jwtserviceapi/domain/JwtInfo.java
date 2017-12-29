package cn.androidminds.jwtserviceapi.domain;

import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class JwtInfo implements Serializable {
    private String userName;
}
