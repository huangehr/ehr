package com.yihu.quota.etl.save.es;

import com.yihu.quota.etl.model.EsConfig;
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

import java.util.List;

/**
 * Created by chenweida on 2017/6/2.
 */
@Component
@Scope("prototype")
public class ElastricSearchSave {

    private Logger logger = LoggerFactory.getLogger(ElastricSearchSave.class);
    @Autowired
    private ElasticFactory elasticFactory;

    private EsConfig esConfig;

    public Boolean save(List<SaveModel> smss, String jsonConfig) {
        BulkResult br = null;
        try {
            //初始化参数
            esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(jsonConfig), EsConfig.class);
            //得到链接
            JestClient jestClient = elasticFactory.getJestClient(esConfig.getHost(),esConfig.getPort());

            Bulk.Builder bulk = new Bulk.Builder().defaultIndex(esConfig.getIndex()).defaultType(esConfig.getType());
            for (SaveModel obj : smss) {
                Index index = new Index.Builder(obj).build();
                bulk.addAction(index);
            }
            br = jestClient.execute(bulk.build());
            //关闭链接
            jestClient.shutdownClient();
            return br.isSucceeded();
        } catch (Exception e) {
            logger.error(" save es error ：" + br.getErrorMessage());
            logger.error(" save error ：" + e.getMessage());
        }
        return null;
    }

}
