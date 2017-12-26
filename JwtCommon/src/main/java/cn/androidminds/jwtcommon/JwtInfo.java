package cn.androidminds.jwtcommon;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class JwtInfo implements Serializable {
    private String identity;
    private String userName;
}
