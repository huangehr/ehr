package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.tj.MQuotaCategory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wxw on 2017/8/31.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface QuotaCategoryClient {
    @RequestMapping(value = "/quota/getQuotaCategoryOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    List<MQuotaCategory> getQuotaCategoryOfChild();
}
