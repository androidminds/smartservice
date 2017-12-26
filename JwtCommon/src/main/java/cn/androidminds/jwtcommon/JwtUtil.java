package cn.androidminds.jwtcommon;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;

public class JwtUtil {
    private static final String JWT_KEY_IDENTITY = "identity";
    private static final String JWT_KEY_USERNAME = "userName";

    public static String generateToken(JwtInfo jwtInfo, int expire, byte[]privateKey) throws Exception {
        return Jwts.builder()
                .setSubject(jwtInfo.getIdentity())
                .claim(JWT_KEY_IDENTITY, jwtInfo.getIdentity())
                .claim(JWT_KEY_USERNAME, jwtInfo.getUserName())
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaKeyUtil.getPrivateKey(privateKey))
                .compact();
    }

    public static JwtInfo getJwtInfo(String token, byte[]publicKey) throws Exception {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(RsaKeyUtil.getPublicKey(publicKey)).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return new JwtInfo(getObjectValue(body.get(JWT_KEY_IDENTITY)), getObjectValue(body.get(JWT_KEY_USERNAME)));
    }

    private static String getObjectValue(Object obj){
        return obj == null ? "" : obj.toString();
    }
}
