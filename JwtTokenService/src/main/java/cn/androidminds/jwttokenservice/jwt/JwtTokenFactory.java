package cn.androidminds.jwttokenservice.jwt;

import cn.androidminds.jwtcommon.JwtInfo;
import cn.androidminds.jwtcommon.JwtUtil;
import cn.androidminds.jwtcommon.RsaKeyUtil;
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
    public String generateToken(JwtInfo jwtInfo) {
        try {
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
