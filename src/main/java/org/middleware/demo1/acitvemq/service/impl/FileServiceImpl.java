package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.File;
import org.middleware.demo1.acitvemq.mapper.FileMapper;
import org.middleware.demo1.acitvemq.service.FileService;
import org.springframework.stereotype.Service;

/**
 * @author summer
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:52
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
}
