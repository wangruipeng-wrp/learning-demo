package cool.wrp.securitydemo.api;

import cool.wrp.securitydemo.bo.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 鉴权测试 Controller
 *
 * @author 码小瑞
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionController {

    @GetMapping("/user")
    public ApiResult user() {
        return ApiResult.success("USER用户访问成功");
    }

    @GetMapping("/admin")
    public ApiResult admin() {
        return ApiResult.success("管理员用户访问成功");
    }
}
