package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import com.yihu.ehr.resource.service.RsAdapterDictionaryService;
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
 * @created 2016.06.01 14:26
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "adapterDictionary", description = "适配字典服务")
public class AdapterDictionaryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsAdapterDictionaryService rsAdapterDictionaryService;

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries,method = RequestMethod.GET)
    @ApiOperation("查询适配字典")
    public List<MRsAdapterDictionary> getDictionaries(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsAdapterDictionary> adapterDictionaries = rsAdapterDictionaryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsAdapterDictionaryService.getCount(filters), page, size);

        return (List<MRsAdapterDictionary>) convertToModels(adapterDictionaries, new ArrayList<MRsAdapterDictionary>(adapterDictionaries.size()), MRsAdapterDictionary.class, fields);
    }


    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建适配字典")
    public MRsAdapterDictionary createDictionaries(
            @ApiParam(name = "json_data", value = "适配字典JSON", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsAdapterDictionary adapterDictionary = toEntity(jsonData, RsAdapterDictionary.class);
        adapterDictionary.setId(getObjectId(BizObject.RsAdapterDictionary));
        adapterDictionary = rsAdapterDictionaryService.save(adapterDictionary);
        return convertToModel(adapterDictionary, MRsAdapterDictionary.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新适配字典")
    public MRsAdapterDictionary updateDictionary(
            @ApiParam(name = "json_data", value = "适配字典JSON", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsAdapterDictionary adapterDictionary = toEntity(jsonData, RsAdapterDictionary.class);
        adapterDictionary = rsAdapterDictionaryService.save(adapterDictionary);
        return convertToModel(adapterDictionary, MRsAdapterDictionary.class);
    }

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionary, method = RequestMethod.DELETE)
    @ApiOperation("删除适配字典")
    public boolean deleteDictionary(
            @ApiParam(name = "id", value = "适配字典ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsAdapterDictionaryService.delete(id);
        return true;
    }


    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionary, method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配字典")
    public MRsAdapterDictionary getDictionaryById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsAdapterDictionaryService.findById(id), MRsAdapterDictionary.class);
    }

}
