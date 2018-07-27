package com.yihu.ehr.basic.dict.controller;

import com.yihu.ehr.basic.dict.service.SystemDictEntryService;
import com.yihu.ehr.basic.dict.service.SystemDictService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.dict.DictEntryKey;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.15 18:25
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "DictionaryEntry", description = "系统全局字典项管理", tags = {"系统字典-系统全局字典项管理"})
public class SystemDictEntryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private SystemDictService dictService;
    @Autowired
    private SystemDictEntryService systemDictEntryService;

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.GET)
    public List<MDictionaryEntry> getDictEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<SystemDictEntry> systemDictEntryList = systemDictEntryService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response,systemDictEntryService.getCount(filters), page, size);
        return (List<MDictionaryEntry>)convertToModels(systemDictEntryList,new ArrayList<MDictionaryEntry>(systemDictEntryList.size()),MDictionaryEntry.class,null);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MDictionaryEntry createDictEntry (
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestBody String entryJson) throws IOException{
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);
        SystemDict systemDict = dictService.retrieve(entry.getDictId());
        if (systemDict == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "所属字典不存在");
        }
        int nextSort = systemDictEntryService.getNextSN(entry.getDictId());
        entry.setSort(nextSort);
        systemDictEntryService.createDictEntry(entry);

        return convertToModel(entry, MDictionaryEntry.class, null);
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.GET)
    public MDictionaryEntry getDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", required = true)
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典项代码", required = true)
            @PathVariable(value = "code") String code) {
        SystemDictEntry systemDictEntry = systemDictEntryService.getDictEntry(dictId, code);

        return convertToModel(systemDictEntry, MDictionaryEntry.class);
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) throws Exception{

        systemDictEntryService.deleteDictEntry(dictId, code);
        return true;
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MDictionaryEntry updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestBody String entryJson) throws IOException {
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);
        SystemDictEntry temp = systemDictEntryService.retrieve(new DictEntryKey(entry.getCode(), entry.getDictId()));
        if (null == temp) {
            throw new ApiException(ErrorCode.NOT_FOUND, "字典项不存在");
        }

        systemDictEntryService.saveDictEntry(entry);

        return convertToModel(entry, MDictionaryEntry.class, null);
    }

    @RequestMapping(value = "/dictionaries/existence/{dict_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据dictId和code判断提交的字典项名称是否已经存在")
    public boolean isDictEntryCodeExists(
            @ApiParam(name = "dict_id", value = "dict_id", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code){
        return systemDictEntryService.isDictContainEntry(dictId, code);
    }

    @RequestMapping(value ="/dictionaries/systemDictEntryList/{dict_id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据dictId获取所有字典项")
    public ListResult GetSystemDictEntryListByDictId(
            @ApiParam(name = "dict_id", value = "dict_id", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId) throws Exception{
        int page=0;
        int size=1000;
        ListResult re = new ListResult(page,size);
        Page<SystemDictEntry> cardList = systemDictEntryService.findByDictId(dictId, page,size);
        if(cardList!=null) {
            re.setDetailModelList(cardList.getContent());
            re.setTotalCount(cardList.getTotalPages());
        }
        return re;
    }

    @RequestMapping(value ="/dictionary/entryList/{dictId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据dictId获取所有字典项")
    public Envelop listByDictId(
            @ApiParam(name = "dictId", value = "dictId", required =  true)
            @PathVariable(value = "dictId") long dictId) {
        Envelop envelop = new Envelop();
        int page = 0;
        int size = 1000;
        //ListResult re = new ListResult(page, size);
        Page<SystemDictEntry> page1 = systemDictEntryService.findByDictId(dictId, page,size);
        if(page1 != null) {
            envelop.setDetailModelList(page1.getContent());
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }
    @ApiOperation(value = "未登录获取字典项")
    @RequestMapping(value = ServiceApi.SystemDict.GetDictEntryByDictIdAndEntryCode, method = RequestMethod.GET)
    public Envelop getDictEntryByDictIdAndEntryCode(
            @ApiParam(name = "dictId", value = "字典ID", required = true)
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "code", value = "字典项代码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop =new Envelop();
        SystemDictEntry systemDictEntry = systemDictEntryService.getDictEntry(dictId, code);
        MDictionaryEntry mDictionaryEntry=convertToModel(systemDictEntry, MDictionaryEntry.class);
        envelop.setObj(mDictionaryEntry);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.SystemDict.getDictEntryCodeAndValueByDictId, method = RequestMethod.GET)
    @ApiOperation("根据字典id获取所有字典项的code和值")
    public Envelop getDictEntryCodeAndValueByDictId(
            @ApiParam(name = "dictId", value = "字典id")
            @RequestParam(value = "dictId", required = false) String dictId) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> map = new HashMap<>();
        List list = systemDictEntryService.getDictEntryCodeAndValueByDictId(dictId);
        String code = "";
        String value = "";
        for (int i = 0; i < list.size(); i++) {
            Object[] obj = (Object[]) list.get(i);
            if (null != obj[0] && null != obj[1]) {
                code = obj[0].toString();
                value = obj[1].toString();
                map.put(code, value);
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(map);
        return envelop;
    }

}
