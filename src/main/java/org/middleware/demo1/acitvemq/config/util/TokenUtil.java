package org.middleware.demo1.acitvemq.config.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
public class TokenUtil {

    private static final String SECRET ="secret-word-token";

    /**
     * 加密
     */
    public static String token(String username,String password){
        return JWT.create().withClaim("username", username)
                .withClaim("password", password)
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 解密
     */
    public static Map<String, Claim> verify(String token){
        JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT decoder =jwtVerifier.verify(token);
        return decoder.getClaims();
    }
}
