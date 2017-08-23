package com.yihu.ehr.quota.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjDimensionSlaveModel;
import com.yihu.ehr.agModel.tj.TjQuotaDimensionSlaveModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.quota.service.TjDimensionSlaveClient;
import com.yihu.ehr.quota.service.TjQuotaDimensionSlaveClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.6.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjDimensionSlave", description = "统计指标管理", tags = {"统计指标管理-从维度"})
public class TjDimensionSlaveController extends ExtendController<TjDimensionSlave> {

    @Autowired
    TjDimensionSlaveClient tjDimensionSlaveClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    TjQuotaDimensionSlaveClient tjQuotaDimensionSlaveClient;


    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "从维度列表")
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

        List<TjDimensionSlaveModel> mainModelList  = new ArrayList<>();
        ListResult listResult = tjDimensionSlaveClient.search(fields, filters, sorts, size, page);

        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                TjDimensionSlaveModel tjDimensionSlaveModel = objectMapper.convertValue(map,TjDimensionSlaveModel.class);
                if(tjDimensionSlaveModel.getCreateTime() != null){
                    Date createTime = DateUtil.parseDate(tjDimensionSlaveModel.getCreateTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjDimensionSlaveModel.setCreateTime( DateTimeUtil.simpleDateTimeFormat(createTime));
                }
                if(tjDimensionSlaveModel.getUpdateTime() != null){
                    Date updateTime = DateUtil.parseDate(tjDimensionSlaveModel.getUpdateTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjDimensionSlaveModel.setUpdateTime( DateTimeUtil.simpleDateTimeFormat(updateTime));
                }
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getDimensionSlaveTypeList(String.valueOf(tjDimensionSlaveModel.getType()));
                tjDimensionSlaveModel.setTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjDimensionSlaveModel.getStatus()));
                tjDimensionSlaveModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                mainModelList.add(tjDimensionSlaveModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }



    }

    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionSlaveInfoList, method = RequestMethod.GET)
    @ApiOperation(value = "从维度列表")
    public Envelop getTjDimensionSlaveInfoList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "filter", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filter", required = true) String filter,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        List<TjDimensionSlaveModel> mainModelList = new ArrayList<>();
        ListResult listResult = tjDimensionSlaveClient.search(fields, filters, sorts, size, page);

        if (listResult.getTotalCount() != 0) {
            List<Map<String, Object>> modelList = listResult.getDetailModelList();
            for (Map<String, Object> map : modelList) {
                TjDimensionSlaveModel tjDimensionSlaveModel = objectMapper.convertValue(map, TjDimensionSlaveModel.class);
                mainModelList.add(tjDimensionSlaveModel);
            }
            ListResult listResult2 = tjQuotaDimensionSlaveClient.search(fields, filter, sorts, size, page);
            List<TjQuotaDimensionSlaveModel> tjQuotaDimensionSlaveModelList = new ArrayList<>();
            if (listResult2.getTotalCount() != 0) {
                List<Map<String, Object>> modelList2 = listResult2.getDetailModelList();
                for (Map<String, Object> map : modelList2) {
                    TjQuotaDimensionSlaveModel tjQuotaQuotaDimensionSlaveModel = objectMapper.convertValue(map, TjQuotaDimensionSlaveModel.class);
                    TjDimensionSlave tjDimensionSlave = tjDimensionSlaveClient.getTjDimensionSlave(tjQuotaQuotaDimensionSlaveModel.getSlaveCode());
                    if (tjDimensionSlave != null) {
                        tjQuotaQuotaDimensionSlaveModel.setName(tjDimensionSlave.getName());
                    }
                    tjQuotaDimensionSlaveModelList.add(tjQuotaQuotaDimensionSlaveModel);
                }
            }
            List<String> list = getTjQuotaDimensionMainOfSlaveCode(filter, sorts);
            for (int i = 0; i < mainModelList.size(); i++) {
                if (list.contains(mainModelList.get(i).getCode())) {
                    mainModelList.get(i).setChecked(true);
                } else {
                    mainModelList.get(i).setChecked(false);
                }
            }
            Envelop result = getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
            result.setObj(tjQuotaDimensionSlaveModelList);
            return result;
        } else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改从维度")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjDimensionSlaveClient.add(model);
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

    @RequestMapping(value =  ServiceApi.TJ.TjDimensionSlaveId, method = RequestMethod.GET)
    @ApiOperation(value = "获取从维度信息", notes = "从维度")
    public Envelop getTjDimensionSlave(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") Integer id) {
        try {
            TjDimensionSlave tjDimensionSlave = tjDimensionSlaveClient.getTjDimensionSlave(id);
            if (tjDimensionSlave == null) {
                return failed("从维度信息获取失败!");
            }
            return success(tjDimensionSlave);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从维度")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        try {
            Result result = tjDimensionSlaveClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("从维度删除失败！");
            }
        }catch (Exception e){

            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlaveName,method = RequestMethod.GET)
    @ApiOperation(value = "验证名称是否存在")
    public boolean isNameExists( @RequestParam(value = "name") String name){
        return tjDimensionSlaveClient.isNameExists(name);
    };

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlaveCode,method = RequestMethod.GET)
    @ApiOperation(value = "验证Code是否存在")
    public boolean isCodeExists( @RequestParam(value = "code") String code){
        TjDimensionSlave tjDimensionSlave = tjDimensionSlaveClient.getTjDimensionSlave(code);
        if(tjDimensionSlave != null ){
            return true;
        }else {
            return false;
        }
    };

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取从维度子表中的所有mainCode")
    public List<String> getTjQuotaDimensionMainOfSlaveCode(String filters, String sorts) {
        List<String> list = new ArrayList<>();
        ListResult listResult = tjQuotaDimensionSlaveClient.getTjQuotaDimensionSlaveAll(filters, sorts);
        List<TjQuotaDimensionSlaveModel>  tjQuotaDimensionSlaveModelList = new ArrayList<>();
        if (listResult.getTotalCount() != 0) {
            List<Map<String, Object>> modelList = listResult.getDetailModelList();
            for (Map<String, Object> map : modelList) {
                TjQuotaDimensionSlaveModel tjQuotaQuotaDimensionSlaveModel = objectMapper.convertValue(map, TjQuotaDimensionSlaveModel.class);
                tjQuotaDimensionSlaveModelList.add(tjQuotaQuotaDimensionSlaveModel);
            }
            for (int i=0; i<tjQuotaDimensionSlaveModelList.size(); i++) {
                list.add(tjQuotaDimensionSlaveModelList.get(i).getSlaveCode());
            }
        }
        return list;
    }
}
