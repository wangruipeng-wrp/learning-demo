package cool.wrp.securitydemo.component;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;
import cool.wrp.securitydemo.constant.CacheName;
import cool.wrp.securitydemo.entity.LoginUser;
import cool.wrp.securitydemo.entity.PermissionEntity;
import cool.wrp.securitydemo.mapper.PermissionMapper;
import cool.wrp.securitydemo.provider.CacheProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口访问投票器
 *
 * @author 码小瑞
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApiAccessVoter implements AccessDecisionVoter<FilterInvocation> {

    private final CacheProvider cache;
    private final PermissionMapper permissionMapper;

    @PostConstruct
    public void init() {
        log.info("ApiAccessVoter 初始化工作开始...");
        List<PermissionEntity> permissionList = new LambdaQueryChainWrapper<>(permissionMapper)
                .select(PermissionEntity::getPermissionUri,
                        PermissionEntity::getPermissionMethod,
                        PermissionEntity::getActiveStatus)
                .list();

        Map<String, Integer> map = new HashMap<>();
        for (PermissionEntity p : permissionList) {
            String permissionValue = p.getPermissionUri() + ":" + p.getPermissionMethod();
            Integer isActive = p.getActiveStatus();
            cache.put(CacheName.PERMISSION, permissionValue, isActive);
            map.put(permissionValue, isActive);
        }
        log.info("ApiAccessVoter 初始化工作完成，缓存权限数据如下：\n{}", map);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {

        if (authentication == null || fi == null) {
            // 弃权
            return ACCESS_ABSTAIN;
        }

        String requestUrl = fi.getRequestUrl();
        String loginPath = "/api/auth/login";
        if (loginPath.equals(requestUrl)) {
            // 登录接口，弃权
            return ACCESS_ABSTAIN;
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        String method = fi.getRequest().getMethod();
        String apiPermissionValue = requestUrl + ":" + method;

        log.info("ApiAccessVoter 开始投票，用户：[{}]，请求接口：[{}]",loginUser.getUsername(), apiPermissionValue);

        final Integer isActive = cache.get(CacheName.PERMISSION, apiPermissionValue, Integer.class);
        if (isActive == null || isActive != 1) {
            log.info("ApiAccessVoter 弃权");
            // 未保护此 API，弃权
            return ACCESS_ABSTAIN;
        }

        if (loginUser.getPermissions().contains(apiPermissionValue)) {
            log.info("ApiAccessVoter 赞成");
            return ACCESS_GRANTED;
        } else {
            log.info("ApiAccessVoter 反对");
            return ACCESS_DENIED;
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}