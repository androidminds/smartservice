package cn.androidminds.jwtserviceapi;

import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class JwtInfo implements Serializable {
    private String identity;
    private String userName;
}
