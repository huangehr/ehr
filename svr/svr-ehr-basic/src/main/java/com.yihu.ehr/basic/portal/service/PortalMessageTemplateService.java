package com.yihu.ehr.basic.portal.service;

import com.alibaba.fastjson.JSON;
import com.yihu.ehr.basic.portal.dao.PortalMessageRemindRepository;
import com.yihu.ehr.basic.portal.dao.PortalMessageTemplateRepository;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.model.portal.MFzH5Message;
import com.yihu.ehr.model.portal.MTemplateContent;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.reflection.MethodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@Service
@Transactional
@EnableFeignClients
public class PortalMessageTemplateService extends BaseJpaService<PortalMessageTemplate, PortalMessageTemplateRepository> {

    @Autowired
    private PortalMessageTemplateRepository portalMessageTemplateRepository;
    @Autowired
    private PortalMessageRemindRepository messageRemindRepository;
    @Autowired
    private FzGatewayClient fzGatewayClient;


    public PortalMessageTemplate getMessageTemplate(Long messageTemplateId) {
        return portalMessageTemplateRepository.findOne(messageTemplateId);
    }

    public void deletePortalMessageTemplate(Long messageTemplateId) {
        portalMessageTemplateRepository.delete(messageTemplateId);
    }

    /**
     * 保存挂号推送
     *
     * @param mFzH5Message
     * @param messageTemplateId
     * @throws NoSuchMethodException
     */
    public void saveH5MessagePush(MFzH5Message mFzH5Message, long messageTemplateId) throws Exception {
        PortalMessageTemplate template = portalMessageTemplateRepository.findOne(messageTemplateId);
        List<MTemplateContent> mTemplateContents = JSON.parseArray(template.getContent(), MTemplateContent.class);
        List<Map<String, String>> list = new ArrayList<>();
        for (MTemplateContent content : mTemplateContents) {
            String value = String.valueOf(MethodUtil.invokeGet(mFzH5Message, content.getCode()));
            if (value.equals("null")) {
                value = "";
            }
            Map<String, String> maps = new LinkedHashMap<>();
            maps.put("code", content.getCode());
            maps.put("name", content.getName());
            maps.put("value", value);
            list.add(maps);
        }
        String contentJson = JSON.toJSONString(list);
        ProtalMessageRemind remind = new ProtalMessageRemind();
        remind.setAppId("WYo0l73F8e");
        remind.setAppName("EHR");
        remind.setFromUserId("system");
        remind.setToUserId(fzGatewayClient.getEhrUserId(mFzH5Message.getUserId()));
        remind.setTypeId("7");//固定值
        remind.setContent(contentJson);
        remind.setWorkUri("");
        remind.setReaded(0);
        remind.setCreateDate(new Date(System.currentTimeMillis()));
        remind.setMessageTemplateId(template.getId());
        messageRemindRepository.save(remind);

    }


}
