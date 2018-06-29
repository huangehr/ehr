package com.yihu.quota.etl.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.job.EsQuotaJob;
import com.yihu.quota.model.jpa.TjQuotaLog;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.QuotaVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by janseny on 2018/5/23.
 */
public class ExtractDataWithRunnable implements Runnable {

    private Logger logger = LoggerFactory.getLogger(ExtractDataWithRunnable.class);
    private String saasid;
    private String timeLevel;
    private String startTime;
    private String endTime;
    private QuotaVo quotaVo;
    private TjQuotaLog tjQuotaLog;
    private int start ;
    private int perCount ;
    private final int count = 0;

    public ExtractDataWithRunnable(TjQuotaLog tjQuotaLog ,QuotaVo quotaVo, String startTime,String endTime,
                                   String timeLevel,String saasid,int start,int perCount ){
        this.tjQuotaLog = tjQuotaLog;
        this.quotaVo = quotaVo;
        this.quotaVo.setStart(start);
        this.quotaVo.setRows(perCount);
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLevel = timeLevel;
        this.saasid = saasid;
    }

    @Override
    public void run(){
        try {
            synchronized(this){
                if (count != 0){
                    quotaVo.setStart(count*perCount+1);
                }else {
                    quotaVo.setStart(0);
                }
                quotaVo.setRows(Contant.compute.perCount);
                System.out.println("start = " + start );
                SpringUtil.getBean(EsQuotaJob.class).quota(tjQuotaLog,quotaVo);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public String getSaasid() {
        return saasid;
    }

    public void setSaasid(String saasid) {
        this.saasid = saasid;
    }

    public String getTimeLevel() {
        return timeLevel;
    }

    public void setTimeLevel(String timeLevel) {
        this.timeLevel = timeLevel;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public QuotaVo getQuotaVo() {
        return quotaVo;
    }

    public void setQuotaVo(QuotaVo quotaVo) {
        this.quotaVo = quotaVo;
    }

    public TjQuotaLog getTjQuotaLog() {
        return tjQuotaLog;
    }

    public void setTjQuotaLog(TjQuotaLog tjQuotaLog) {
        this.tjQuotaLog = tjQuotaLog;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPerCount() {
        return perCount;
    }

    public void setPerCount(int perCount) {
        this.perCount = perCount;
    }

}

