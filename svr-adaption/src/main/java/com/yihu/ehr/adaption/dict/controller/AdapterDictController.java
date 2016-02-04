package com.yihu.ehr.adaption.dict.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.dict.service.AdapterDict;
import com.yihu.ehr.adaption.dict.service.AdapterDictManager;
import com.yihu.ehr.adaption.dict.service.AdapterDictModel;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterDict")
@Api(protocols = "https", value = "adapterDict", description = "适配字典管理接口", tags = {"适配字典管理"})
public class AdapterDictController extends BaseRestController {

    @Autowired
    AdapterDictManager adapterDictManager;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "字典适配关系分页查询")
    public Result searchAdapterDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows){

        Result result = new Result();
        try {
            List<AdapterDictModel> dict = adapterDictManager.searchAdapterDict(adapterPlanId, strKey, page, rows);
            int totalCount = adapterDictManager.searchDictInt(adapterPlanId, strKey);
            result.setSuccessFlg(true);
            result = getResult(dict,totalCount,page,rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "/itemsPage", method = RequestMethod.GET)
    @ApiOperation(value = "字典项适配关系分页查询")
    public Result searchAdapterDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "adapterPlanId", value = "adapterPlanId", defaultValue = "")
            @RequestParam(value = "adapterPlanId") Long adapterPlanId,
            @ApiParam(name = "dictId", value = "dictId", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "strKey", value = "查询条件", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows){
        Result result = new Result();

        List<AdapterDictModel> adapterDictModels;
        int totalCount;
        try {
            adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, strKey, page, rows);
            totalCount = adapterDictManager.searchDictEntryInt(adapterPlanId, dictId, strKey);
            result.setSuccessFlg(true);
            result = getResult(adapterDictModels,totalCount,page,rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    public Result getAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id){
        Result result = new Result();
        try {
            AdapterDict adapterDict = getAdapterDict(id);
            result.setObj(adapterDict);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项映射关系")
    public boolean updateAdapterDictEntry(
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestParam(value = "adapterDictModel") String dictJsonModel){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AdapterDictModel adapterDictModel = objectMapper.readValue(dictJsonModel, AdapterDictModel.class);
            AdapterDict adapterDict = getAdapterDict(adapterDictModel.getId());
            adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
            adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
            adapterDict.setDictId(adapterDictModel.getDictId());
            adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
            adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
            adapterDict.setDescription(adapterDictModel.getDescription());
            if (adapterDictModel.getId()==null){
                adapterDictManager.addAdapterDict(adapterDict);
            }else {
                adapterDictManager.save(adapterDict);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射")
    public boolean delDictEntry(@RequestParam("id") Long[] id){
        Result result = new Result();
        try {
            if (id == null || id.length == 0) {
                result.setErrorMsg("没有要删除信息！");
                return true;
            }
            if (adapterDictManager.deleteAdapterDictRemain(id)<1){
                result.setErrorMsg("至少保留一条！");//适配的字典由数据元关联得来，字典不能全部删除
                return false;
            }
            adapterDictManager.delete(id, "id");
            return true;
        }catch (Exception e){
            return false;
        }
    }


    private AdapterDict getAdapterDict(long id){
        AdapterDict adapterDict = adapterDictManager.findOne(id);
        return adapterDict==null? new AdapterDict() : adapterDict;
    }

}
