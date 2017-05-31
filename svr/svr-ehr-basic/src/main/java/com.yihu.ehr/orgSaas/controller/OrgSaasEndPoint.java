package com.yihu.ehr.orgSaas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONObject;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.orgSaas.service.OrgSaasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "OrgSaas", description = "机构saas", tags = {"机构saas"})
public class OrgSaasEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgSaasService orgSaasService;
    @ApiParam
    private ObjectMapper objectMapper;

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
    /**
     * 机构授权检查并保存
     * @return
     */
    @RequestMapping(value = "/orgSaasSave", method = RequestMethod.POST)
    @ApiOperation(value = "机构授权检查,如果被授权的机构或者区域在指定机构总不存在，这新增这条记录，否则返回地址id")
    public String saveOrgSaas(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody  String jsonData) throws Exception{
        //根据机构code和type值删除既存数据
     //   orgSaasService.deleteOrgSaas(orgCode,type);
        ObjectMapper objectMapper = new ObjectMapper();
        String[] jsonDatalist=jsonData.split("jsonData=");
        //将json串转换成对象，放进list里面
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, OrgSaas.class);
       List<OrgSaas> models = objectMapper.readValue(jsonDatalist[1],javaType);
        String addressId ="";
        addressId=orgSaasService.saveOrgSaas(models,orgCode,type);
//        if(models.size()>0){
//            for(OrgSaas os:models){
//                addressId=orgSaasService.saveOrgSaas(os);
//            }
//        }
        return addressId;

    }


}
