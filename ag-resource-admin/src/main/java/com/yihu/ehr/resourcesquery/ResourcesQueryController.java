package com.yihu.ehr.resourcesquery;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/query")
@Api(value = "query", description = "资源查询服务接口")
public class ResourcesQueryController extends BaseController {
//    @Autowired
//    private IResourcesQueryService resourcesQueryService;
//
//    /*@Autowired
//    private IResourcesQueryService resourcesQueryService;*/
//
//    /**
//     *获取资源数据
//     */
//    @ApiOperation("获取资源数据")
//    @RequestMapping(value = "/getResources", method = RequestMethod.GET)
//    public DataList getResources(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode,
//                               @ApiParam("appId") @RequestParam(value = "appId", required = true) String appId,
//                               @ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
//                               @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                               @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//
//            //判断app是否有权限
//            if(true)
//            {
//                return resourcesQueryService.getResources(resourcesCode,appId,queryParams,page,size);
//            }
//            else{
//                throw new Exception("该应用[appId="+appId+"]无权访问资源[resourcesCode="+resourcesCode+"]");
//            }
//
//    }
}
