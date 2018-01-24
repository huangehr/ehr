package com.yihu.ehr.analyze.job;

import com.yihu.ehr.analyze.service.pack.PackageAnalyzeService;
import com.yihu.ehr.lang.SpringContext;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;
import org.springframework.stereotype.Component;

/**
 * 档案包分析任务。
 * 采用最简单的方式将zip包解析到Hbase中，不做特殊的业务逻辑，健康档案的中间数据。
 * 之后可对解析出来的数据做资源化及数据质量控制，目前的主要用途就是数据质量控制。
 *
 * @author Airhead
 * @version 1.0
 * @created 2018.01.15
 */
@Component
public class PackageAnalyzeJob implements InterruptableJob {
    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        PackageAnalyzeService service = SpringContext.getService(PackageAnalyzeService.class);
        service.analyze();
    }
}
