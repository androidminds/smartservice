package cn.androidminds.jwtservice.jwt;

import cn.androidminds.commonapi.jwt.JwtInfo;
import cn.androidminds.commonapi.jwt.JwtUtil;
import cn.androidminds.commonapi.jwt.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Service
public class JwtTokenFactory {

    private byte[] privateKey;
    private byte[] publicKey;

    @Value("${jwt.token.expire:1440}")
    private int tokenExpire; //unit is minute

    @Value("${jwt.password:$@23sdf}")
    private String password;

    public String generateToken(JwtInfo jwtInfo) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(publicKey == null) {
            generateKeyPair();
        }
        return JwtUtil.generateToken(jwtInfo, tokenExpire * 60, privateKey);
    }

    public byte[] getPublicKey() throws NoSuchAlgorithmException {
        if(publicKey == null) {
            generateKeyPair();
        }
        return publicKey;
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        List<byte[]> list = RsaKeyUtil.generateKeyPair(password);
        privateKey = list.get(0);
        publicKey = list.get(1);
    }
}
