package com.yihu.ehr.SystemDict.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.agModel.dict.SystemDictModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.SystemDict.service.SystemDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "sys_dict", description = "系统字典接口，用于系统全局字典管理", tags = {"系统字典接口"})
public class SystemDictController extends BaseController {

    @Autowired
    private SystemDictClient systemDictClient;

    @Autowired
    private ObjectMapper objectMapper;
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
            @RequestParam(value = "page", required = false) Integer page) {

        try {
            ResponseEntity<Collection<MSystemDict>> responseEntity = systemDictClient.getDictionaries(fields, filters, sorts, size, page);
            List<MSystemDict> systemDicts = (List<MSystemDict>) responseEntity.getBody();
            List<SystemDictModel> systemDictModelList = new ArrayList<>();
            for(MSystemDict mSystemDict : systemDicts)
            {
                SystemDictModel dictModel = convertToSysDictModel(mSystemDict);
                systemDictModelList.add(dictModel);
            }

            Envelop envelop = getResult(systemDictModelList, getTotalCount(responseEntity), page, size);

            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public Envelop createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {

        try {
            SystemDictModel systemDictModel = objectMapper.readValue(dictJson,SystemDictModel.class);
            if(StringUtils.isEmpty(systemDictModel.getName()))
            {
                return failed("名称不能为空!");
            }
            if(systemDictClient.isDictNameExists(systemDictModel.getName()))
            {
                return failed("名称已存在!");
            }
            MSystemDict systemDict = convertToMSystemDict(systemDictModel);
            systemDict = systemDictClient.createDictionary(objectMapper.writeValueAsString(systemDict));
            systemDictModel = convertToSysDictModel(systemDict);

            if (systemDictModel == null) {
                return failed("字典新增失败!");
            }

            return success(systemDictModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public Envelop getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        try {
            MSystemDict systemDict = systemDictClient.getDictionary(id);
            SystemDictModel systemDictModel = convertToModel(systemDict, SystemDictModel.class);
            systemDictModel.setCreateDate(DateToString(systemDict.getCreateDate(),AgAdminConstants.DateTimeFormat));
            if (systemDictModel == null) {
                return failed("获取字典失败!");
            }

            return success(systemDictModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.PUT)
    public Envelop updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        try {
            SystemDictModel systemDictModel = objectMapper.readValue(dictJson,SystemDictModel.class);
            if(StringUtils.isEmpty(systemDictModel.getName()))
            {
                return failed("名称不能为空!");
            }

            MSystemDict systemDict = systemDictClient.getDictionary(systemDictModel.getId());
            if(systemDict==null)
            {
                return failed("系统字典不存在，请确认!");
            }
            if(!systemDict.getName().equals(systemDictModel.getName())
                    && systemDictClient.isDictNameExists(systemDictModel.getName()))
            {
                return failed("名称已存在!");
            }
            systemDict = convertToMSystemDict(systemDictModel);
            systemDict = systemDictClient.updateDictionary(objectMapper.writeValueAsString(systemDict));
            systemDictModel = convertToSysDictModel(systemDict);

            if (systemDictModel == null) {
                return failed("修改字典失败!");
            }
            return success(systemDictModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public Envelop deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        try {
            boolean bo = systemDictClient.deleteDictionary(id);
            if (!bo) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.GET)
    public Envelop getDictEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page) {

        try {
            if(size==null)
            {
                size=999;
            }
            ResponseEntity<List<MDictionaryEntry>> responseEntity = systemDictClient.getDictEntries(fields,filters,sorts,size,page);
            List<MDictionaryEntry> dictionaryEntries = responseEntity.getBody();
            List<SystemDictEntryModel> systemDictEntryModelList = (List<SystemDictEntryModel>) convertToModels(dictionaryEntries
                    , new ArrayList<SystemDictEntryModel>(dictionaryEntries.size())
                    , SystemDictEntryModel.class
                    , null);

            Envelop envelop = getResult(systemDictEntryModelList, getTotalCount(responseEntity), page, size);

            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST)
    public Envelop createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        try {

            MConventionalDict mConventionalDict = systemDictClient.createDictEntry(entryJson);
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);

            if (systemDictEntryModel == null) {
                return failed("新增失败！");
            }

            return success(systemDictEntryModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.GET)
    public Envelop getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        try {

            MDictionaryEntry mDictionaryEntry = systemDictClient.getDictEntry(id, code);
            SystemDictEntryModel systemDictEntryModel = convertToModel(mDictionaryEntry, SystemDictEntryModel.class);

            if (systemDictEntryModel == null) {
                return failed("获取字典项失败");
            }
            return success(systemDictEntryModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    public Envelop deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {

        try {
            boolean bo = systemDictClient.deleteDictEntry(id, code);

            if (!bo) {
                return failed("删除失败!");
            }

            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT)
    public Envelop updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        try {

            MConventionalDict mConventionalDict = systemDictClient.updateDictEntry(entryJson);
            SystemDictEntryModel systemDictEntryModel = convertToModel(mConventionalDict, SystemDictEntryModel.class);

            if (systemDictEntryModel == null) {
                return failed("修改失败！");
            }

            return success(systemDictEntryModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/dictionaries/existence/{dict_id}/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根基dictId和code判断提交的字典项名称是否已经存在")
    public  boolean isDictEntryCodeExists(
            @ApiParam(name = "dict_id", value = "dict_id", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return systemDictClient.isDictEntryCodeExists(dictId,code);
    }

    public SystemDictModel convertToSysDictModel(MSystemDict mSystemDict)
    {
        if(mSystemDict==null)
        {
            return null;
        }
        SystemDictModel dictModel = convertToModel(mSystemDict,SystemDictModel.class);
        dictModel.setCreateDate(DateToString(mSystemDict.getCreateDate(), AgAdminConstants.DateTimeFormat));
        return dictModel;
    }

    public MSystemDict convertToMSystemDict(SystemDictModel dictModel)
    {
        if(dictModel==null)
        {
            return null;
        }
        MSystemDict mSystemDict = convertToModel(dictModel,MSystemDict.class);
        mSystemDict.setCreateDate(StringToDate(dictModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        return  mSystemDict;
    }
}
