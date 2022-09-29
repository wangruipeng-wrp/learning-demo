package cool.wrp.securitydemo.constant;

/**
 * @author 码小瑞
 */
public class JwtConstant {

    /**
     * 密匙
     */
    public static final String API_SECRET_KEY = "JWT_SECRET_KEY";

    /**
     * 过期时间-默认半个小时
     */
    public static final Long EXPIRATION_TIME = 1800L;

    /**
     * 可刷新时间-默认过期时间 + 1 天
     */
    public static final Long REFRESH_TIME = 86400L;

    /**
     * 可刷新时间字段名称
     */
    public static final String CLAIM_REFRESH_TIME = "REFRESH_TIME";

    /**
     * 默认存放token的请求头
     */
    public static final String REQUEST_HEADER = "Authorization";

    /**
     * 默认token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer";
}