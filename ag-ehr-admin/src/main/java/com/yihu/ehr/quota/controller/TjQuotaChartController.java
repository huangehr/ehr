package com.yihu.ehr.quota.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjQuotaChartModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuotaChart;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.quota.service.TjQuotaChartClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjQuotaChart", description = "统计指标管理", tags = {"统计指标管理-指标图表"})
public class TjQuotaChartController extends ExtendController<TjQuotaChart> {

    @Autowired
    TjQuotaChartClient tjQuotaChartClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "指标图表列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ListResult listResult = tjQuotaChartClient.search(fields, filters, sorts, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
           /* list = convertArApplyModels(list);*/
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改指标图表")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaChartClient.add(model);
            if (objectResult.getCode() == 200) {
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.BatchTjQuotaChart, method = RequestMethod.POST)
    @ApiOperation(value = "批量新增指标图表")
    public Envelop batchAddTjQuotaChart(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaChartClient.batchAddTjQuotaChart(model);
            if (objectResult.getCode() == 200) {
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标图表")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = tjQuotaChartClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("统计指标删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaChartId, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标图表信息", notes = "指标图表")
    public Envelop getTjQuotaChart(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") Integer id) {
        try {
            TjQuotaChart tjQuotaChart = tjQuotaChartClient.getTjQuotaChart(id);
            if (tjQuotaChart == null) {
                return failed("指标图表信息获取失败!");
            }
            return success(tjQuotaChart);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaChartList, method = RequestMethod.GET)
    @ApiOperation(value = "指标图表列表")
    public Envelop getTjQuotaChartList(
            @ApiParam(name = "dictfilter", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "dictfilter", required = false) String dictfilter,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ListResult listResult = conventionalDictClient.searchMConventionalDict(dictfilter, sorts, size, page);

        List<MConventionalDict> mainModelList = new ArrayList<>();
        if (listResult.getTotalCount() != 0) {
            List<Map<String, Object>> modelList = listResult.getDetailModelList();
            for (Map<String, Object> map : modelList) {
                MConventionalDict tjDimensionSlaveModel = objectMapper.convertValue(map, MConventionalDict.class);
                mainModelList.add(tjDimensionSlaveModel);
            }
            Map<String,String> dictMap = new HashMap<>();
            for(MConventionalDict dict:mainModelList){
                dictMap.put(dict.getCode(),dict.getValue());
            }

            ListResult listResult2 = tjQuotaChartClient.search(null, filters, sorts, size, page);
            List<MConventionalDict> checkedConventionalDicts = new ArrayList<>();
            if (listResult2.getTotalCount() != 0) {
                List<Map<String, Object>> modelList2 = listResult2.getDetailModelList();
                for (Map<String, Object> map : modelList2) {
                    TjQuotaChartModel tjQuotaChartModel = objectMapper.convertValue(map, TjQuotaChartModel.class);
                    MConventionalDict mConventionalDict = new MConventionalDict();
                    if(dictMap.containsKey(tjQuotaChartModel.getChartId())){
                        mConventionalDict.setCode(tjQuotaChartModel.getChartId());
                        mConventionalDict.setValue(dictMap.get(tjQuotaChartModel.getChartId()));
                        checkedConventionalDicts.add(mConventionalDict);
                    }
                }
            }
            List<String> list = getAllCheckedTjQuotaChart(filters);
            for (int i = 0; i < list.size(); i++) {
                if (list.contains(mainModelList.get(i).getCode())) {
                    mainModelList.get(i).setChecked(true);
                } else {
                    mainModelList.get(i).setChecked(false);
                }
            }
            Envelop result = getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
            result.setObj(checkedConventionalDicts);
            return result;
        } else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetAllTjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有选中的图表")
    public List<String> getAllCheckedTjQuotaChart(String filters) {
        List<String> list = new ArrayList<>();
        ListResult listResult = tjQuotaChartClient.getTjQuotaChartAll(filters);
        List<TjQuotaChartModel>  tjQuotaChartModels = new ArrayList<>();
        if (listResult.getTotalCount() != 0) {
            List<Map<String, Object>> modelList = listResult.getDetailModelList();
            for (Map<String, Object> map : modelList) {
                TjQuotaChartModel tjQuotaChartModel = objectMapper.convertValue(map, TjQuotaChartModel.class);
                tjQuotaChartModels.add(tjQuotaChartModel);
            }
            for (int i=0; i<tjQuotaChartModels.size(); i++) {
                list.add(tjQuotaChartModels.get(i).getChartId());
            }
        }
        return list;
    }

}
