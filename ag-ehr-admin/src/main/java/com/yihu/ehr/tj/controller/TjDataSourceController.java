package com.yihu.ehr.tj.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjDataSourceModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDataSource;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.tj.service.TjDataSourceClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/8.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjDataSource", description = "数据来源", tags = {"统计指标管理-数据来源"})
public class TjDataSourceController extends ExtendController<TjDataSource> {
    @Autowired
    TjDataSourceClient tjDataSourceClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceList, method = RequestMethod.GET)
    @ApiOperation(value = "数据来源表")
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

        ListResult listResult = tjDataSourceClient.search(fields, filters, sorts, size, page);
        List<TjDataSourceModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                TjDataSourceModel tjDataSourceModel = objectMapper.convertValue(map,TjDataSourceModel.class);
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getTjDataSourceTypeList(String.valueOf(tjDataSourceModel.getType()));
                tjDataSourceModel.setTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjDataSourceModel.getStatus()));
                tjDataSourceModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                mainModelList.add(tjDataSourceModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }

    }


    @RequestMapping(value = ServiceApi.TJ.AddTjDataSource, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据来源")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjDataSourceClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.DeleteTjDataSource, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据来源")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        try {
            Result result = tjDataSourceClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("数据来源删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询数据源")
    public Envelop getById(@PathVariable(value = "id") Long id) {
        try {
            TjDataSource tjDataSource = tjDataSourceClient.getById(id);
            if (null == tjDataSource) {
                return failed("获取数据源失败");
            }
            return success(tjDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(@PathVariable("name") String name) {
        return tjDataSourceClient.hasExistsName(name);
    }

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(@PathVariable("code") String code) {
        return tjDataSourceClient.hasExistsCode(code);
    }
}
