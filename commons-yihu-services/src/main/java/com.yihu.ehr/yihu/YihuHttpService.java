package com.yihu.ehr.yihu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.HttpClientUtil.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzp on 2016/7/28.
 * yihu服务请求
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class YihuHttpService {

    @Autowired
    ObjectMapper objectMapper;

    @Value("${yihu-service.url}")
    public String url;

    @Value("${yihu-service.clientid}")
    public String clientId;

    /**
     * yihu服务请求
     * @param api 请求api
     * @param parmam api相应参数
     * @return
     */
    public YihuResponse doPost(String api, String parmam)
    {
        YihuResponse re = new YihuResponse();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("AuthInfo", "{ \"ClientId\": " + clientId + " }");
            params.put("SequenceNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
            params.put("Api", api);
            params.put("Param", parmam);

            String result = HttpClientUtil.doPost(url, params, null, null);
            Map<String,Object> map = objectMapper.readValue(result, Map.class);

            if(map.containsKey("Code"))
            {
                re.setCode(Integer.parseInt(String.valueOf(map.get("Code"))));
            }
            if(map.containsKey("Message"))
            {
                re.setMessage(String.valueOf(map.get("Message")));
            }
            if(map.containsKey("Result"))
            {
                re.setResult(map.get("Result"));
            }
            return re;
        }
        catch (Exception ex)
        {
            re.setCode(-1);
            re.setMessage(ex.getMessage());
        }
        return re;
    }
}
