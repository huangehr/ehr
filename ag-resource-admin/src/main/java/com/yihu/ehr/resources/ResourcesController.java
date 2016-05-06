package com.yihu.ehr.resources;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lyr on 2016/4/25.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/resources")
@Api(value = "resources", description = "资源服务接口")
public class ResourcesController extends BaseController {
//    @Autowired
//    private IResourcesService rsService;
//
//    @ApiOperation("创建资源")
//    @RequestMapping(method = RequestMethod.POST)
//    public MRsResources createResource(
//            @ApiParam(name="resource",value="资源",defaultValue = "")
//            @RequestParam(name="resource")String resource) throws Exception
//    {
//        RsResources  rs= toEntity(resource,RsResources.class);
//        rs.setId(getObjectId(BizObject.Resources));
//        rsService.createResource(rs);
//        return convertToModel(rs,MRsResources.class);
//    }
//
//    @ApiOperation("更新资源")
//    @RequestMapping(method = RequestMethod.PUT)
//    public MRsResources updateResources(
//            @ApiParam(name="resource",value="资源",defaultValue="")
//            @RequestParam(name="resource")String resource) throws Exception
//    {
//        RsResources  rs= toEntity(resource,RsResources.class);
//        rsService.updateResource(rs);
//        return convertToModel(rs,MRsResources.class);
//    }
//
//    @ApiOperation("资源删除")
//    @RequestMapping(value="/{id}",method = RequestMethod.DELETE)
//    public boolean deleteResources(
//            @ApiParam(name="id",value="资源ID",defaultValue = "")
//            @PathVariable(value="id") String id) throws Exception
//    {
//        rsService.deleteResource(id);
//        return true;
//    }
//
//    @ApiOperation("资源删除")
//    @RequestMapping(method = RequestMethod.DELETE)
//    public boolean deleteResourcesPatch(
//            @ApiParam(name="id",value="资源ID",defaultValue = "")
//            @RequestParam(name="id") String id) throws Exception
//    {
//        String[] ids = id.split(",");
//
//        for(String id_ : ids)
//        {
//            rsService.deleteResource(id_);
//        }
//
//        return true;
//    }
//
//    @ApiOperation("资源查询")
//    @RequestMapping(method = RequestMethod.GET)
//    public Page<MRsResources> queryResources(
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
//        Collection<MRsResources> rsList;
//
//        //过滤条件为空
//        if(StringUtils.isEmpty(filters))
//        {
//            Page<RsResources> resources = rsService.getResources(sorts,reducePage(page),size);
//            total = resources.getTotalElements();
//            rsList = convertToModels(resources.getContent(),new ArrayList<>(resources.getNumber()),MRsResources.class,fields);
//        }
//        else
//        {
//            List<RsResources> resources = rsService.search(fields,filters,sorts,page,size);
//            total = rsService.getCount(filters);
//            rsList = convertToModels(resources,new ArrayList<>(resources.size()),MRsResources.class,fields);
//        }
//
//        pagedResponse(request,response,total,page,size);
//        Page<MRsResources> rsPage = new PageImpl<MRsResources>((List<MRsResources>)rsList,pageable,total);
//
//        return rsPage;
//    }
}