package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import com.yihu.quota.model.rest.HospitalComposeModel;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ResultModel;
import com.yihu.quota.service.orgHealthCategory.OrgHealthCategoryStatisticsService;
import com.yihu.quota.service.quota.BaseStatistsService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.service.save.TjDataSaveService;
import com.yihu.quota.service.source.TjDataSourceService;
import com.yihu.quota.vo.OrgHealthCategoryShowModel;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.server.namenode.Quota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.*;


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
    private TjDataSaveService dataSaveService;

    private static final Logger log = LoggerFactory.getLogger(QuotaController.class);
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
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top
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
                TjQuotaDataSave quotaDataSave = dataSaveService.findByQuota(code);
                if(quotaDataSave != null && StringUtils.isNotEmpty(quotaDataSave.getConfigJson())){
                    JSONObject objSave = new JSONObject().fromObject(quotaDataSave.getConfigJson());
                    EsConfig esConfigSave = (EsConfig) JSONObject.toBean(objSave,EsConfig.class);
                    if(StringUtils.isEmpty(esConfig.getIndex()) || esConfig.getIndex().equals(esConfigSave.getIndex()) ){
                        if(StringUtils.isNotEmpty(filters)){
                            filters += " and " + configFilter;
                        }else {
                            filters = configFilter;
                        }
                    }
                }else {
                    if(StringUtils.isNotEmpty(filters)){
                        filters += " and " + configFilter;
                    }else {
                        filters = configFilter;
                    }
                }
            }

            // 判断该指标是否需要同比， 需要的话拼接时间条件
            if (StringUtils.isNotEmpty(esConfig.getIncrementFlag())) {
                filters = baseStatistsService.filtersExchangeHandle(filters, esConfig);
                log.info("filters = {}", filters);
            }

            String molecularFilter = filters;
            String denominatorFilter = filters;

            if (StringUtils.isNotEmpty(esConfig.getGrowthFlag())) {
                resultList = baseStatistsService.getGrowthByQuota(dimension, filters, esConfig, dateType);
            } else {
                if(tjQuota.getResultGetType().equals("1")){
                    //普通指标直接查询
                    resultList = baseStatistsService.getQuotaResultList(code, dimension, filters, dateType, top);
                }else {
                    if( (StringUtils.isNotEmpty(esConfig.getMolecular())) && StringUtils.isNotEmpty(esConfig.getDenominator())) {//除法
                        //除法指标查询输出结果
                        molecularFilter = baseStatistsService.handleFilter(esConfig.getMolecularFilter(), molecularFilter);
                        denominatorFilter = baseStatistsService.handleFilter(esConfig.getDenominatorFilter(), denominatorFilter);
                        if (StringUtils.isNotEmpty(esConfig.getDivisionType()) && esConfig.getDivisionType().equals("2")) {
                            resultList = baseStatistsService.divisionQuotaDenoConstant(esConfig.getMolecular(), dimension, molecularFilter, denominatorFilter,esConfig.getPercentOperation(), esConfig.getPercentOperationValue(), dateType, top);
                        } else {
                            resultList = baseStatistsService.divisionQuota(esConfig.getMolecular(), esConfig.getDenominator(), dimension, molecularFilter, denominatorFilter, esConfig.getPercentOperation(), esConfig.getPercentOperationValue(), dateType, top);
                        }
                    } else if (StringUtils.isNotEmpty(esConfig.getAddOperation())) {
                        String firstFilter = baseStatistsService.handleFilter(esConfig.getAddFirstFilter(), filters);
                        String secondFilter = baseStatistsService.handleFilter(esConfig.getAddSecondFilter(), filters);
                        resultList = baseStatistsService.addQuota(esConfig.getAddFirstQuotaCode(), firstFilter, esConfig.getAddSecondQuotaCode(), secondFilter, esConfig.getAddOperation(), dimension, dateType, top);
                    } else {
                        if(StringUtils.isNotEmpty(esConfig.getSuperiorBaseQuotaCode())){
                            //通过基础指标 抽取查询
                            resultList = baseStatistsService.getQuotaResultList(esConfig.getSuperiorBaseQuotaCode(), dimension,filters,dateType, top);
                        }
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
            @RequestParam(value = "dimension", required = true) String dimension,
            @ApiParam(name = "top", value = "获取前几条数据")
            @RequestParam(value = "top", required = false) String top
    ) {
        Envelop envelop = new Envelop();
        try {
            if(filters!=null){
                filters = URLDecoder.decode(filters, "UTF-8");
            }
            List<Map<String, Object>> result =  baseStatistsService.getSimpleQuotaReport(code,filters,dimension,true, top);
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

}
