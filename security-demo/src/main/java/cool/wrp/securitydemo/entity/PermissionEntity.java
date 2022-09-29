package cool.wrp.securitydemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 码小瑞
 */
@Data
@TableName("permission")
public class PermissionEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String permissionName;

    private String permissionUri;

    private String permissionMethod;

    private String userId;

    private Integer activeStatus;

    private LocalDateTime createTime;
}
