package com.yihu.ehr.resolve.service.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.esb.model.Upload;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


@Service
public class UploadService {

    private static final String INDEX = "upload";
    private static final String TYPE = "info";
    private static int time = 86400000;//1天毫秒数

    @Value("${eip.schemaVersion}")
    private String schemeVersion;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;


    public void addWaitUpload(ResourceBucket resourceBucket) throws IOException, ParseException {

        //结构化数据才进行上传省平台...
        if(resourceBucket.getProfileType().getType()==1){
            if (resourceBucket.isReUploadFlg()) {
                //首先删除
                elasticSearchUtil.delete(INDEX,TYPE,resourceBucket.getId());
                //重新插入
                addWaitUpload(resourceBucket,0);
            }else{
                //判断是否是补传,补传则添加数据
                long eventTime = resourceBucket.getEventDate().getTime();
                long currentTime = new Date().getTime();
                long diff = currentTime - eventTime;
                double day = diff / time;
                if(day > 2){
                    //首先删除
                    elasticSearchUtil.delete(INDEX,TYPE,resourceBucket.getId());
                    addWaitUpload(resourceBucket,1);
                }
            }
        }
    }

    private void addWaitUpload(ResourceBucket resourceBucket,int origin) throws IOException, ParseException {
        Upload upload = new Upload();
        upload.set_id(resourceBucket.getId());
        upload.setOrigin(origin);
        upload.setCreate_date(new Date());
        upload.setSchema_version(schemeVersion);
        upload.setFailed_count(0);
        if(origin == 0){
            upload.setFailed_message("reUpload");
        }else if(origin == 1){
            upload.setFailed_message("oldData");
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        upload.setRepeat_date(c.getTime());
        upload.setEvent_date(resourceBucket.getEventDate());
        upload.setRow_key(resourceBucket.getId());
        elasticSearchUtil.index(INDEX, TYPE, objectMapper.readValue(objectMapper.writeValueAsString(upload), Map.class ));
    }
}
