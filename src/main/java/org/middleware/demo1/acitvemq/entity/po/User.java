package org.middleware.demo1.acitvemq.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user")
public class User {
    @TableId
    private Long id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("type")
    private Integer type;
    @TableField("online")
    private Integer online;
    @TableField("img")
    private String img;
}
