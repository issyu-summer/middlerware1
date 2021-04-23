package org.middleware.demo1.acitvemq.schedule;

import org.middleware.demo1.acitvemq.entity.po.File;
import org.middleware.demo1.acitvemq.mapper.FileMapper;
import org.middleware.demo1.acitvemq.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class BackupMYSQL {
    @Autowired
    private FileMapper fileMapper;

    //每小时执行一次备份
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void testSca(){
        try{
            Date d = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String dateNowStr = simpleDateFormat.format(d);
            String path="src/main/resources/txtbackup/" + dateNowStr+".txt";
            java.io.File folder = new java.io.File("src/main/resources/txtbackup");
            if(!folder.exists()){
                folder.mkdir();
            }

            List<File> list = fileMapper.selectList(null);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));

            list.forEach(vo->{
                try{
                    String str = vo.getId()+","+vo.getFileName()+","+vo.getPath()+","+vo.getUri()+"\r\n";
                    bos.write(str.getBytes());
                }catch (Exception e){
                    System.out.println(e);
                }
            });
            System.out.println("BackupMYSQL : "+dateNowStr);
            bos.close();
        }catch (Exception e){

        }

    }
}
