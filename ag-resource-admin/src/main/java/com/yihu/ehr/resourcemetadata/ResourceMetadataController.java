package com.yihu.ehr.resourcemetadata;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lyr on 2016/4/25.
 */
@RestController
@Api(value="ResourceMetadata",description = "资源数据元")
@RequestMapping(value= ApiVersion.Version1_0 + "/resourceMetadata")
public class ResourceMetadataController extends BaseController {

//    @Autowired
//    private IResourceMetadataService rsMetadataService;
//
//    @ApiOperation("创建资源数据元")
//    @RequestMapping(method = RequestMethod.POST)
//    public MRsResourceMetadata createResourceMetadata(
//            @ApiParam(name="metadata",value="资源数据元",defaultValue = "")
//            @RequestParam(name="metadata")String metadata) throws Exception
//    {
//        RsResourceMetadata rsMetadata = toEntity(metadata,RsResourceMetadata.class);
//        rsMetadata.setId(getObjectId(BizObject.ResourceMetadata));
//        rsMetadataService.createResourceMetadata(rsMetadata);
//        return convertToModel(rsMetadata, MRsResourceMetadata.class);
//    }
//
//    @ApiOperation("更新资源数据元")
//    @RequestMapping(method = RequestMethod.PUT)
//    public MRsResourceMetadata updateResourceMetadata(
//            @ApiParam(name="dimension",value="资源数据元",defaultValue="")
//            @RequestParam(name="dimension")String metadata) throws Exception
//    {
//        RsResourceMetadata  rsMetadata= toEntity(metadata,RsResourceMetadata.class);
//        rsMetadataService.updateResourceMetadata(rsMetadata);
//        return convertToModel(rsMetadata, MRsResourceMetadata.class);
//    }
//
//    @ApiOperation("资源数据元删除")
//    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
//    public boolean deleteResourceMetadata(
//            @ApiParam(name="id",value="资源数据元ID",defaultValue = "")
//            @PathVariable(value="id") String id) throws Exception
//    {
//        rsMetadataService.deleteResourceMetadata(id);
//        return true;
//    }
//
//    @ApiOperation("根据资源ID批量删除资源数据元")
//    @RequestMapping(method = RequestMethod.DELETE)
//    public boolean deleteResourceMetadataPatch(
//            @ApiParam(name="resourceId",value="资源ID",defaultValue = "")
//            @RequestParam(value="resourceId") String resourceId) throws Exception
//    {
//        rsMetadataService.deleteRsMetadataByResourceId(resourceId);
//        return true;
//    }
//
//    @ApiOperation("资源数据元查询")
//    @RequestMapping(value="",method = RequestMethod.GET)
//    public Page<MRsResourceMetadata> queryDimensions(
//            @ApiParam(name="fields",value="返回字段",defaultValue = "")
//            @RequestParam(name="fields",required = false)String fields,
//            @ApiParam(name="filters",value="过滤",defaultValue = "")
//            @RequestParam(name="filters",required = false)String filters,
//            @ApiParam(name="sorts",value="排序",defaultValue = "")
//            @RequestParam(name="sorts",required = false)String sorts,
//            @ApiParam(name="page",value="页码",defaultValue = "1")
//            @RequestParam(name="page",required = false)int page,
//            @ApiParam(name="size",value="分页大小",defaultValue = "15")
//            @RequestParam(name="size",required = false)int size,
//            HttpServletRequest request,
//            HttpServletResponse response) throws Exception
//    {
//        Pageable pageable = new PageRequest(reducePage(page),size);
//        long total = 0;
//        Collection<MRsResourceMetadata> rsAppMetaList;
//
//        //过滤条件为空
//        if(StringUtils.isEmpty(filters))
//        {
//            Page<RsResourceMetadata> dimensions = rsMetadataService.getResourceMetadata(sorts,reducePage(page),size);
//            total = dimensions.getTotalElements();
//            rsAppMetaList =  convertToModels(dimensions.getContent(),new ArrayList<>(dimensions.getNumber()),MRsResourceMetadata.class,fields);
//        }
//        else
//        {
//            List<RsResourceMetadata> dimensions = rsMetadataService.search(fields,filters,sorts,page,size);
//            total = rsMetadataService.getCount(filters);
//            rsAppMetaList =  convertToModels(dimensions,new ArrayList<>(dimensions.size()),MRsResourceMetadata.class,fields);
//        }
//
//        pagedResponse(request,response,total,page,size);
//        Page<MRsResourceMetadata> rsMetaPage = new PageImpl<MRsResourceMetadata>((List<MRsResourceMetadata>)rsAppMetaList,pageable,total);
//
//        return rsMetaPage;
//    }
}
