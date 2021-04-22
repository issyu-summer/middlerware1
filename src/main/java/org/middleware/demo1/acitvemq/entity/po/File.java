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
 * @author summer
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("file")
public class File {

    @TableId(type= IdType.AUTO)
    private Long id;
    @TableField("file_name")
    private String fileName;
    @TableField("path")
    private String path;
    @TableField("uri")
    private String uri;
}
