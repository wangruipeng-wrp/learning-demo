package cool.wrp.orderservice.web;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import cool.wrp.feignapi.clients.UserClient;
import cool.wrp.feignapi.pojo.Order;
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
@RequestMapping("/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private final OrderMapper orderMapper;
    private final UserClient userClient;

    @GetMapping("{orderId}")
    public Order queryOrderByOrderId(@PathVariable("orderId") Long orderId) {
        Order order = new LambdaQueryChainWrapper<>(orderMapper)
                .eq(Order::getId, orderId)
                .one();

        // 调用 Feign Client 返回 User
        order.setUser(userClient.findById(order.getUserId()));

        return order;
    }

    interface OrderMapper extends BaseMapper<Order> {

    }
}
