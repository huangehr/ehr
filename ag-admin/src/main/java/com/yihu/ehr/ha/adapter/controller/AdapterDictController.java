package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.agModel.adapter.AdapterDictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.ha.adapter.service.AdapterDictClient;
import com.yihu.ehr.ha.adapter.utils.ExtendController;
import com.yihu.ehr.model.adaption.MAdapterDict;
import com.yihu.ehr.model.adaption.MDataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter")
@RestController
public class AdapterDictController extends ExtendController<AdapterDictModel> {

    @Autowired
    AdapterDictClient adapterDictClient;


    @RequestMapping(value = "/plan/{planId}/dicts", method = RequestMethod.GET)
    public Collection<MDataSet> searchDictse(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return adapterDictClient.searchAdapterDict(planId, code, name, sorts, size, page, request, response);
    }

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/entrys", method = RequestMethod.GET)
    public Collection<MAdapterDict> getAdapterDictEntryByDictId(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") Long dictId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return adapterDictClient.searchAdapterDictEntry(planId, dictId, code, name, sorts, size, page, request, response);
    }


    @RequestMapping(value = "/dict/entry/{id}", method = RequestMethod.GET)
    public AdapterDictModel getAdapterDictEntry(
            @ApiParam(name = "id", value = "适配关系ID")
            @RequestParam(value = "id") long id) {

        return getModel(adapterDictClient.getAdapterDictEntry(id));
    }

    @RequestMapping(value = "/dict/entry", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典项映射关系")
    public Envelop createAdapterDictEntry(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDictModel adapterDictModel = jsonToObj(model);
            ValidateResult validateResult = validate(adapterDictModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            return success(adapterDictClient.createAdapterDictEntry(model));
        } catch (ApiException e){
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return failedSystem();
        }
    };


    @RequestMapping(value = "/dict/entry", method = RequestMethod.POST)
    public Envelop updateAdapterDictEntry(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            AdapterDictModel adapterDictModel = jsonToObj(model);
            ValidateResult validateResult = validate(adapterDictModel);
            if(!validateResult.isRs()){
                return failed(validateResult.getMsg());
            }
            return success(adapterDictClient.updateAdapterDictEntry(adapterDictModel.getId(), model));
        } catch (ApiException e){
            e.printStackTrace();
            return failed(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/dict/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配关系", notes = "根据适配关系ID删除适配关系，批量删除时ID以逗号隔开")
    public boolean delAdapterDictEntry(
            @ApiParam(name = "ids", value = "适配关系ID")
            @RequestParam(value = "ids") String ids) throws Exception{

        return adapterDictClient.delDictEntry(ids);
    }






    /***************************放到标准字典 以及第三方 ***************************************************************/

    @RequestMapping(value = "/getStdDictEntry", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准字典项", produces = "application/json", notes = "获取未适配的标准数据元，查询条件(mode)为 modify/view")
    public Object getStdDictEntry(
            @ApiParam(name = "adapterPlanId", value = "方案ID")
            @RequestParam(value = "adapterPlanId") long adapterPlanId,
            @ApiParam(name = "dictId", value = "标准字典ID")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "mode", value = "查询条件")
            @RequestParam(value = "mode") String mode) {
        return null;
    }

    @RequestMapping(value = "/getOrgDict", method = RequestMethod.GET)
    public Object getOrgDict(
            @ApiParam(name = "adapterPlanId", value = "方案ID")
            @RequestParam(value = "adapterPlanId") long adapterPlanId) {
        return null;
    }

    @RequestMapping("/getOrgDictEntry")
    public Object getOrgDictEntry(
            @ApiParam(name = "adapterPlanId", value = "方案ID")
            @RequestParam(value = "adapterPlanId") long adapterPlanId,
            @ApiParam(name = "orgDictId", value = "机构字典ID")
            @RequestParam(value = "orgDictId") int orgDictId) {
        return null;
    }


}
