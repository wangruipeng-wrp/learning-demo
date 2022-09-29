package cool.wrp.securitydemo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import cool.wrp.securitydemo.constant.ApiStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一响应结果
 *
 * @author 码小瑞
 */
@Data
public class ApiResult {

    private int code;
    private String msg;
    private Object data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /* 成功 */

    public static <T> ApiResult success() {
        return success(ApiStatus.SUCCESS.getMsg(), null);
    }

    public static <T> ApiResult success(T data) {
        return success(ApiStatus.SUCCESS.getMsg(), data);
    }

    public static <T> ApiResult success(String msg, T data) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiStatus.SUCCESS.getCode());
        apiResult.setMsg(msg);
        apiResult.setData(data);
        apiResult.setTimestamp(LocalDateTime.now());
        return apiResult;
    }

    /* 失败 */

    public static <T> ApiResult fail(String msg) {
        return fail(msg, null);
    }

    public static <T> ApiResult fail(String msg, T data) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiStatus.FAIL.getCode());
        apiResult.setMsg(msg);
        apiResult.setData(data);
        apiResult.setTimestamp(LocalDateTime.now());
        return apiResult;
    }

    /* 自定义 */

    public static <T> ApiResult instance(ApiStatus status) {
        return instance(status, null);
    }

    public static <T> ApiResult instance(ApiStatus status, T data) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(status.getCode());
        apiResult.setMsg(status.getMsg());
        apiResult.setData(data);
        apiResult.setTimestamp(LocalDateTime.now());
        return apiResult;
    }

    public boolean isFail() {
        return this.code != ApiStatus.SUCCESS.getCode();
    }

    public boolean isSuccess() {
        return this.code == ApiStatus.SUCCESS.getCode();
    }
}
