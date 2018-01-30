package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.model.rest.HospitalComposeModel;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ResultModel;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.vo.OrgHealthCategoryShowModel;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.server.namenode.Quota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标统计 -指标控制入口")
public class QuotaController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private TjDataSourceService dataSourceService;
    @Autowired
    private BaseStatistsService baseStatistsService;

    /**
     * 查询结果
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标执行结果分页")
    @RequestMapping(value = ServiceApi.TJ.TjGetQuotaResult, method = RequestMethod.GET)
    public Envelop getQuotaResult(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "pageNo", value = "页码", defaultValue = "0")
            @RequestParam(value = "pageNo" , required = false ,defaultValue = "0") int pageNo,
            @ApiParam(name = "pageSize", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "pageSize" , required = false ,defaultValue ="15") int pageSize
    ) {
        Envelop envelop = new Envelop();
        try {
            if(filters!=null){
                filters = URLDecoder.decode(filters, "UTF-8");
            }
            System.out.println(filters);
            List<SaveModel> saveModelList = new ArrayList<SaveModel>();
            List<Map<String, Object>> resultList = quotaService.queryResultPage(id, filters, pageNo, pageSize);
            if(resultList != null && resultList.size() > 0){
                for(Map<String, Object> map : resultList){
                    SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                    if(saveModel != null){
                        saveModelList.add(saveModel);
                    }
                }
            }
            long totalCount = quotaService.getQuotaTotalCount(id,filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(saveModelList);
            envelop.setCurrPage(pageNo);
            envelop.setPageSize(pageSize);
            envelop.setTotalCount((int) totalCount);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    /**
     * 获取指标统计不同维度结果总量
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计不同维度结果数据")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaTotalCount, method = RequestMethod.GET)
    public Envelop getQuotaTotalCount(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段多个维度用;隔开", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {

        Envelop envelop = new Envelop();
        TjQuota tjQuota = quotaService.findOne(id);
        String code = tjQuota.getCode();
        String dateType = null;
        try {
            if(filters!=null){
                filters = URLDecoder.decode(filters, "UTF-8");
                if(filters.equals("{}")){
                    filters = null;
                }
            }
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(code);
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);
            List<Map<String, Object>>  resultList = new ArrayList<>();
            String configFilter = esConfig.getFilter();
            if(StringUtils.isNotEmpty(configFilter) && quotaDataSource.getSourceCode().equals("1")){//数据源为ES库
                if(StringUtils.isNotEmpty(filters)){
                    filters += " and " + configFilter;
                }else {
                    filters = configFilter;
                }
            }
            String molecularFilter = filters;
            String denominatorFilter = filters;


            if(tjQuota.getResultGetType().equals("1")){
                //普通指标直接查询
                resultList = baseStatistsService.getQuotaResultList(code, dimension,filters,dateType);
            }else {
                if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())){//除法
                    //除法指标查询输出结果
                    molecularFilter = baseStatistsService.handleFilter(esConfig.getMolecularFilter(), molecularFilter);
                    denominatorFilter = baseStatistsService.handleFilter(esConfig.getDenominatorFilter(), denominatorFilter);
                    resultList =  baseStatistsService.divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, molecularFilter, denominatorFilter, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(),dateType);
                }else {
                    if(StringUtils.isNotEmpty(esConfig.getSuperiorBaseQuotaCode())){
                        //通过基础指标 抽取查询
                        resultList = baseStatistsService.getQuotaResultList(esConfig.getSuperiorBaseQuotaCode(), dimension,filters,dateType);
                    }
                }
            }

            List<ResultModel> resultModelList = new ArrayList<>();
            String [] dimens = dimension.split(";");
            if(resultList != null && resultList.size() > 0){
                for(Map<String, Object> map :resultList){
                    if(map.size() > 0){
                        List<String> cloumns = new ArrayList<>();
                        ResultModel resultModel = new ResultModel();
                        resultModel.setValue(map.get("result"));
                        for(int i = 0;i < dimens.length;i++){
                            cloumns.add(map.get(dimens[i]).toString());
                        }
                        resultModel.setCloumns(cloumns);
                        resultModelList.add(resultModel);
                    }
                }
            }

            envelop.setDetailModelList(resultModelList);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }


    /**
     * 根据指标code获取指标统计结果 swagger- 测试接口
     * @param
     * @return
     */
    @ApiOperation(value = "根据指标code获取指标统计结果")
    @RequestMapping(value = ServiceApi.TJ.TjGetReportQuotaResult, method = RequestMethod.GET)
    public Envelop tjGetReportQuotaResult(
            @ApiParam(name = "code", value = "指标code", required = true)
            @RequestParam(value = "code" , required = true) String code,
            @ApiParam(name = "filters", value = "检索条件 多个条件用 and 拼接 如：town=361002 and org=10000001 ", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = true) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            if(filters!=null){
                filters = URLDecoder.decode(filters, "UTF-8");
            }
            List<Map<String, Object>> result =  baseStatistsService.getSimpleQuotaReport(code,filters,dimension);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "根据编码获取指标执行结果")
    @RequestMapping(value = ServiceApi.TJ.FindByQuotaCodes, method = RequestMethod.GET)
    public Envelop findByQuotaCodes(
            @ApiParam(name = "quotaCodes", value = "指标code", required = true)
            @RequestParam(value = "quotaCodes") String quotaCodes,
            @ApiParam(name = "town", value = "区域town", required = true)
            @RequestParam(value = "town") String town) {
        if ("all".equalsIgnoreCase(town)) {
            town = "";
        }
        List<HospitalComposeModel> hospitalComposeModels = new ArrayList<>();
        List<HospitalComposeModel> hospitalComposeModelList = new ArrayList<>();
        HospitalComposeModel hospitalComposeModel = new HospitalComposeModel();
        hospitalComposeModel.setName("按性别分");
        Envelop envelop = new Envelop();
        String[] code = quotaCodes.split(",");

        List<Map<String, Object>> myListMap = new ArrayList<>();
        try {
            for (int i = 0; i < code.length; i++) {
                HospitalComposeModel hos = new HospitalComposeModel();

                List<Map<String, Object>> mapList = quotaService.queryResultPageByCode(code[i], "{\"town\":\""+ town + "\"}", 1, 10000);
                if (null != mapList && mapList.size() > 0) {
                    String title = exchangeCode(code[i]);
                    hos.setName(title);
                    Integer x1 = 0;
                    Integer x2 = 0;
                    for (Map<String, Object> map : mapList) {
                        SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                        if(saveModel != null){
                            if ("1".equals(saveModel.getSlaveKey1())) {
                                x1 += Integer.parseInt(saveModel.getResult() == null ? "0" : saveModel.getResult());
                            } else if ("2".equals(saveModel.getSlaveKey1())) {
                                x2 += Integer.parseInt(saveModel.getResult() == null ? "0" : saveModel.getResult());
                            }

                        }
                    }
                    hos.setX1(x1 + "");
                    hos.setX2(x2 + "");
                    hospitalComposeModels.add(hos);
                } else {
                    String title = exchangeCode(code[i]);
                    hos.setName(title);
                    hos.setX1("0");
                    hos.setX2("0");
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> titleMap = new HashMap<>();
                    map.put("男", 0);
                    map.put("女", 0);
                    titleMap.put("title", title);
                    map.putAll(titleMap);
                    myListMap.add(map);
                    hospitalComposeModels.add(hos);
                }
            }
            envelop.setSuccessFlg(true);

            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map1 = new HashMap<>();
            Map<String, Object> map2 = new HashMap<>();
            int sum1 = 0;
            int sum2 = 0;
            for (int i = 0; i < hospitalComposeModels.size(); i++) {
                map1.put(hospitalComposeModels.get(i).getName(), hospitalComposeModels.get(i).getX1() == null ? "0" : hospitalComposeModels.get(i).getX1());
                map2.put(hospitalComposeModels.get(i).getName(), hospitalComposeModels.get(i).getX2() == null ? "0" : hospitalComposeModels.get(i).getX2());
                sum1 += Integer.parseInt(hospitalComposeModels.get(i).getX1() == null ? "0" : hospitalComposeModels.get(i).getX1());
                sum2 += Integer.parseInt(hospitalComposeModels.get(i).getX2() == null ? "0" : hospitalComposeModels.get(i).getX2());
            }
            map1.put("name", "男");
            map1.put("sum", sum1);
            map2.put("name", "女");
            map2.put("sum", sum2);
            list.add(map1);
            list.add(map2);
            hospitalComposeModel.setChildren(list);
            hospitalComposeModelList.add(hospitalComposeModel);
            envelop.setObj(hospitalComposeModelList);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    public String exchangeCode(String code) {
        String value = "";
        switch (code) {
            case "HC_02_0101" :
                value = "nurse";
                break;
            case "HC_02_0102" :
                value = "pharmacist";
                break;
            case "HC_02_0103" :
                value = "technician";
                break;
            case "HC_02_0104" :
                value = "other";
                break;
            case "HC_02_0105" :
                value = "practitioner";
                break;
            case "HC_02_0106" :
                value = "assistant";
                break;
            case "HC_02_0107" :
                value = "othertechnician";
                break;
            case "HC_02_0108" :
                value = "adminer";
                break;
        }
        return value;
    }

}
