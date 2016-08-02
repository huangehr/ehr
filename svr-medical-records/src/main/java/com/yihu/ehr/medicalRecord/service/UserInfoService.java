package com.yihu.ehr.medicalRecord.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecord.dao.intf.UserInfoDao;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.model.User;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.HttpClientUtil.HttpClientUtil;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.web.RestTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Guo Yanshan on 2016/7/12.
 */
@Service
@Transactional
public class UserInfoService{

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RestTemplates restTemplates;
    @Value("${service-gateway.wsurl}")
    public String wsurl;
    @Value("${service-gateway.WSclientId}")
    public String WSclientId;

    private Map<String, Object> getLoginParam(String api, String parma) {
        Map<String, Object> param = new HashMap<>();
        param.put("AuthInfo", "{ \"ClientId\": " + WSclientId + " }");
        param.put("SequenceNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        param.put("Api", api);
        param.put("Param", parma);
        return param;
    }

    public Object getUserInfo(String id)throws Exception{
        Map<String, Object> params = getLoginParam("UserMgmt.User.queryUserInfoByID", "{ \"UserID\":\"" + id + "\"}");
        String result,result1;
        MrPatientsEntity mrPatientsEntity=new MrPatientsEntity();
        result1 = restTemplates.doPost(wsurl, params,Map.class);
        result = HttpClientUtil.doPost(wsurl, params,null,null);
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.ISO8601Pattern));
        Map map=objectMapper.readValue(result, Map.class);

       return map.get("Result");

        //JSONObject myJsonObject = new JSONObject(result);


    }
}
