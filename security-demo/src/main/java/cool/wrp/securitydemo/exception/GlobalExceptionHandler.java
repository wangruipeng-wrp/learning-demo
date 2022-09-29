package cool.wrp.securitydemo.exception;

import cool.wrp.securitydemo.bo.ApiResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 *
 * @author 码小瑞
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(NotAllowRefreshTokenException.class)
    public ApiResult notAllowRefreshTokenExceptionHandler(NotAllowRefreshTokenException e) {
        return ApiResult.fail(e.getMessage());
    }
}
