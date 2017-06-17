package com.yihu.ehr.tj.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjQuotaModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjQuota;
import com.yihu.ehr.entity.tj.TjQuotaDataSource;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.tj.service.TjQuotaClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/9.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjQuota", description = "统计表", tags = {"统计指标管理-统计表"})
public class TjQuotaController extends ExtendController<MTjQuotaModel> {
    @Autowired
    TjQuotaClient tjQuotaClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "统计表")
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

        ListResult listResult = tjQuotaClient.search(fields, filters, sorts, size, page);
        List<MTjQuotaModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            for(Map<String,Object> map : list){
                MTjQuotaModel tjQuotaModel = objectMapper.convertValue(map,MTjQuotaModel.class);
                if(tjQuotaModel.getCreateTime() != null){
                    Date createTime = DateUtil.parseDate(tjQuotaModel.getCreateTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setCreateTime( DateTimeUtil.simpleDateTimeFormat(createTime));
                }
                if(tjQuotaModel.getUpdateTime() != null){
                    Date updateTime = DateUtil.parseDate(tjQuotaModel.getUpdateTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setUpdateTime( DateTimeUtil.simpleDateTimeFormat(updateTime));
                }
                if(tjQuotaModel.getExecTime() != null){
                    Date execTime = DateUtil.parseDate(tjQuotaModel.getExecTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setExecTime( DateTimeUtil.simpleDateTimeFormat(execTime));
                }
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getTjQuotaExecTypeList(tjQuotaModel.getExecType());
                tjQuotaModel.setExecTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjQuotaModel.getStatus()));
                tjQuotaModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                MConventionalDict dict3 = conventionalDictClient.getTjQuotaDataLevelList(String.valueOf(tjQuotaModel.getDataLevel()));
                tjQuotaModel.setDataLevelName(dict3 == null ? "" : dict3.getValue());
                mainModelList.add(tjQuotaModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = ServiceApi.TJ.AddTjQuota, method = RequestMethod.POST)
    @ApiOperation(value = "新增统计")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        try {
            Result result = tjQuotaClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("统计源删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询指标")
    public Envelop getById(@PathVariable(value = "id") Long id) {
        try {
            MTjQuotaModel tjQuotaModel = tjQuotaClient.getById(id);
            if (null == tjQuotaModel) {
                return failed("获取指标失败");
            }
            return success(tjQuotaModel);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(@PathVariable("name") String name) {
        return tjQuotaClient.hasExistsName(name);
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(@PathVariable("code") String code) {
        return tjQuotaClient.hasExistsCode(code);
    }
}
