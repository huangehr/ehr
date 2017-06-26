package com.yihu.quota.job;

import com.yihu.quota.dao.jpa.TjQuotaLogDao;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractHelper;
import com.yihu.quota.etl.save.SaveHelper;
import com.yihu.quota.model.jpa.TjQuotaLog;
import com.yihu.quota.util.SpringUtil;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
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
        try {
            TjQuotaLog tjQuotaLog = new TjQuotaLog();
            tjQuotaLog.setQuotaCode(quotaVo.getCode());
            tjQuotaLog.setSaasId(saasid);
            tjQuotaLog.setStartTime(new Date());


            //抽取数据 如果是累加就是 List<DataModel>  如果是相除 Map<String,List<DataModel>>
            List<SaveModel> dataModels = extract();
            if(dataModels != null && dataModels.size() > 0){
                //保存数据
                Boolean success = saveDate(dataModels);
                tjQuotaLog.setStatus(success ? Contant.save_status.success : Contant.save_status.fail);
                tjQuotaLog.setEndTime(new Date());
            }else {
                tjQuotaLog.setStatus(Contant.save_status.fail);
                tjQuotaLog.setContent("error:dataModels=[]");
            }
            saveLog(tjQuotaLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 抽取数据
     *
     * @return
     */
    private List<SaveModel> extract() {
        try {
            return SpringUtil.getBean(ExtractHelper.class).extractData(quotaVo, startTime, endTime,timeLevel,saasid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
