package cool.wrp.feignapi.clients;

import cool.wrp.feignapi.config.FeignConfig;
import cool.wrp.feignapi.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author maxiaorui
 */
@FeignClient(value = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    /**
     * 根据 User id 返回 User 对象
     *
     * @param id user id
     * @return user 对象
     */
    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);
}
