package cool.wrp.orderservice.web;

import com.baomidou.mybatisplus.annotation.TableField;
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
import org.springframework.web.client.RestTemplate;

/**
 * @author maxiaorui
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private final OrderMapper orderMapper;
    private final RestTemplate rt;

    @GetMapping("{orderId}")
    public Order queryOrderByOrderId(@PathVariable("orderId") Long orderId) {
        Order order = new LambdaQueryChainWrapper<>(orderMapper)
                .eq(Order::getId, orderId)
                .one();

        String url = "http://user-service/user/" + order.getUserId();
        order.setUser(rt.getForObject(url, User.class));

        return order;
    }

    interface OrderMapper extends BaseMapper<Order> {

    }

    @Data
    @TableName("tb_order")
    private static class Order {
        private Long id;
        private Long price;
        private String name;
        private Integer num;
        private Long userId;
        @TableField(exist = false)
        private User user;
    }

    @Data
    @TableName("tb_user")
    private static class User {
        private Long id;
        private String username;
        private String address;
    }
}
