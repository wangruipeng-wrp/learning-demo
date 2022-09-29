package cool.wrp.securitydemo.component;

import cn.hutool.json.JSONUtil;
import cool.wrp.securitydemo.bo.ApiResult;
import cool.wrp.securitydemo.constant.ApiStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户未登录异常处理类
 *
 * @author 码小瑞
 */
@Component
public class UnLoginAccessDeniedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(JSONUtil.toJsonStr(ApiResult.instance(ApiStatus.UNAUTHORIZED)));
        response.getWriter().flush();
    }
}
