package cool.wrp.securitydemo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Token 对象
 *
 * @author 码小瑞
 */
@Data
@Builder
public class AccessToken {
    private Long userId;
    private String token;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiresAt;
}