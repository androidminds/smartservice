package cn.androidminds.commonapi.jwt;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class JwtInfo implements Serializable {
    private String userName;
    private String userId;
    private Date expireDate;
}
