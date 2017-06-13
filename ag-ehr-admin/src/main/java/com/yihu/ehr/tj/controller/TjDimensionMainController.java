package com.yihu.ehr.tj.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjDimensionMainModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionMain;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.tj.service.TjDimensionMainClient;
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
 * @author janseny
 * @version 1.0
 * @created 2017.6.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjDimensionMain", description = "统计指标管理", tags = {"统计指标管理-主维度"})
public class TjDimensionMainController extends ExtendController<TjDimensionMain> {

    @Autowired
    TjDimensionMainClient tjDimensionMainClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;


    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionMainList, method = RequestMethod.GET)
    @ApiOperation(value = "主维度列表")
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

        ListResult listResult = tjDimensionMainClient.search(fields, filters, sorts, size, page);

        List<TjDimensionMainModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                TjDimensionMainModel tjDimensionMainModel = objectMapper.convertValue(map,TjDimensionMainModel.class);
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getDimensionMainTypeList(String.valueOf(tjDimensionMainModel.getType()));
                tjDimensionMainModel.setTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjDimensionMainModel.getStatus()));
                tjDimensionMainModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                mainModelList.add(tjDimensionMainModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainId, method = RequestMethod.GET)
    @ApiOperation(value = "获取主维度信息", notes = "主维度")
    public Envelop getTjDimensionMain(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") Integer id) {
        try {
            TjDimensionMain tjDimensionMain = tjDimensionMainClient.getTjDimensionMain(id);
            if (tjDimensionMain == null) {
                return failed("主维度信息获取失败!");
            }
            return success(tjDimensionMain);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改主维度")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjDimensionMainClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除主维度")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = tjDimensionMainClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("主维度删除失败！");
            }
        }catch (Exception e){

            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainName,method = RequestMethod.GET)
    @ApiOperation(value = "验证名称是否存在")
    public boolean isNameExists( @RequestParam(value = "name") String name){
        return tjDimensionMainClient.isNameExists(name);
    };

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainCode,method = RequestMethod.GET)
    @ApiOperation(value = "验证Code是否存在")
    public boolean isCodeExists( @RequestParam(value = "code") String code){
        TjDimensionMain tjDimensionMain = tjDimensionMainClient.getTjDimensionMain(code);
        if(tjDimensionMain != null ){
            return true;
        }else {
            return false;
        }
    };
}
