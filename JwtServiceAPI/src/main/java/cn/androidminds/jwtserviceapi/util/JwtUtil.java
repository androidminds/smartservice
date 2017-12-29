package cn.androidminds.jwtserviceapi.util;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

public class JwtUtil {
    private static final String JWT_KEY_USERNAME = "userName";

    public static String generateToken(JwtInfo jwtInfo, int expire, byte[]privateKey) throws Exception {
        return Jwts.builder()
                .claim(JWT_KEY_USERNAME, jwtInfo.getUserName())
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaKeyUtil.getPrivateKey(privateKey))
                .compact();
    }

    public static JwtInfo getJwtInfo(String token, byte[]publicKey) throws Exception {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(RsaKeyUtil.getPublicKey(publicKey)).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        return new JwtInfo(getObjectValue(body.get(JWT_KEY_USERNAME)));
    }

    private static String getObjectValue(Object obj){
        return obj == null ? "" : obj.toString();
    }
}
