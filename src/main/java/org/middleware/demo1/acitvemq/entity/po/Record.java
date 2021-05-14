package org.middleware.demo1.acitvemq.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.time.LocalDateTime;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("`record`")
public class Record {

    @TableId(type= IdType.AUTO)
    private Long id;
    @TableField("date_time")
    private LocalDateTime dateTime;
    @TableField("content")
    private String content;
    @TableField("type")
    private Integer type;
    @TableField("receiver_id")
    private Long receiverId;
    @TableField("sender_id")
    private Long senderId;
}
