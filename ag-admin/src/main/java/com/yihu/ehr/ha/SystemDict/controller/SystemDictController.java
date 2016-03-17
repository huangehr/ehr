package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.agModel.dict.SystemDictModel;
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
            List<SystemDictModel> systemDictModelList = (List<SystemDictModel>) convertToModels(systemDicts
                    , new ArrayList<SystemDictModel>(systemDicts.size())
                    , SystemDictModel.class
                    , null);

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
            MSystemDict systemDict = systemDictClient.createDictionary(dictJson);
            SystemDictModel systemDictModel = convertToModel(systemDict, SystemDictModel.class);

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
            MSystemDict systemDict = systemDictClient.updateDictionary(dictJson);
            SystemDictModel systemDictModel = convertToModel(systemDict, SystemDictModel.class);

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
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.GET)
    public Envelop getDictEntries(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "行数", defaultValue = "")
            @RequestParam(value = "size", required = false) Integer size) {

        try {
            ResponseEntity<Collection<MDictionaryEntry>> responseEntity = systemDictClient.getDictEntries(id, value, page, size);
            List<MDictionaryEntry> dictionaryEntries = (List<MDictionaryEntry>) responseEntity.getBody();
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
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
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


    @RequestMapping(value = "/dictionaries/existence/{dict_name}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isAppNameExists(
            @ApiParam(name = "dict_name", value = "dict_name", defaultValue = "")
            @PathVariable(value = "dict_name") String dictName) {

        return systemDictClient.isAppNameExists(dictName);
    }
}
