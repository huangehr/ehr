package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.util.ftp.FtpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FtpFileService {

    @Value("${ftp.address}")
    private String address;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.port}")
    private int port;

    public void deleteFile(LinkPackage pack){
        Map<String, List<String>> files = pack.getFiles();
        if(files !=null && files.size()>0){
            FtpUtils ftpUtils = null;
            try{
                ftpUtils = new FtpUtils(username, password, address, port);
                ftpUtils.connect();
                for(String path: files.keySet()){//key值为path
                    List<String> fileNames = files.get(path);//文件名
                    ftpUtils.deleteFile(path,fileNames);
                }
            } finally {
                if(ftpUtils != null){
                    ftpUtils.closeConnect();
                }
            }
        }
    }

}
