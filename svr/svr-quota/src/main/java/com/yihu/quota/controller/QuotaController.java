package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.model.rest.QuotaReport;
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
    @Autowired
    private OrgHealthCategoryStatisticsService orgHealthCategoryStatisticsService;
    private static String orgHealthCategory = "orgHealthCategory";





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
            List<Map<String, Object>> resultList = quotaService.queryResultPage(id, filters, pageNo, pageSize);
            List<SaveModel> saveModelList = new ArrayList<SaveModel>();
            for(Map<String, Object> map : resultList){
                SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                if(saveModel != null){
                    saveModelList.add(saveModel);
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
        try {
            TjQuota tjQuota = quotaService.findOne(id);

//            if(数据源为 es){
//                quotaService.searcherByGroup()
//            }else if(数据源为 mysql){
//
//            }else if(数据源为 solr){
//
//            }
            QuotaReport  quotaReport = quotaService.getQuotaReport(tjQuota, filters, dimension,1000);
            envelop.setDetailModelList(quotaReport.getReultModelList());
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
     * 根据指标code获取指标统计结果
     * @param
     * @return
     */
    @ApiOperation(value = "根据指标code获取指标统计结果")
    @RequestMapping(value = ServiceApi.TJ.TjGetReportQuotaResult, method = RequestMethod.GET)
    public Envelop geQuotaReportResultByFilter(
            @ApiParam(name = "code", value = "指标code", required = true)
            @RequestParam(value = "code" , required = true) String code,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = true) String dimension,
            @ApiParam(name = "dateType", value = "时间聚合类型", defaultValue = "")
            @RequestParam(value = "dateType", required = false) String dateType
    ) {
        Envelop envelop = new Envelop();
        try {
            if(filters!=null){
                filters = URLDecoder.decode(filters, "UTF-8");
            }
            TjQuotaDataSource quotaDataSource = dataSourceService.findSourceByQuotaCode(code);
            JSONObject obj = new JSONObject().fromObject(quotaDataSource.getConfigJson());
            EsConfig esConfig= (EsConfig) JSONObject.toBean(obj,EsConfig.class);

            if( (StringUtils.isNotEmpty(esConfig.getEspecialType())) && esConfig.getEspecialType().equals(orgHealthCategory)){
                //特殊机构类型查询输出结果  只有查询条件没有维度 默认是 机构类型维度
                List<Map<String, Object>> result = baseStatistsService.getOrgHealthCategory(code,filters,dateType);
                envelop.setObj(result);
            }else if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())){//除法
                //除法指标查询输出结果
                List<Map<String, Object>> result =  baseStatistsService.divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, filters, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(),dateType);
                envelop.setObj(result);
            }else if( (StringUtils.isNotEmpty(esConfig.getThousandDmolecular())) && StringUtils.isNotEmpty(esConfig.getThousandDenominator())){//除法
                //除法指标查询输出结果
                List<Map<String, Object>> result =  baseStatistsService.divisionQuota(esConfig.getThousandDmolecular(), esConfig.getThousandDenominator(), dimension, filters, "1", esConfig.getThousandFlag(),dateType);
                envelop.setObj(result);
            }else {
                //普通指标查询
                List<Map<String, Object>>  resultMap = baseStatistsService.getQuotaResultList(code, dimension,filters,dateType);
                envelop.setObj(resultMap);
            }
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }


//    /**
//     * 根据指标code获取 指标统计结果
//     * @param
//     * @return
//     */
//    @ApiOperation(value = "获取特殊机构指标执行结果")
//    @RequestMapping(value = ServiceApi.TJ.TjGetOrgHealthCategoryQuotaResult, method = RequestMethod.GET)
//    public Envelop getOrgHealthCategoryQuotaResult(
//            @ApiParam(name = "code", value = "指标code", required = true)
//            @RequestParam(value = "code" , required = true) String code,
//            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
//            @RequestParam(value = "filters", required = false) String filters,
//            @ApiParam(name = "dimension", value = "需要统计不同维度字段", defaultValue = "")
//            @RequestParam(value = "dimension", required = false) String dimension ) {
//        Envelop envelop = new Envelop();
//        try {
//            if(filters!=null){
//                filters = URLDecoder.decode(filters, "UTF-8");
//            }
//            List<String> dimensionValList = null;
//            TjQuota tjQuota= quotaService.findByCode(code);
//            if(tjQuota != null){
//                List<Map<String, Object>> dimenListResult = quotaService.searcherByGroup(tjQuota, filters, dimension);
//                for(Map<String,Object> map : dimenListResult){
//                    dimensionValList = new ArrayList<String>(map.keySet());
//                }
//            }
//            List<Map<String,Object>> result = new ArrayList<>();
//            List<Map<String,Object>> orgHealthCategoryList = new ArrayList<>();
//
//            orgHealthCategoryList = orgHealthCategoryStatisticsService.getOrgHealthCategoryTreeByPid(-1);
//
//            Map<String, List<Map<String, Object>>>  resultMap = new HashMap<>();
//            for(String val : dimensionValList){
//                Map<String, Object> param = new HashMap<>();
//                param.put(dimension,val);
//                List<Map<String, Object>>  mapList = quotaService.queryResultPageByCode(tjQuota.getCode(), objectMapper.writeValueAsString(param), 1, 10000);
//                resultMap.put(val,mapList);
//            }
//
//            result = setResult(orgHealthCategoryList,dimensionValList,filters,dimension,tjQuota,resultMap);
//
//            envelop.setSuccessFlg(true);
//            envelop.setDetailModelList(result);
//            return envelop;
//        } catch (Exception e) {
//            error(e);
//            invalidUserException(e, -1, "查询失败:" + e.getMessage());
//        }
//        envelop.setSuccessFlg(false);
//        return envelop;
//    }


    /**
     * 从维度结果集中抽取机构类型的数据 返回机构类型树状结构数据
     * @param orgHealthCategoryList
     * @param dimensionValList
     * @param filters
     * @param dimension
     * @param tjQuota
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> setResult(List<Map<String,Object>> orgHealthCategoryList ,
                                              List<String> dimensionValList ,String filters,
                                              String dimension,TjQuota tjQuota, Map<String, List<Map<String, Object>>>  resultMap ) throws Exception {
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0 ; i < orgHealthCategoryList.size() ; i++ ){
            Map<String,Object> mapCategory = orgHealthCategoryList.get(i);
            String code = mapCategory.get("code").toString();
            for(String val : dimensionValList){
                if(resultMap.get(val) != null ){
                    for(Map<String,Object> map : resultMap.get(val)){
                        mapCategory.put(val, map.get(code) != null ? map.get(code).toString() : "0");
                    }
                }
            }
            result.add(mapCategory);
            if(mapCategory.get("children") != null){
                List<Map<String,Object>> childrenOrgHealthCategoryList = (List<Map<String, Object>>) mapCategory.get("children");
                mapCategory.put("children",setResult(childrenOrgHealthCategoryList,dimensionValList,filters,dimension,tjQuota,resultMap));
            }
        }
        return  result;
    }

}
