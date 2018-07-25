package com.yihu.quota.job;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.quota.dao.jpa.TjQuotaLogDao;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractHelper;
import com.yihu.quota.etl.extract.solr.SolrExtract;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.SaveHelper;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.model.jpa.TjQuotaLog;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import net.sf.json.JSONObject;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.joda.time.LocalDate;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private String saasid; // saasid
    private QuotaVo quotaVo = new QuotaVo(); // 指标对象
    private String endTime; // 结束时间
    private String startTime; //开始时间
    private String timeLevel; //时间
    private String executeFlag; // 执行动作 1 手动执行 2 周期执行
    private int haveThreadCount = 0;//已完成线程数
    private int threadCount = 1;//总线程数

    @Autowired
    private TjQuotaLogDao tjQuotaLogDao;
    @Autowired
    private ExtractHelper extractHelper;
    @Autowired
    ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SolrExtract solrExtract;
    @Autowired
    private TjDataSourceService dataSourceService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TjQuotaLog tjQuotaLog = new TjQuotaLog();
        String time = "";
        try {
            //springz注入
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            //初始化参数
            initParams(context);
            quotaVo.setExecuteFlag(executeFlag);
            tjQuotaLog.setQuotaCode(quotaVo.getCode());
            tjQuotaLog.setSaasId(saasid);
            tjQuotaLog.setStartTime(new Date());
            tjQuotaLog.setStatus( Contant.save_status.executing);  //指标执行中
            tjQuotaLog.setContent( "时间：" + startTime + "到"+ endTime +" , " + "任务执行中。");
            tjQuotaLog = saveLog(tjQuotaLog);
            time = "时间：" + startTime + "到"+ endTime +" , ";
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(quotaVo.getCode());
            if (quotaDataSource == null) {
                throw new Exception("数据源配置错误");
            }
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig = (EsConfig) JSONObject.toBean(obj,EsConfig.class);
            //查询是否已经统计过,如果已统计 先删除后保存
            deleteRecord(quotaVo);
            if(quotaDataSource.getSourceCode().equals("2") && esConfig.getAggregation()!= null && esConfig.getAggregation().equals("list")) {//来源solr
                moreThredQuota(tjQuotaLog,esConfig);
            }else{
                //统计并保存
                quota(tjQuotaLog, quotaVo);
            }
        } catch (Exception e) {
            //如果出錯立即重新執行
            tjQuotaLog.setStatus(Contant.save_status.fail);
            tjQuotaLog.setEndTime(new Date());
            tjQuotaLog.setContent( time+"统计异常," + e.getMessage());
            saveLog(tjQuotaLog);
            JobExecutionException e2 = new JobExecutionException(e);
            e2.setRefireImmediately(true);
            e.printStackTrace();
        }
    }

    /*
     * solr list 方式 多线程执行指标
     */
    public void moreThredQuota(TjQuotaLog tjQuotaLog, EsConfig esConfig){
        try {
            int perCount = Contant.compute.perCount;
            quotaVo.setStart(0);
            quotaVo.setRows(perCount);
            int rows = solrExtract.getExtractTotal(startTime,endTime, esConfig);
            if(rows > perCount*50){
                throw new Exception("数据量过大请缩小抽取时间范围");
            }
            if (rows > perCount) {
                int count  = rows/perCount;
                int remainder = rows % perCount;
                if(remainder != 0){
                    count ++;
                }else {
                    remainder = perCount;
                }
                threadCount = count;
                for (int i = 0; i < count; i++) {
                    //防止过快执行导致参数被覆盖
                    Thread.sleep(1000);
                    final int f = i;//传值用。
                    final TjQuotaLog quotaLogf = tjQuotaLog;//传值用。
                    final QuotaVo quotaVof = quotaVo;//传值用。
                    if (f != 0){
                        quotaVof.setStart(f*perCount);
                    }else {
                        quotaVof.setStart(0);
                    }
                    if(i+1 == count){
                        quotaVof.setRows(remainder);
                    }else {
                        quotaVof.setRows(perCount);
                    }
                    Thread th = new Thread(new Thread(){
                        public void run(){
                            logger.info("启动第 "+ (f+1) + " 个线程。 ");//只能访问外部的final变量。
                            quota(quotaLogf, quotaVof);
                        }
                    });
                    Thread.sleep(10000);//延迟10 秒 Es 保存2万条 有时超时，延迟执行减缓个线程同时执行的压力
                    th.start();
                }
            }else {
                //统计并保存
                quota(tjQuotaLog, quotaVo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 统计过程
     */
    public void quota(TjQuotaLog tjQuotaLog,QuotaVo quotaVo) {
        String time = "时间：" + startTime + "到"+ endTime +" , ";
        String status = "";
        String content = "";
        try {
            //抽取数据
            List<SaveModel> dataModels = extract(quotaVo);
            if (dataModels != null && dataModels.size() > 0) {
                //保存数据
                Boolean success = saveData(dataModels,quotaVo);
                status = success ? Contant.save_status.success : Contant.save_status.fail;
                content = success ? time+"统计保存成功" : time+"统计保存失败";
                logger.info(content + dataModels.size());
                haveThreadCount++;
            } else {
                status = Contant.save_status.fail;
                content = time + "没有抽取到数据";
                haveThreadCount++;
            }
            // 初始执行时，更新该指标为已初始执行过
            if (quotaVo.getExecuteFlag().equals("1")) {
                String sql = "UPDATE tj_quota SET is_init_exec = '1' WHERE id = " + quotaVo.getId();
                jdbcTemplate.update(sql);
            }
        } catch (Exception e) {
            haveThreadCount++;
            tjQuotaLog.setStatus(Contant.save_status.fail);
            tjQuotaLog.setContent(e.getMessage());
            tjQuotaLog = saveLog(tjQuotaLog);
            e.printStackTrace();
        }
        if(threadCount > 1){
            if(haveThreadCount  == threadCount){
                tjQuotaLog.setStatus(Contant.save_status.success);
                tjQuotaLog.setContent(time+"统计保存成功");
                System.out.println("指标" + tjQuotaLog.getQuotaCode() + "统计成功 结束！");
            }else {
                tjQuotaLog.setStatus(Contant.save_status.fail);
                tjQuotaLog.setContent( time+"统计保存失败");
            }
            tjQuotaLog.setEndTime(new Date());
            saveLog(tjQuotaLog);
        }else {
            tjQuotaLog.setStatus(status);
            tjQuotaLog.setContent(content);
            tjQuotaLog.setEndTime(new Date());
            saveLog(tjQuotaLog);
        }
    }

    private void deleteRecord(QuotaVo quotaVo) throws Exception {
        EsConfig esConfig = extractHelper.getEsConfig(quotaVo.getCode());
        EsConfig sourceEsConfig = extractHelper.getDataSourceEsConfig(quotaVo.getCode());
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryStringQueryBuilder termQueryQuotaCode = QueryBuilders.queryStringQuery("quotaCode:" + quotaVo.getCode().replaceAll("_", ""));
        boolQueryBuilder.must(termQueryQuotaCode);
        String start = "";
        String end = "";
        if(sourceEsConfig.getFullQuery() !=null && sourceEsConfig.getFullQuery().equals("true")){
            start = LocalDate.now().toString();
            end = start;
        }else {
            if (!StringUtils.isEmpty(startTime)) {
                start = startTime;
            }
            if (!StringUtils.isEmpty(endTime)) {
                end = endTime;
            }
        }
        RangeQueryBuilder rangeQueryStartTime = QueryBuilders.rangeQuery("quotaDate").gte(start.substring(0, 10));
        boolQueryBuilder.must(rangeQueryStartTime);
        RangeQueryBuilder rangeQueryEndTime = QueryBuilders.rangeQuery("quotaDate").lte(end.substring(0, 10));
        boolQueryBuilder.must(rangeQueryEndTime);
        boolean flag = true ;
        Client talClient = elasticSearchPool.getClient();
        Client client = elasticSearchPool.getClient();
        try {
            while (flag){
                long count = elasticsearchUtil.getTotalCount(talClient, esConfig.getIndex() ,esConfig.getType(), boolQueryBuilder);
                if(count != 0){
                    boolean successFlag = elasticsearchUtil.queryDelete(client, esConfig.getIndex() ,esConfig.getType(),boolQueryBuilder);
                    if(!successFlag){
                        throw  new Exception("Elasticsearch 指标统计时原始数据删除失败");
                    }
                }else {
                    flag = false ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw  new Exception("Elasticsearch 指标统计时原始数据删除异常");
        } finally {
            talClient.close();
            client.close();
            logger.debug(quotaVo.getCode()+" delete success");
        }
    }

    /**
     * 抽取数据
     * @return
     */
    private List<SaveModel> extract(QuotaVo quotaVo) throws Exception {
        return SpringUtil.getBean(ExtractHelper.class).extractData(quotaVo, startTime, endTime, timeLevel, saasid);
    }

    /**
     * 初始化参数
     * @param context
     */
    private void initParams(JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        Map<String, Object> params = context.getJobDetail().getJobDataMap();
        Object object = map.get("quota");
        if (object != null) {
            BeanUtils.copyProperties(object, this.quotaVo);
        }
        this.saasid = map.getString("saasid");
        // 默认按天，如果指标有配置时间维度，ES抽取过程中维度字典项转换为 SaveModel 时再覆盖。
        this.timeLevel = Contant.main_dimension_timeLevel.day;
        this.executeFlag = map.getString("executeFlag");
        if ("2".equals(executeFlag)) {
            if (StringUtils.isEmpty(map.getString("startTime"))) {
                startTime = Contant.main_dimension_timeLevel.getStartTime(timeLevel);
            } else {
                this.startTime = map.getString("startTime").split("T")[0] + "T00:00:00Z";
            }
            if (StringUtils.isEmpty(map.getString("endTime"))) {
                endTime = LocalDate.now().toString("yyyy-MM-dd'T'00:00:00'Z'");
            } else {
                this.endTime = map.getString("endTime").split("T")[0] + "T23:59:59Z";
            }
        }
    }

    @Transactional
    private TjQuotaLog saveLog(TjQuotaLog tjQuotaLog) {
        TjQuotaLog log =  tjQuotaLogDao.save(tjQuotaLog);
        return  log;
    }

    /**
     * 保存数据
     *
     * @param dataModels
     */
    private Boolean saveData(List<SaveModel> dataModels,QuotaVo quotaVo) {
        try {
            return SpringUtil.getBean(SaveHelper.class).save(dataModels, quotaVo);
        } catch (Exception e) {
            logger.error("save error:" + e.getMessage());
        }
        return false;
    }
}
