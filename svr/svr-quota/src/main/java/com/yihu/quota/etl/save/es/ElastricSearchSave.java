package com.yihu.quota.etl.save.es;

import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.LargDataWithRunnable;
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


    public Boolean saveByMoreThred(List<SaveModel> saveModels, String jsonConfig) {
        boolean isSuccessed = true;
        try {
            int perCount = Contant.compute.perCount;
            if(saveModels.size() > perCount){
                int count  = saveModels.size()/perCount;
                int remainder = saveModels.size()%perCount;
                if(remainder != 0){
                    count ++;
                }
                for(int i=0; i< count; i++){
                    int totalCount = saveModels.size();
                    int start = 0;
                    int end = perCount - 1;
                    if( i!=0 ){
                        start = i * perCount;
                        if((i + 1) * perCount >= totalCount){
                            end = totalCount-1;
                        }else {
                            end = (i + 1) * perCount-1;
                        }
                    }
                    logger.debug("data save 这是第" + (i+1) + "个线程；数据 = " + start+ " - " + end);
                    System.out.println("data save 这是第" + (i+1) + "个线程；数据 = " + start+ " - " + end);
                    List<SaveModel> list = saveModels.subList(start, end);
                    LargDataWithRunnable dataWithRunnable = new LargDataWithRunnable(list,jsonConfig,esClientUtil);
                    Thread thread = new Thread(dataWithRunnable);
                    thread.start();
                }
            }else {
                save(saveModels,jsonConfig);
            }


        } catch (Exception e) {
            throw new RuntimeException("ES 保存数据异常");
        }
        return  isSuccessed;
    }

    public Boolean save(List<SaveModel> smss, String jsonConfig) {
        BulkResult br = null;
        boolean isSuccessed = false;
        try {
            System.out.println("ES 开始保存 " + smss.size());
            int perCount = Contant.compute.perCount;
            //初始化参数
            esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(jsonConfig), EsConfig.class);
            if(smss.size() > perCount){
                int count  = smss.size()/perCount;
                int remainder = smss.size()%perCount;
                if(remainder != 0){
                    count ++;
                }
                for(int i = 1;i<= count ;i++){
                    List<SaveModel> newList = null;
                    if(i == count){
                        newList = smss.subList(perCount*(i-1),smss.size() -1);
                    }else {
                        newList = smss.subList(perCount*(i-1),perCount*i);
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
