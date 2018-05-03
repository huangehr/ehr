package com.yihu.quota.etl.save.es;

import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.vo.SaveModel;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import net.sf.json.JSONObject;;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by chenweida on 2017/6/2.
 */
@Component
@Scope("prototype")
public class ElastricSearchSave {

    private Logger logger = LoggerFactory.getLogger(ElastricSearchSave.class);
    @Autowired
    private EsClientUtil esClientUtil;

    private EsConfig esConfig;

    public Boolean save(List<SaveModel> smss, String jsonConfig) {
        BulkResult br = null;
        boolean isSuccessed = false;
        try {
            //初始化参数
            esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(jsonConfig), EsConfig.class);
            if(smss.size() > 10000){
                int count  = smss.size()/10000;
                int remainder = smss.size()%10000;
                if(remainder != 0){
                    count ++;
                }
                for(int i = 1;i<= count ;i++){
                    List<SaveModel> newList = null;
                    if(i == count){
                        newList = smss.subList(10000*(i-1),smss.size() -1);
                    }else {
                        newList = smss.subList(10000*(i-1),10000*i);
                    }
                    //得到链接
                    JestClient jestClient = esClientUtil.getJestClient(esConfig.getHost(),esConfig.getPort());
                    Bulk.Builder bulk = new Bulk.Builder().defaultIndex(esConfig.getIndex()).defaultType(esConfig.getType());
                    for (SaveModel obj : newList) {
                        obj.setCreateTime( new Date());
                        Index index = new Index.Builder(obj).build();
                        bulk.addAction(index);
                    }
                    br = jestClient.execute(bulk.build());
                    //关闭链接
                    jestClient.shutdownClient();
                    isSuccessed = br.isSucceeded();
                }
            }else{
                //得到链接
                JestClient jestClient = esClientUtil.getJestClient(esConfig.getHost(),esConfig.getPort());
                Bulk.Builder bulk = new Bulk.Builder().defaultIndex(esConfig.getIndex()).defaultType(esConfig.getType());
                for (SaveModel obj : smss) {
                    obj.setCreateTime( new Date());
                    Index index = new Index.Builder(obj).build();
                    bulk.addAction(index);
                }
                br = jestClient.execute(bulk.build());
                //关闭链接
                jestClient.shutdownClient();
                isSuccessed = br.isSucceeded();
            }
            return isSuccessed;
        } catch (Exception e) {
            throw new RuntimeException("ES 保存数据异常");
        }
    }



}
