package com.yihu.ehr.analysis.controller;

import com.yihu.ehr.analysis.config.es.ElasticFactory;
import com.yihu.ehr.analysis.listener.save.impl.ESLogSaver;
import com.yihu.ehr.analysis.listener.LabelDataListener;
import com.yihu.ehr.analysis.model.OperatorDataModel;
import com.yihu.ehr.analysis.service.QuotaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chenweida on 2018/1/9.
 * 指标查询
 */
@RestController
@RequestMapping("/quota")
@Api(value = "指标查询", description = "指标查询")
public class QuotaController {

    @Autowired
    private ElasticFactory elasticFactory;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private ESLogSaver esLogSaver;

    @RequestMapping(value = "/testAdd", method = RequestMethod.POST)
    @ApiOperation(value = "测试添加数据到ES")
    public String apiAllNum(
    ) throws Exception {

        JSONObject paramsChild = new JSONObject();
        paramsChild.put("api","rhip.doctor.post");
        paramsChild.put("appKey","123");
        paramsChild.put("param","");


        JSONObject chlidrenData = new JSONObject();
        chlidrenData.put("responseTime","2017-06-13 21:02:16.487");
        chlidrenData.put("responseCode","123");
        chlidrenData.put("response","{\"successFlg\":false,\"pageSize\":10,\"currPage\":0,\"totalPage\":0,\"totalCount\":0,\"detailModelList\":null,\"obj\":{\"id\":13,\"userId\":\"0dae00035715c8906db7084cdb79f56b\",\"code\":\"CWS\",\"name\":\"陈新川\",\"pyCode\":\"cxc\",\"sex\":\"1\",\"photo\":\"0dae002258c644f6b3865e1c79974e82\",\"skill\":\"儿科\",\"workPortal\":\"http://www.baidu.com\",\"email\":\"365596@2qq.com\",\"phone\":\"13559485271\",\"secondPhone\":\"\",\"familyTel\":\"014-4578511\",\"officeTel\":\"014-4578511\",\"introduction\":\"qwerwer211\",\"jxzc\":\"4211\",\"lczc\":\"321\",\"xlzc\":\"221\",\"xzzc\":\"121\",\"status\":\"1\",\"insertTime\":\"2017-02-16 00:00:00\",\"updateTime\":\"2017-06-08 14:49:13\"},\"errorMsg\":null,\"errorCode\":0}");
        chlidrenData.put("url","http://127.0.0.1:9999/api");
        chlidrenData.put("params",paramsChild);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logType",1);
        jsonObject.put("caller","caller");
        jsonObject.put("time","2017-06-13 21:02:16.487");
        jsonObject.put("data",chlidrenData);

        OperatorDataModel operatorDataModel = new OperatorDataModel().getByJsonObject(jsonObject);
        esLogSaver.save(operatorDataModel, LabelDataListener.mongoDb_Operator_TableName);
        return null;
    }
    /**
     * {
     * "rhip.doctor.post":[{
     * "200":1
     * }],
     * "rhip.doctor.get":[{
     * "200":1605},{
     * "201":1}
     * ]
     * }
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apiSuccessFlagNum", method = RequestMethod.GET)
    @ApiOperation(value = "指标成功失败次数统计查询")
    public String apiSuccessFlagNum(
    ) throws Exception {
        return quotaService.apiSuccessFlagNum();
    }

    @RequestMapping(value = "/apiNum", method = RequestMethod.GET)
    @ApiOperation(value = "指标数目查询")
    public String apiNum(
    ) throws Exception {
        return quotaService.apiNum();
    }
}
