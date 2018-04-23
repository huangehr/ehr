package com.yihu.ehr.resource.job;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.portal.MTemplateContent;
import com.yihu.ehr.resource.model.PortalMessageTemplate;
import com.yihu.ehr.resource.model.ProtalMessageRemind;
import com.yihu.ehr.resource.model.Registration;
import com.yihu.ehr.resource.service.PortalMessageRemindService;
import com.yihu.ehr.resource.service.PortalMessageTemplateService;
import com.yihu.ehr.resource.service.ResourceBrowseService;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.reflection.MethodUtil;
import org.apache.poi.util.StringUtil;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 健康上饶app、公众健康服务平台-满意度调查
 * Created by zdm  on 2018/4/20.
 */
@Component
public class SurveyTask {

    @Autowired
    private ResourceBrowseService resourceBrowseService;
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PortalMessageRemindService messageRemindService;
    @Autowired
    private PortalMessageTemplateService messageTemplateService;
    /**
     * 应用ID
     */
    @Value("${h5.clientId}")
    public String clientId;

    @Scheduled(cron = "0 0 7 * * ?")
    private void startTask() throws  Exception{
        System.out.println("--------------开始-------------------");
        //检索条件
        String date = DateUtil.getNowDate(DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String queryParams = "create_date:["+date+"T00:00:00Z  TO "+date+ "T23:59:59Z"+"] AND event_type: \"0\"";
//        String queryParams = "create_date:[2018-01-29T10:00:00Z  TO 2018-01-29T23:59:59Z"+"] AND event_type: \"0\" AND  demographic_id: \"362321199203110529\"";
        //获取消息总条数 门诊 时间 create_date:"2018-01-29T22:52:08Z" AND event_type: "0"
        // "demographic_id": "362321199203110529",
        int t=0;
        int count = (int)(solrUtil.count("HealthProfile", queryParams));
        int size = 1000;
        int totalPages = count%size == 0 ? count/size : (count/size)+1;
        ProtalMessageRemind messageRemind = null;
        for(int i=0; i<totalPages ;i++){
            //查找档案
            Page<Map<String, Object>> result = resourceBrowseService.getEhrCenter(queryParams, totalPages, size);
            List list = result.getContent();
            List<Map<String, String>> contentList = new ArrayList<>();
            Registration newEntity = null;
            if(null != list && list.size()>0){
                //获取消息模板
                List<PortalMessageTemplate> messageTemplateList = messageTemplateService.getMessageTemplate("0","100","0");
                List<MTemplateContent> mTemplateContents = null;
                if(null != messageTemplateList && messageTemplateList.size()>0) {
                    PortalMessageTemplate template = messageTemplateList.get(0);
                    mTemplateContents = JSON.parseArray(template.getContent(), MTemplateContent.class);
                    for (Object obj : list) {
                        Map<String, Object> map = (Map) obj;
                        newEntity = new Registration();
                        //身份证号码 没有用户id 表示该用户未注册
                        String userId = "";
                        if (null != map.get("demographic_id") && !StringUtils.isEmpty(map.get("demographic_id").toString())&&map.get("demographic_id").toString().length()<19) {
                            newEntity.setCardNo(map.get("demographic_id").toString());
                            userId = messageTemplateService.getUserIdByIdCardNo(newEntity.getCardNo());
                        }
                        if (!StringUtils.isEmpty(userId)) {
                            if (null != map.get("org_name")) {
                                newEntity.setHospitalName(map.get("org_name").toString());
                            }
                            if (null != map.get("EHR_000082")) {
                                newEntity.setDeptName(map.get("EHR_000082").toString());
                            }
                            if (null != map.get("EHR_000079")) {
                                newEntity.setDoctorName(map.get("EHR_000079").toString());
                            }
                            if (null != map.get("EHR_000065")) {
                                newEntity.setRegisterDate(map.get("EHR_000065").toString());
                            }
                            if (null != map.get("EHR_000004")) {
                                newEntity.setPatientName(map.get("EHR_000004").toString());
                            }
                        for (MTemplateContent content : mTemplateContents) {
                            String value = "";
                            if (null != newEntity) {
                                value = String.valueOf(MethodUtil.invokeGet(newEntity, content.getCode())) == null ? "" : String.valueOf(MethodUtil.invokeGet(newEntity, content.getCode()));
                            }
                            if (value.equals("null")) {
                                value = "";
                            }
                            Map<String, String> maps = new LinkedHashMap<>();
                            maps.put("code", content.getCode());
                            maps.put("name", content.getName());
                            maps.put("value", value);
                            contentList.add(maps);
                        }
                        String contentJson = JSON.toJSONString(contentList);
                        messageRemind = new ProtalMessageRemind();
                        messageRemind.setAppId(clientId);
                        messageRemind.setAppName("健康上饶App");
                        messageRemind.setFromUserId("system");
                        messageRemind.setCreateDate(new Date());
                        messageRemind.setNotifieFlag("0");//未评价
                        messageRemind.setPortalMessagerTemplateType("100");
                        messageRemind.setTypeId("7");//健康上饶App消息固定值
                        messageRemind.setWorkUri("");
                        messageRemind.setMessageTemplateId(template.getId());
                        messageRemind.setReaded(0);//未读
                        messageRemind.setToUserId(userId);
                        messageRemind.setContent(contentJson);
                        messageRemindService.save(messageRemind);
                        t++;
                    }

                }

                }else{
                    throw  new Exception("消息模板不存在！");
                }

            }
        }

    }

}
