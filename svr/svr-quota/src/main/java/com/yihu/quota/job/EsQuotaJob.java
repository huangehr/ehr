package com.yihu.quota.job;

import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.dao.jpa.TjQuotaLogDao;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractHelper;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.SaveHelper;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.etl.util.EsClientUtil;
import com.yihu.quota.etl.util.EsConfigUtil;
import com.yihu.quota.model.jpa.TjQuotaLog;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chenweida on 2017/6/6.
 */
@Component
@Scope("prototype")
@DisallowConcurrentExecution//防止到了执行时间点前一任务还在执行中，但是这时有空闲的线程，那么马上又会执行，这样一来就会存在同一job被并行执行
public class EsQuotaJob implements Job {
    private Logger logger = LoggerFactory.getLogger(EsQuotaJob.class);

    private String saasid;//saasid
    private QuotaVo quotaVo=new QuotaVo();//指标对象
    private String endTime;//结束时间
    private String startTime;//开始时间
    private String timeLevel;//时间
    @Autowired
    private TjQuotaLogDao tjQuotaLogDao;
    @Autowired
    private EsClientUtil esClientUtil;
    @Autowired
    EsConfigUtil esConfigUtil;
    @Autowired
    private ExtractHelper extractHelper;
    @Autowired
    ElasticsearchUtil elasticsearchUtil;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //springz注入
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            //初始化参数
            initParams(context);
            //统计
            quota();
        } catch (Exception e) {
            //如果出錯立即重新執行
            JobExecutionException e2 = new JobExecutionException(e);
            e2.setRefireImmediately(true);
            e.printStackTrace();
        }
    }

    /**
     * 统计过程
     */
    private void quota() {
        String message = "";
        TjQuotaLog tjQuotaLog = new TjQuotaLog();
        tjQuotaLog.setQuotaCode(quotaVo.getCode());
        tjQuotaLog.setSaasId(saasid);
        tjQuotaLog.setStartTime(new Date());
        try {
            //抽取数据 如果是累加就是 List<DataModel>  如果是相除 Map<String,List<DataModel>>
            deleteRecord();
            List<SaveModel> dataModels = extract();
            if(dataModels != null && dataModels.size() > 0){
                if(dataModels!=null && dataModels.size()==1 && dataModels.get(0).getQuotaCode().equals("orgHealthCategory")){
                    //特殊卫生机构类型数据返回结果
                    if(dataModels.get(0).getSaasId().equals("success")){
                        tjQuotaLog.setStatus(Contant.save_status.success);
                        tjQuotaLog.setContent("特殊卫生机构类型 统计保存成功");
                        System.out.println("特殊卫生机构类型 统计保存成功");
                    }else {
                        tjQuotaLog.setStatus(Contant.save_status.fail);
                        tjQuotaLog.setContent("特殊卫生机构类型 统计数据ElasticSearch保存失败");
                        System.out.println("特殊卫生机构类型 统计数据ElasticSearch保存失败");
                    }
                }else{
                    String quoataDate =  new org.joda.time.LocalDate(new DateTime().minusDays(1)).toString("yyyy-MM-dd");
                    //查询是否已经统计过,如果已统计 先删除后保存
                    deleteRecord();
                    List<SaveModel> dataSaveModels = new ArrayList<>();
                    for(SaveModel saveModel :dataModels){
                        if(saveModel.getResult() != null ){//&& Double.valueOf(saveModel.getResult())>0
                            saveModel.setQuotaDate(quoataDate);
                            dataSaveModels.add(saveModel);
                        }
                    }
                    if(dataSaveModels != null && dataSaveModels.size() > 0){
                        //保存数据
                        Boolean success = saveDate(dataSaveModels);
                        tjQuotaLog.setStatus(success ? Contant.save_status.success : Contant.save_status.fail);
                        tjQuotaLog.setContent(success ? "统计保存成功" : "统计数据ElasticSearch保存失败");
                        System.out.println(success?"统计保存成功":"统计数据ElasticSearch保存失败");
                    }else {
                        tjQuotaLog.setStatus(Contant.save_status.success);
                        tjQuotaLog.setContent("统计成功,统计结果大于0的数据为0条");
                    }
                }
            }else {
                tjQuotaLog.setStatus(Contant.save_status.fail);
                tjQuotaLog.setContent("没有抽取到数据");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            message = e.getMessage();
            tjQuotaLog.setStatus(Contant.save_status.fail);
            tjQuotaLog.setContent(message);
        }
        tjQuotaLog.setEndTime(new Date());
        saveLog(tjQuotaLog);
    }

    private void deleteRecord() throws Exception {
        EsConfig esConfig = extractHelper.getEsConfig(quotaVo.getCode());
        BoolQueryBuilder boolQueryBuilder =  QueryBuilders.boolQuery();
        if(esConfig.getType().equals("orgHealthCategoryQuota")){
            QueryStringQueryBuilder termQueryQuotaCode = QueryBuilders.queryStringQuery("orgHealthCategoryQuotaCode:" + quotaVo.getCode().replaceAll("_", ""));
            boolQueryBuilder.must(termQueryQuotaCode);
        }else{
            QueryStringQueryBuilder termQueryQuotaCode = QueryBuilders.queryStringQuery("quotaCode:" + quotaVo.getCode().replaceAll("_", ""));
            boolQueryBuilder.must(termQueryQuotaCode);
        }

        if( !StringUtils.isEmpty(startTime) ){
            RangeQueryBuilder rangeQueryStartTime = QueryBuilders.rangeQuery("quotaDate").gte(startTime);
                    boolQueryBuilder.must(rangeQueryStartTime);
        }
        if( !StringUtils.isEmpty(endTime)){
            RangeQueryBuilder rangeQueryEndTime = QueryBuilders.rangeQuery("quotaDate").lte(endTime);
                    boolQueryBuilder.must(rangeQueryEndTime);
        }
        Client client = esClientUtil.getClient(esConfig.getHost(), esConfig.getPort(),esConfig.getIndex(),esConfig.getType(), esConfig.getClusterName());
        try {
            elasticsearchUtil.queryDelete(client,boolQueryBuilder);
        }catch (Exception e){
            e.getMessage();
        }finally {
            client.close();
        }
    }

    /**
     * 抽取数据
     *
     * @return
     */
    private List<SaveModel> extract() throws Exception {
        return SpringUtil.getBean(ExtractHelper.class).extractData(quotaVo, startTime, endTime,timeLevel,saasid);
    }

    /**
     * 初始化参数
     *
     * @param context
     */
    private void initParams(JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        Map<String, Object> params = context.getJobDetail().getJobDataMap();
        this.saasid = map.getString("saasid");
        this.endTime = map.getString("endTime");
        if (StringUtils.isEmpty(endTime)) {
            endTime = LocalDate.now().toString("yyyy-MM-dd"); //2017-06-01 默认今天
        }
        this.startTime = map.getString("startTime");
        if (StringUtils.isEmpty(startTime)) {
            startTime = Contant.main_dimension_timeLevel.getStartTime(timeLevel);
        }

        Object object =  map.get("quota");
        if(object!=null){
            BeanUtils.copyProperties(object,this.quotaVo);
        }
        this.timeLevel = (String) map.get("timeLevel");
        if (StringUtils.isEmpty(this.timeLevel)) {
            this.timeLevel = Contant.main_dimension_timeLevel.day;
        }
    }

    @Transactional
    private void saveLog(TjQuotaLog tjQuotaLog) {
        tjQuotaLogDao.save(tjQuotaLog);
    }

    /**
     * 保存数据
     *
     * @param dataModels
     */
    private Boolean saveDate(List<SaveModel> dataModels) {
        try {
            return SpringUtil.getBean(SaveHelper.class).save(dataModels, quotaVo);
        } catch (Exception e) {
            logger.error("save error:" + e.getMessage());
        }
        return false;
    }
}
