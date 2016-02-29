package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.SystemDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "sys_dict", description = "系统字典接口，用于系统全局字典管理", tags = {"系统字典接口"})
public class SystemDictController extends BaseController {

    @Autowired
    private SystemDictClient systemDictClient;

//    @Autowired
//    private BaseRestController baseRestController;

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    public Envelop getDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page){

        List<MSystemDict> systemDicts =(List<MSystemDict>)systemDictClient.getDictionaries(fields,filters,sorts,size,page);

//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(systemDicts,0,page,size);

       return envelop;
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public Envelop createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        Envelop envelop = new Envelop();
        MSystemDict systemDict = systemDictClient.createDictionary(dictJson);

        if(systemDict != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(systemDict);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典失败");
        }

        return envelop;
    }


    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public Envelop getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id){
        Envelop envelop = new Envelop();

        MSystemDict systemDict = systemDictClient.getDictionary(id);

        if(systemDict != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(systemDict);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典失败");
        }

        return envelop;
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.PUT)
    public Envelop updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        Envelop envelop = new Envelop();

        MSystemDict systemDict = systemDictClient.updateDictionary(dictJson);

        if(systemDict != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(systemDict);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典失败");
        }

        return envelop;
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public Envelop deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        Envelop envelop = new Envelop();

        Boolean bo = systemDictClient.deleteDictionary(id);

        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.GET)
    public Envelop getDictEntries(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) {

        List<MDictionaryEntry> dictionaryEntries = (List<MDictionaryEntry>)systemDictClient.getDictEntries(id,value,page,rows);

//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(dictionaryEntries,0,page,rows);

        return envelop;
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST)
    public Envelop createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict= systemDictClient.createDictEntry(entryJson);

        if(mConventionalDict != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(mConventionalDict);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典项失败");
        }

        return envelop;
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
    public Envelop getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        Envelop envelop = new Envelop();

        MDictionaryEntry mDictionaryEntry= systemDictClient.getDictEntry(id, code);

        if(mDictionaryEntry != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(mDictionaryEntry);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典项失败");
        }

        return envelop;
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    public Envelop deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {
        Envelop envelop = new Envelop();

        Boolean bo= systemDictClient.deleteDictEntry(id, code);

        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT)
    public Envelop updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        Envelop envelop = new Envelop();

        MConventionalDict mConventionalDict= systemDictClient.updateDictEntry(entryJson);

        if(mConventionalDict != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(mConventionalDict);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典项失败");
        }

        return envelop;
    }
}
