package cool.wrp.securitydemo.component;

import cn.hutool.json.JSONUtil;
import cool.wrp.securitydemo.bo.ApiResult;
import cool.wrp.securitydemo.constant.ApiStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足异常处理类
 *
 * @author 码小瑞
 */
@Component
public class PermissionLessAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(JSONUtil.toJsonStr(ApiResult.instance(ApiStatus.NOT_PERMISSION)));
        response.getWriter().flush();
    }
}
