package cool.wrp.securitydemo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 码小瑞
 */
@Data
public class LoginInfo {

    @NotBlank(message = "登陆用户名为空")
    private String loginAccount;

    @NotBlank(message = "登陆密码为空")
    private String password;
}
