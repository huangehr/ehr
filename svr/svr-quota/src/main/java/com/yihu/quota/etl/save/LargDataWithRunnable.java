package com.yihu.quota.etl.save;

import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.vo.SaveModel;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by janseny on 2018/5/23.
 */
public class LargDataWithRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(LargDataWithRunnable.class);
    private EsClientUtil esClientUtil;
    private String jsonConfig;
    private List<SaveModel> list;//待处理数据
    private int threadCount = 0;//初始化线程数
    private int flag = 1;       //这是第几个线程
    private int perCount = 1000;//每个线程处理的数据量
    private int totalCount = 0;//待处理数据总数量
    private int havedCount = 0;//已经处理的数据量

    public LargDataWithRunnable(List<SaveModel> saveModels, String jsonConfig , EsClientUtil esClientUtil){
        this.list = saveModels;
        int count  = saveModels.size()/perCount;
        int remainder = saveModels.size()%perCount;
        if(remainder != 0){
            count ++;
        }
        this.threadCount = count;
        this.totalCount = list.size();
        this.jsonConfig = jsonConfig;
        this.esClientUtil = esClientUtil;
    }

    @Override
    public void run() {
        List<SaveModel> sublist = null;
        while(totalCount - havedCount > 0){//线程会循环执行，直到所有数据都处理完
            synchronized(this){//在分包时需要线程同步，避免线程间处理重复的数据
                if(totalCount-havedCount != 0) {
                    sublist = list.subList(perCount*(flag-1), totalCount - havedCount > perCount ? perCount*flag : perCount*(flag-1) + (totalCount - havedCount));
                    flag = flag+1;
                    havedCount = sublist.size() + havedCount;
                    logger.debug("这是第" + (flag-1) +"个线程；数据 = "+ sublist.size());
                }
                if(sublist != null) {
                    //此处为数据处理（简单打印 ）
                    System.out.println(Thread.currentThread().getName());
                    BulkResult br = null;
                    boolean isSuccessed = false;
                    try {
                        //得到链接
                        EsConfig esConfig = (EsConfig) JSONObject.toBean(JSONObject.fromObject(jsonConfig), EsConfig.class);
                        System.out.println(esConfig.getIndex());
                        JestClient jestClient = esClientUtil.getJestClient(esConfig.getHost(),esConfig.getPort());
                        Bulk.Builder bulk = new Bulk.Builder().defaultIndex(esConfig.getIndex()).defaultType(esConfig.getType());
                        for (SaveModel obj : sublist) {
                            obj.setCreateTime( new Date());
                            Index index = new Index.Builder(obj).build();
                            bulk.addAction(index);
                        }
                        br = jestClient.execute(bulk.build());
                        //关闭链接
                        jestClient.shutdownClient();
                        isSuccessed = br.isSucceeded();
                    }catch (Exception e){
                        throw new RuntimeException("ES 保存数据异常");
                    }
                }
            }
        }

    }


    public List<SaveModel> getList() {
        return list;
    }

    public void setList(List<SaveModel> list) {
        this.list = list;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getPerCount() {
        return perCount;
    }

    public void setPerCount(int perCount) {
        this.perCount = perCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getHavedCount() {
        return havedCount;
    }

    public void setHavedCount(int havedCount) {
        this.havedCount = havedCount;
    }

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    public EsClientUtil getEsClientUtil() {
        return esClientUtil;
    }

    public void setEsClientUtil(EsClientUtil esClientUtil) {
        this.esClientUtil = esClientUtil;
    }
}
