//package com.yihu.ehr.resource.controller;
//
//import com.yihu.ehr.api.ServiceApi;
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.constants.BizObject;
//import com.yihu.ehr.model.resource.MRsAdapterMetadata;
//import com.yihu.ehr.model.resource.MRsDictionary;
//import com.yihu.ehr.resource.model.RsAdapterMetadata;
//import com.yihu.ehr.resource.model.RsDictionary;
//import com.yihu.ehr.resource.service.AdapterMetadataService;
//import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * @author linaz
// * @created 2016.06.01 14:26
// */
//@RestController
//@RequestMapping(value = ApiVersion.Version1_0)
//@Api(value = "adapterDictionary", description = "适配字典服务")
//public class AdapterDictionaryEndPoint extends EnvelopRestEndPoint {
//
//    @Autowired
//    private AdapterMetadataService metadataService;
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataList,method = RequestMethod.GET)
//    @ApiOperation("查询适配字典")
//    public List<MRsAdapterMetadata> getDictionaries(
//            @ApiParam(name="fields",value="返回字段",defaultValue = "")
//            @RequestParam(value="fields",required = false)String fields,
//            @ApiParam(name="filters",value="过滤",defaultValue = "")
//            @RequestParam(value="filters",required = false)String filters,
//            @ApiParam(name="sorts",value="排序",defaultValue = "")
//            @RequestParam(value="sorts",required = false)String sorts,
//            @ApiParam(name="page",value="页码",defaultValue = "1")
//            @RequestParam(value="page",required = false)int page,
//            @ApiParam(name="size",value="分页大小",defaultValue = "15")
//            @RequestParam(value="size",required = false)int size,
//            HttpServletRequest request,
//            HttpServletResponse response) throws Exception {
//        List<RsDictionary> dictionaries = dictionaryService.search(fields, filters, sorts, page, size);
//        pagedResponse(request, response, dictionaryService.getCount(filters), page, size);
//
//        return (List<MRsDictionary>) convertToModels(dictionaries, new ArrayList<MRsDictionary>(dictionaries.size()), MRsDictionary.class, fields);
//    }
//
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation("创建适配字典")
//    public MRsAdapterMetadata createDictionaries(
//            @ApiParam(name = "adapterMetadata", value = "数据元JSON", defaultValue = "")
//            @RequestBody String adapterMetadata) throws Exception {
//        RsAdapterMetadata metadata = toEntity(adapterMetadata, RsAdapterMetadata.class);
//        metadata.setId(getObjectId(BizObject.RsAdapterMetadata));
//        metadata = metadataService.saveAdapterMetadata(metadata);
//        return convertToModel(metadata, MRsAdapterMetadata.class);
//    }
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataList, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation("更新适配字典")
//    public MRsAdapterMetadata updateDictionary(
//            @ApiParam(name = "adapterMetadata", value = "数据元JSON", defaultValue = "")
//            @RequestBody String adapterMetadata) throws Exception {
//        RsAdapterMetadata metadata = toEntity(adapterMetadata, RsAdapterMetadata.class);
//        metadata = metadataService.saveAdapterMetadata(metadata);
//        return convertToModel(metadata, MRsAdapterMetadata.class);
//    }
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.DELETE)
//    @ApiOperation("删除适配字典")
//    public boolean deleteDictionary(
//            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
//            @PathVariable(value = "id") String id) throws Exception {
//        metadataService.deleteAdapterMetadata(id);
//        return true;
//    }
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataList, method = RequestMethod.DELETE)
//    @ApiOperation("批量删除适配字典")
//    public boolean deleteDictionaryBatch(
//            @ApiParam(name = "ids", value = "数据元ID", defaultValue = "")
//            @RequestParam(value = "ids") String ids) throws Exception {
//        metadataService.deleteAdapterMetadata(ids);
//        return true;
//    }
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadata, method = RequestMethod.GET)
//    @ApiOperation("根据ID获取适配字典")
//    public MRsAdapterMetadata getDictionaryById(
//            @ApiParam(name = "id", value = "id", defaultValue = "")
//            @PathVariable(value = "id") String id) throws Exception {
//        return convertToModel(metadataService.getAdapterMetadataById(id), MRsAdapterMetadata.class);
//    }
//
//
//    @RequestMapping(value = ServiceApi.Adaptions.SchemaMetadataBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation(value = "批量创建适配字典", notes = "批量创建适配数据元")
//    public boolean createRsMetaDataBatch(
//            @ApiParam(name = "json_data", value = "", defaultValue = "")
//            @RequestBody String jsonData) throws Exception {
//        RsAdapterMetadata[] adapterMetadata = toEntity(jsonData, RsAdapterMetadata[].class);
//        metadataService.batchInsertAdapterMetadata(adapterMetadata);
//        return true;
//    }
//}
