package cool.wrp.securitydemo.filter;

import cn.hutool.json.JSONUtil;
import cool.wrp.securitydemo.bo.ApiResult;
import cool.wrp.securitydemo.constant.ApiStatus;
import cool.wrp.securitydemo.constant.CacheName;
import cool.wrp.securitydemo.constant.JwtConstant;
import cool.wrp.securitydemo.entity.LoginUser;
import cool.wrp.securitydemo.provider.CacheProvider;
import cool.wrp.securitydemo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 登录认证过滤器
 *
 * @author 码小瑞
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final CacheProvider cache;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        String loginPath = "/api/auth/login";
        if (loginPath.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWT过滤器开始校验 Token");

        // 请求的 token 信息
        String authToken = JwtUtil.getToken(request);

        // 判断 token 是否合法
        if (authToken == null || "".equals(authToken) || !authToken.startsWith(JwtConstant.TOKEN_PREFIX)) {
            log.info("JWT过滤器校验结果：此 Token 不合法。");
            fail(response, ApiResult.instance(ApiStatus.UNAUTHORIZED));
            return;
        }

        String realToken = authToken.substring(JwtConstant.TOKEN_PREFIX.length());
        String loginAccount = JwtUtil.getSubject(realToken);

        // 判断用户登录是否已过期
        LoginUser loginUser = cache.get(CacheName.USER, loginAccount, LoginUser.class);
        if (loginUser == null) {
            log.info("JWT过滤器校验结果：未从缓存中找到此 Token，验证为未登录或已过期。");
            fail(response, ApiResult.instance(ApiStatus.UNAUTHORIZED));
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginUser,
                loginUser.getPassword(),
                loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("JWT过滤器校验 Token 成功, user : {}", loginUser.getUsername());
        filterChain.doFilter(request, response);
    }

    private void fail(HttpServletResponse response, ApiResult apiResult) throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(JSONUtil.toJsonStr(apiResult));
        response.getWriter().flush();
    }
}
