package cn.androidminds.jwtservice.jwt;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.jwtserviceapi.util.RsaKeyUtil;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
@Service
public class JwtTokenFactory {
    private byte[] privateKey;
    private byte[] publicKey;

    @Value("${jwt.token.expire:1440}")
    private int tokenExpire; //unit is minute

    @Value("${jwt.password:$@23sdf}")
    private String password;

    public String generateToken(UserInfo userInfo) throws Exception {
        if(publicKey == null) {
            generateKeyPair();
        }
        JwtInfo jwtInfo = new JwtInfo(userInfo.getName());
        return JwtUtil.generateToken(jwtInfo, tokenExpire*60, privateKey);
    }

    public String generateToken(JwtInfo jwtInfo) throws Exception {
        if(publicKey == null) {
            generateKeyPair();
        }
        return JwtUtil.generateToken(jwtInfo, tokenExpire*60, privateKey);
    }

    private void generateKeyPair() {
        try {
            List<byte[]> list = RsaKeyUtil.generateKeyPair(password);
            privateKey = list.get(0);
            publicKey = list.get(1);
        } catch (java.security.NoSuchAlgorithmException e) {

        }
    }

    public byte[] getPublicKey() {
        if(publicKey == null) {
            generateKeyPair();
        }
        return publicKey;
    }
}
