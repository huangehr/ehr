package com.yihu.ehr.event.job;

import com.yihu.ehr.event.chain.EventDealChain;
import com.yihu.ehr.event.listener.EventListener;
import com.yihu.ehr.event.model.Event;
import com.yihu.ehr.lang.SpringContext;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;
import org.springframework.stereotype.Component;

/**
 * 事件处理任务
 * Created by progr1mmer on 2018/7/13.
 */
@Component
@DisallowConcurrentExecution
public class EventProcessJob implements InterruptableJob {

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    public void execute(JobExecutionContext context) {
        Event event = EventListener.poll();
        if (event != null) {
            EventDealChain dealChain = SpringContext.getService(EventDealChain.class);
            dealChain.deal(event);
        }
    }

}
