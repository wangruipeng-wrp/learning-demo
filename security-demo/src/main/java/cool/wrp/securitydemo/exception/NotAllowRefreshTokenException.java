package cool.wrp.securitydemo.exception;

import lombok.Getter;

/**
 * 不允许刷新 Token
 * @author 码小瑞
 */
@Getter
public class NotAllowRefreshTokenException extends RuntimeException {

    private final String message;

    public NotAllowRefreshTokenException(String message) {
        super();
        this.message = message;
    }

    public NotAllowRefreshTokenException() {
        this("不允许刷新此 Token！");
    }
}
