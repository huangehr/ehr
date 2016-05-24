package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsDimension;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface DimensionClient {

    @ApiOperation("创建维度")
    @RequestMapping(value = "/dimensions",method = RequestMethod.POST)
    MRsDimension createDimension(
            @RequestParam(value = "dimension") String dimension);

    @ApiOperation("更新维度")
    @RequestMapping(value = "/dimensions",method = RequestMethod.PUT)
    MRsDimension updateDimension(
            @RequestParam(value = "dimension") String dimension);

    @ApiOperation("维度删除")
    @RequestMapping(value = "/dimensions/{id}", method = RequestMethod.DELETE)
    boolean deleteDimension(
            @PathVariable(value = "id") String id);

    @ApiOperation("维度查询")
    @RequestMapping(value = "/dimensions", method = RequestMethod.GET)
    Page<MRsDimension> queryDimensions(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

}
