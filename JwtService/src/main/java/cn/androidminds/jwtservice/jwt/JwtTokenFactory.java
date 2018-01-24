package cn.androidminds.jwtservice.jwt;

import cn.androidminds.commonapi.jwt.JwtInfo;
import cn.androidminds.commonapi.jwt.JwtUtil;
import cn.androidminds.commonapi.jwt.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Service
public class JwtTokenFactory {

    private byte[] privateKey;
    private byte[] publicKey;

    @Value("${jwt.token.expire-minutes:1440}")
    private int tokenExpireMinutes;

    @Value("${jwt.password:$@23sdf}")
    private String password;

    @PostConstruct
    public void init() {
        List<byte[]> list = RsaKeyUtil.generateKeyPair(password);
        privateKey = list.get(0);
        publicKey = list.get(1);
    }

    public String generateToken(JwtInfo jwtInfo) throws InvalidKeySpecException {
        return JwtUtil.generateToken(jwtInfo, tokenExpireMinutes, privateKey);
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
