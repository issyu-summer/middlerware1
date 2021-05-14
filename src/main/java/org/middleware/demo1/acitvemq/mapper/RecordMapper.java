package org.middleware.demo1.acitvemq.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.middleware.demo1.acitvemq.entity.po.Group;
import org.middleware.demo1.acitvemq.entity.po.Record;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:51
 */
@Component
public interface RecordMapper extends BaseMapper<Record> {
}