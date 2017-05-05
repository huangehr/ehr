package com.yihu.ehr.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 17:03
 */
@Component
public class MessageReceiver  {
    @Autowired
    MessageBuffer messageBuffer;

    @Autowired
    ObjectMapper objectMapper;

    public void receive(Object message) {
        try {
            MPackage pack = objectMapper.readValue((String)message, MPackage.class);
            LogService.getLogger().info("Receive package: " + pack.getId());

            messageBuffer.putMessage(pack);
        } catch (Exception e) {
            LogService.getLogger().error(e.getMessage());
        }
    }
}
