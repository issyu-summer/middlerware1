package org.middleware.demo1.acitvemq.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("`group`")
public class Group {

    @TableId(type= IdType.AUTO)
    private Long id;
    @TableField("group_name")
    private String groupName;
}
