package com.yihu.ehr.profile.service;

import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.TableBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>>  savePrescription(String profileId,List<Map<String,String>> dataList,int existed) throws Exception
    {
        //表数据
        TableBundle bundle = new TableBundle();
        //basic列族数据
        Map<String,String> basicFamily = new HashMap<String,String>();
        //返回保存成功数据
        List<Map<String,Object>> returnMapList = new ArrayList<Map<String,Object>>();

        //basic列族添加profile_id
        basicFamily.put("profile_id",profileId);
        //rowkey集合
        List<String> rowkeys = new ArrayList<String>();

        for(int i = 0; i < dataList.size(); i++)
        {
            int dataCount = existed + i;
            //行主健
            String rowkey = profileId + "$HDSC01_16$" + dataCount;
            rowkeys.add(rowkey);
        }

        //删除已有数据
        if(rowkeys.size() > 0)
        {
            bundle.addRows(rowkeys.toArray(new String[rowkeys.size()]));
            hbaseDao.delete("HealthProfileSub", bundle);
        }

        for (Map<String,String> data : dataList)
        {
            //返回保存数据
            Map<String,Object> returnMap = new HashMap<String,Object>();
            //行主健
            String rowkey = profileId + "$HDSC01_16$" + existed;
            //添加basic列族数据
            bundle.addValues(rowkey, "basic", basicFamily);
            //添加data列族数据
            bundle.addValues(rowkey, "d", data);

            //返回保存数据
            returnMap.put("rowkey",rowkey);
            returnMap.put("profile_id",profileId);
            returnMap.putAll(data);
            returnMapList.add(returnMap);

            existed++;
        }

        //保存数据到HBASE
        hbaseDao.save("HealthProfileSub", bundle);

        return returnMapList;
    }
}
