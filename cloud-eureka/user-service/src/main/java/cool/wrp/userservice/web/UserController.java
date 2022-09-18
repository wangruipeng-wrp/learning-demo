package cool.wrp.userservice.web;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maxiaorui
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getId, id)
                .one();
    }

    interface UserMapper extends BaseMapper<User> {

    }

    @Data
    @TableName("tb_user")
    private static class User {
        private Long id;
        private String username;
        private String address;
    }
}
