package org.middleware.demo1.acitvemq.config.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.List;

/**
 * @author summer
 * @date 2021/3/14 19:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Group {
    private Long id;
    private String name;

    private List<User> userList;
    private List<File> groupFiles;

    private List<Msg> groupMsg;
}
