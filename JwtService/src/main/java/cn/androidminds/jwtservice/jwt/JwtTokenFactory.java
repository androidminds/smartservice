package cn.androidminds.jwtservice.jwt;

import cn.androidminds.jwtserviceapi.JwtInfo;
import cn.androidminds.jwtserviceapi.JwtUtil;
import cn.androidminds.jwtserviceapi.RsaKeyUtil;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
public class JwtTokenFactory {
    private byte[] privateKey;
    private byte[] publicKey;

    @Value("${jwt.token.expire}")
    private int tokenExpire; //unit is minute

    @Value("${jwt.password}")
    private String password = "Ladjfdalfd#adf3";

    public JwtTokenFactory() {
        generateKeyPair();
    }
    public String generateToken(UserInfo userInfo) {
        try {
            JwtInfo jwtInfo = new JwtInfo(null, userInfo.getName());
            return JwtUtil.generateToken(jwtInfo, tokenExpire*60, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void generateKeyPair() {
        try {
            List<byte[]> list = RsaKeyUtil.generateKeyPair(password);
            privateKey = list.get(0);
            publicKey = list.get(1);
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
