package com.yihu.ehr.orgSaas.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.geography.service.GeographyDictService;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.orgSaas.MAreaSaas;
import com.yihu.ehr.orgSaas.service.OrgSaasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "OrgSaas", description = "机构saas", tags = {"机构saas"})
public class OrgSaasEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgSaasService orgSaasService;

    /**
     * 根据机构获取saas机构数据
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构获取saas机构数据")
    public ListResult getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception  {
        ListResult result = new ListResult();
        List<Object> OrgSaasList = orgSaasService.getOrgSaasByorgCode(orgCode,type);
        result.setDetailModelList(OrgSaasList);
        result.setSuccessFlg(true);
        result.setCode(200);
        return result;
    }




}
