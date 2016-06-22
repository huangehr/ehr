package com.yihu.ehr.profile.service;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.TableBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处方笺HBASE入库服务
 * Created by lyr on 2016/6/22.
 */
@Service
public class PrescriptionPersistService {

    @Autowired
    HBaseDao hbaseDao;

    /**
     * 保存处方笺到HABSE
     * @param profileId 主表rowkey
     * @param data 处方笺数据
     * @return
     * @throws Exception
     */
    public boolean savePrescription(String profileId,List<Map<String,String>> dataList,int existed) throws Exception
    {
        //表数据
        TableBundle bundle = new TableBundle();
        //basic列族数据
        Map<String,String> basicFamily = new HashMap<String,String>();

        //basic列族添加profile_id
        basicFamily.put("profile_id",profileId);

        for (Map<String,String> data : dataList){
            //添加basic列族数据
            bundle.addValues(profileId + "$HDSC01_16$" + existed, "basic", basicFamily);
            //添加data列族数据
            bundle.addValues(profileId + "$HDSC01_16$" + existed, "d", data);

            existed++;
        }

        //保存数据到HBASE
        hbaseDao.save("HealthProfileSub", bundle);

        return true;
    }
}
