package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yasuko-an
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVo implements Serializable {

    private String fileName;

    private byte[] file;

    private Long senderId;

}
