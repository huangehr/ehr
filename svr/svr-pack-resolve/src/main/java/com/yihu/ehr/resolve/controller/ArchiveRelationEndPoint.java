package com.yihu.ehr.resolve.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.resolve.service.profile.ArchiveRelationService;
import com.yihu.ehr.resolve.service.resource.stage2.RsDictionaryEntryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * EndPoint - 档案关联
 * Created by progr1mmer on 2018/4/4.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ArchiveRelationEndPoint", description = "档案关联", tags = {"档案解析服务-档案识别关联信息"})
public class ArchiveRelationEndPoint extends EnvelopRestEndPoint {

    private static final String INDEX = "archive_relation";
    private static final String TYPE = "info";

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private RsDictionaryEntryService rsDictionaryEntryService;

    @RequestMapping(value = ServiceApi.PackageResolve.ArchiveRelation, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联列表")
    public Envelop list(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        Page<Map<String, Object>> result = elasticSearchUtil.page(INDEX, TYPE, filters, sorts, page, size);
        //updated by zdm on 2018/07/17 ,bug 6343---start
        result.forEach(item -> {
            //卡类型编码不为空
            if (!StringUtils.isEmpty(item.get("card_type"))) {
                //获取资源字典-卡类型代码 集合
                String cardType = rsDictionaryEntryService.getRsDictionaryEntryByDictCode("STD_CARD_TYPE", item.get("card_type").toString());
                //获取字典值
                item.put("card_type", cardType);
            }
        });
        //updated by zdm on 2018/07/17---end
        Envelop envelop = getPageResult(result.getContent(), (int)result.getTotalElements(), page, size);
        return envelop;
    }

    @ApiOperation(value = "档案关联（单条）")
    @RequestMapping(value = ServiceApi.PackageResolve.ArchiveRelation, method = RequestMethod.POST)
    public Envelop archiveRelation(
            @ApiParam(name = "profileId", value = "档案ID", required = true)
            @RequestParam(value = "profileId") String profileId,
            @ApiParam(name = "idCardNo", value = "身份证号码", required = true)
            @RequestParam(value = "idCardNo") String idCardNo) throws Exception {
        archiveRelationService.archiveRelation(profileId, idCardNo);
        return success(true);
    }
}
