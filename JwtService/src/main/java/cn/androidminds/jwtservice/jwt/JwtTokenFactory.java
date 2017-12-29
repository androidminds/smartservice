package cn.androidminds.jwtservice.jwt;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.jwtserviceapi.util.JwtUtil;
import cn.androidminds.jwtserviceapi.util.RsaKeyUtil;
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

    public JwtTokenFactory() throws NoSuchAlgorithmException {
        generateKeyPair();
    }

    public String generateToken(UserInfo userInfo) throws Exception {
        JwtInfo jwtInfo = new JwtInfo(userInfo.getName());
        return JwtUtil.generateToken(jwtInfo, tokenExpire*60, privateKey);
    }

    public String generateToken(JwtInfo jwtInfo) throws Exception {
        return JwtUtil.generateToken(jwtInfo, tokenExpire*60, privateKey);
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
            List<byte[]> list = RsaKeyUtil.generateKeyPair(password);
            privateKey = list.get(0);
            publicKey = list.get(1);
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
