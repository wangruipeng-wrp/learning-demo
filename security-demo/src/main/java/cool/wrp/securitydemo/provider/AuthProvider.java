package cool.wrp.securitydemo.provider;

import cool.wrp.securitydemo.entity.LoginUser;
import cool.wrp.securitydemo.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 码小瑞
 */
public class AuthProvider {

    public static LoginUser getLoginUser() {
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static UserEntity getUserEntity() {
        return ((LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserEntity();
    }

    public static String getUsername() {
        return ((LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    public static Long getUserId() {
        return ((LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
    }
}
