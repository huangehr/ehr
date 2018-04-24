package com.yihu.ehr.quota.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjDataSaveModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.quota.service.TjDataSaveClient;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/8.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjDataSave", description = "数据存储", tags = {"统计指标管理-数据存储"})
public class TjDataSaveController extends ExtendController<TjDataSaveModel> {
    @Autowired
    TjDataSaveClient tjDataSaveClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSaveList, method = RequestMethod.GET)
    @ApiOperation(value = "数据存储信息表")
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

        ListResult listResult = tjDataSaveClient.search(fields, filters, sorts, size, page);

        List<TjDataSaveModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                TjDataSaveModel tjDataSaveModel = objectMapper.convertValue(map,TjDataSaveModel.class);
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getTjDataSaveTypeList(String.valueOf(tjDataSaveModel.getType()));
                tjDataSaveModel.setTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjDataSaveModel.getStatus()));
                tjDataSaveModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                mainModelList.add(tjDataSaveModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = ServiceApi.TJ.AddTjDataSave, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据存储信息")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjDataSaveClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.DeleteTjDataSave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据存储信息")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        try {
            Result result = tjDataSaveClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("数据存储信息删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSaveById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询数据存储")
    public Envelop getById(@PathVariable(value = "id") Long id) {
        try {
            TjDataSaveModel tjDataSaveModel = tjDataSaveClient.getById(id);
            if (null == tjDataSaveModel) {
                return failed("获取数据存储失败");
            }
            return success(tjDataSaveModel);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/tj/dataSaveExistsName/{name}", method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(@PathVariable("name") String name) {
        return tjDataSaveClient.hasExistsName(name);
    }

    @RequestMapping(value = "/tj/dataSaveExistsCode/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(@PathVariable("code") String code) {
        return tjDataSaveClient.hasExistsCode(code);
    }
}
