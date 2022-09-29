package cool.wrp.securitydemo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import cool.wrp.securitydemo.bo.AccessToken;
import cool.wrp.securitydemo.bo.JwtPayLoad;
import cool.wrp.securitydemo.constant.JwtConstant;
import cool.wrp.securitydemo.exception.NotAllowRefreshTokenException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author 码小瑞
 */
public class JwtUtil {

    /**
     * 从请求中拿到token
     */
    public static String getToken(HttpServletRequest request) {
        return request.getHeader(JwtConstant.REQUEST_HEADER);
    }

    /**
     * 生成 token
     */
    public static AccessToken createToken(JwtPayLoad payLoad) {
        final Date now = new Date();
        final Date expirationDate = new Date(now.getTime() + JwtConstant.EXPIRATION_TIME * 1000);
        final Date refreshDate = new Date(expirationDate.getTime() + JwtConstant.REFRESH_TIME * 1000);

        String token = JwtConstant.TOKEN_PREFIX + JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withSubject(String.valueOf(payLoad.getUserId()))
                .withClaim(JwtConstant.CLAIM_REFRESH_TIME, refreshDate)
                .sign(Algorithm.HMAC256(JwtConstant.API_SECRET_KEY));

        return AccessToken.builder()
                .userId(payLoad.getUserId())
                .token(token)
                .expiresAt(expirationDate)
                .build();
    }

    public static String getSubject(String token) {
        return verify(token).getSubject();
    }

    /**
     * 验证 token 是否还有效
     */
    public static boolean validateToken(String token) {
        return verify(token).getExpiresAt().before(new Date());
    }

    /**
     * 验证 token
     */
    private static DecodedJWT verify(String token) {
        if (token == null || "".equals(token)) {
            throw new JWTDecodeException("token can not be null");
        }

        return JWT.require(Algorithm.HMAC256(JwtConstant.API_SECRET_KEY)).build().verify(token);
    }

    /**
     * 刷新 token
     * 仅在过期时间之后，刷新时间之前的 token 可以刷新
     *
     * @param token 已过期 token
     * @return 可用 token
     */
    public static AccessToken refreshToken(String token) {
        final DecodedJWT claims = verify(token);
        final Date now = new Date();

        Date expiresDate = claims.getExpiresAt();
        if (now.before(expiresDate)) {
//            return AccessToken.builder()
//                    .userId(Long.valueOf(claims.getSubject()))
//                    .token(token)
//                    .expiresAt(claims.getExpiresAt())
//                    .build();
        }

        Date refreshDate = (Date) claims.getClaim(JwtConstant.CLAIM_REFRESH_TIME);
        if (refreshDate == null || now.after(refreshDate)) {
            throw new NotAllowRefreshTokenException();
        }

        return createToken(new JwtPayLoad(Long.valueOf(claims.getSubject())));
    }
}
