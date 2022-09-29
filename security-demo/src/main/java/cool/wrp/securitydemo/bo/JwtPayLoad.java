package cool.wrp.securitydemo.bo;

import cool.wrp.securitydemo.entity.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 码小瑞
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayLoad {

    private Long userId;

    public JwtPayLoad(LoginUser loginUser) {
        this.userId = loginUser.getUserId();
    }
}
