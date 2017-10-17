package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author 张进军
 * @created 2017.06.27 17:56
 */
@ApiIgnore
@FeignClient(name = MicroServices.Package)
public interface DataSetPackageMgrClient {

    /**
     * 根据ID获取数据集档案包
     *
     * @param datasetId 数据集ID
     * @return
     */
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.DatasetPackages.Package, method = RequestMethod.GET)
    String acquireDatasetPackage(@RequestParam(value = "id") String datasetId);

    /**
     * 回写数据集档案
     * @param id
     * @param status
     * @param message
     */
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.DatasetPackages.Package, method = RequestMethod.PUT)
    void reportStatus(@PathVariable(value = "id") String id,
                      @RequestParam(value = "status") ArchiveStatus status,
                      @RequestBody String message);

}
