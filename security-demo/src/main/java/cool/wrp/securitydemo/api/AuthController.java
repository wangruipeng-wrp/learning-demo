package cool.wrp.securitydemo.api;

import cool.wrp.securitydemo.bo.AccessToken;
import cool.wrp.securitydemo.bo.ApiResult;
import cool.wrp.securitydemo.bo.JwtPayLoad;
import cool.wrp.securitydemo.bo.LoginInfo;
import cool.wrp.securitydemo.constant.CacheName;
import cool.wrp.securitydemo.constant.JwtConstant;
import cool.wrp.securitydemo.entity.LoginUser;
import cool.wrp.securitydemo.provider.AuthProvider;
import cool.wrp.securitydemo.provider.CacheProvider;
import cool.wrp.securitydemo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证测试 Controller
 *
 * @author 码小瑞
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    // 代码比较简单，为方便起见就不严格遵循 MVC 三层架构的设计了。

    private final AuthenticationManager am;
    private final CacheProvider cache;

    @PostMapping("/login")
    public ApiResult login(@Valid @RequestBody LoginInfo loginInfo) {
        log.info("{} 用户开始登录。", loginInfo.getLoginAccount());

        // 认证
        final UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginInfo.getLoginAccount(), loginInfo.getPassword());
        final Authentication authenticate = am.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        log.info("{} 用户成功登录。", loginInfo.getLoginAccount());

        // 放入缓存
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        cache.put(CacheName.USER, String.valueOf(loginUser.getUserId()), loginUser);

        // 生成 token
        AccessToken accessToken = JwtUtil.createToken(new JwtPayLoad(loginUser));
        return ApiResult.success(accessToken);
    }

    @PostMapping("/logout")
    public ApiResult logout() {
        cache.remove(CacheName.USER, String.valueOf(AuthProvider.getUserId()));
        SecurityContextHolder.clearContext();
        return ApiResult.success();
    }

    @PostMapping("/refresh")
    public ApiResult refreshToken(HttpServletRequest request) {
        String token = JwtUtil.getToken(request);
        String realToken = token.substring(JwtConstant.TOKEN_PREFIX.length());
        return ApiResult.success(JwtUtil.refreshToken(realToken));
    }
}
