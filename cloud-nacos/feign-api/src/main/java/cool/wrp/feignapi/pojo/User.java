package cool.wrp.feignapi.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author maxiaorui
 */
@Data
@TableName("tb_user")
public class User {
    private Long id;
    private String username;
    private String address;
}
