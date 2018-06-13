package com.yihu.ehr.analyze.job;

import com.yihu.ehr.analyze.service.dataQuality.WarningQuestionService;
import com.yihu.ehr.lang.SpringContext;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;
import org.springframework.stereotype.Component;

/**
 * 预警问题生成job
 * @author yeshijie on 2018/6/11.
 */
@Component
public class WarningQuestionJob implements InterruptableJob {

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        WarningQuestionService service = SpringContext.getService(WarningQuestionService.class);
        service.analyze();
    }
}
