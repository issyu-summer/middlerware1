package org.middleware.demo1.acitvemq;

import org.junit.jupiter.api.Test;
import org.middleware.demo1.acitvemq.entity.po.File;
import org.middleware.demo1.acitvemq.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author summer
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:53
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersistenceTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test(){
        fileService.updateById(
                new File().setId(1L).setFileName("file_test").setPath("/path/test").setUri("test"));
    }
    @Test
    public void saveNoId(){
        fileService.save(new File().setFileName("test-2"));
    }

    @Test
    public void saveFile(){
        //to-do
    }
}
