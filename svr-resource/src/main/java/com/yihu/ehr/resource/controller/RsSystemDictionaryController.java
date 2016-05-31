package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsSystemDictionary;
import com.yihu.ehr.resource.model.RsSystemDictionary;
import com.yihu.ehr.resource.model.RsSystemDictionaryEntry;
import com.yihu.ehr.resource.service.RsSystemDictionaryEntryService;
import com.yihu.ehr.resource.service.RsSystemDictionaryService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "systemDictionaries", description = "系统字典服务接口")
public class RsSystemDictionaryController extends BaseRestController {

    @Autowired
    private RsSystemDictionaryService rsSystemDictionaryService;

    @Autowired
    private RsSystemDictionaryEntryService rsSystemDictionaryEntryService;

    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典列表", notes = "根据查询条件获取系统字典列表")
    public List<MRsSystemDictionary> searchRsSystemDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsSystemDictionary> systemDictionaries = rsSystemDictionaryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsSystemDictionaryService.getCount(filters), page, size);
        return (List<MRsSystemDictionary>) convertToModels(systemDictionaries, new ArrayList<MRsSystemDictionary>(systemDictionaries.size()), MRsSystemDictionary.class, fields);
    }



    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典", notes = "创建系统字典")
    public MRsSystemDictionary createRsSystemDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsSystemDictionary systemDictionary = toEntity(jsonData, RsSystemDictionary.class);
        String code = systemDictionary.getCode();
        if(isExistence(code)){
            throw new Exception("字典代码不能重复");
        }
        systemDictionary.setId(getObjectId(BizObject.RsSystemDictionary));
        rsSystemDictionaryService.save(systemDictionary);
        return convertToModel(systemDictionary, MRsSystemDictionary.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典", notes = "修改系统字典")
    public MRsSystemDictionary updateRsSystemDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsSystemDictionary rsSystemDictionary = toEntity(jsonData, RsSystemDictionary.class);
        String id = rsSystemDictionary.getId();
        RsSystemDictionary d = rsSystemDictionaryService.findById(id);
        String code = rsSystemDictionary.getCode();
        if(code!=d.getCode()){
            throw new Exception("字典代码不可修改");
        }
        rsSystemDictionaryService.save(rsSystemDictionary);
        return convertToModel(rsSystemDictionary, MRsSystemDictionary.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典", notes = "删除系统字典")
    public boolean deleteRsSystemDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        RsSystemDictionary systemDictionary = rsSystemDictionaryService.findById(id);
        String code = systemDictionary.getCode();
        List<RsSystemDictionaryEntry> systemDictionaryEntries = rsSystemDictionaryEntryService.findByField("dictCode",code);
        if(systemDictionaryEntries!=null && systemDictionaryEntries.size()!=0){
            throw new Exception("该字典包含字典项，不可删除");
        }
        rsSystemDictionaryService.delete(id);
        return true;
    }

    public boolean isExistence(String code) {
        return rsSystemDictionaryService.findByField("code",code) != null;
    }

}