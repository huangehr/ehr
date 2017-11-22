package com.yihu.ehr.api.rhip;

import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.feign.ConventionalDictEntryClient;
import com.yihu.ehr.feign.GeographyClient;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HZY
 * @version 1.0
 * @created 2017.11.15 14:00
 */
@EnableFeignClients
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rhip/profile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "organizations", description = "区域卫生信息平台-即时业务档案上传服务", tags = {"区域卫生信息平台","即时业务档案"})
public class RhipProfileEndPoint extends BaseController {





    /**
     *  上传健康档案数据集
     * @param idCardNo
     * @param orgCode
     * @param data
     *  data    :格式
        {
            "inner_version": "000000000000",
            "patient_id": "10295514",
            "event_no": "000622508",
            "org_code": "41872607-9",
            "event_time": "2015-10-05T00:00:00Z",
            "create_date": "2015-11-05T17:30:56Z",
            "data": {
                "数据集编码1": [
                    {
                        "数据元编码1": "段廷兰",
                        "数据元编码2": "2",
                        "数据元编码3": "1951-11-30 00:00:00",
                        "数据元编码4": "412726195111306268"
                    }
                ],
                "数据集编码2": []
            }
        }
     * @return
     */
    @RequestMapping(value = "/dataset/upload" , method = RequestMethod.POST)
    @ApiOperation(value = "上传健康档案数据集")
    public Envelop create(
            @ApiParam(name = "idCardNo", value = "身份证号", defaultValue = "")
            @RequestParam(value = "idCardNo", required = true) String idCardNo,
            @ApiParam(name = "orgCode", value = "组织机构代码", defaultValue = "")
            @RequestParam(value = "orgCode", required = true) String orgCode,
            @ApiParam(name = "data", value = "健康档案内容", defaultValue = "")
            @RequestParam(value = "data", required = true) String data){
        try {
            String errorMsg = "";

            return success("");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }



}
