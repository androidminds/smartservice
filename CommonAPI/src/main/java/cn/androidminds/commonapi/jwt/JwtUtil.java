package cn.androidminds.commonapi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

public class JwtUtil {
    private static final String JWT_KEY_USERNAME = "userName";
    private static final String JWT_KEY_USERID = "userId";

    public static String generateToken(JwtInfo jwtInfo, int expire, byte[]privateKey)
            throws InvalidKeySpecException {
        return Jwts.builder()
                .claim(JWT_KEY_USERNAME, jwtInfo.getUserName())
                .claim(JWT_KEY_USERID, jwtInfo.getUserId())
                .setExpiration(DateTime.now().plusMinutes(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaKeyUtil.getPrivateKey(privateKey))
                .compact();
    }

    public static JwtInfo getJwtInfo(String token, byte[]publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(RsaKeyUtil.getPublicKey(publicKey)).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return new JwtInfo(getObjectValue(body.get(JWT_KEY_USERNAME)),
                getObjectValue(body.get(JWT_KEY_USERID)),
                body.getExpiration());
    }

    private static String getObjectValue(Object obj){
        return obj == null ? "" : obj.toString();
    }

}
