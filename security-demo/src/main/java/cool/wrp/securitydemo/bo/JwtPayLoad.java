package cool.wrp.securitydemo.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author 码小瑞
 */
@Data
@Builder
public class JwtPayLoad {

    private Long userId;
}
