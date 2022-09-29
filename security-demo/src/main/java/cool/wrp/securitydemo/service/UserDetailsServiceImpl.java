package cool.wrp.securitydemo.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import cool.wrp.securitydemo.entity.LoginUser;
import cool.wrp.securitydemo.entity.PermissionEntity;
import cool.wrp.securitydemo.entity.UserEntity;
import cool.wrp.securitydemo.mapper.PermissionMapper;
import cool.wrp.securitydemo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 码小瑞
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = new LambdaQueryChainWrapper<>(userMapper)
                .eq(UserEntity::getUsername, username)
                .one();
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在，登录失败！");
        }

        List<String> permissions = new LambdaQueryChainWrapper<>(permissionMapper)
                .select(PermissionEntity::getPermissionUri,PermissionEntity::getPermissionMethod)
                .eq(PermissionEntity::getUserId, user.getId())
                .list()
                .stream()
                .map(item -> item.getPermissionUri() + ":" + item.getPermissionMethod())
                .collect(Collectors.toList());

        return new LoginUser(user, permissions);
    }
}
