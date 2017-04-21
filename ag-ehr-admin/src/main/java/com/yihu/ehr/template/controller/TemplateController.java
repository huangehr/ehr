package com.yihu.ehr.template.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.template.TemplateModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.profile.MTemplate;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.std.service.CDAClient;
import com.yihu.ehr.template.service.TemplateClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.util.validate.ValidateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.4.16
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(protocols = "https", value = "template", description = "模版管理", tags = {"健康档案-模版管理"})
public class TemplateController extends ExtendController<TemplateModel> {

    @Autowired
    TemplateClient templateClient;

    @Autowired
    OrganizationClient organizationClient;

    @Autowired
    AddressClient addressClient;

    @Autowired
    CDAClient cdaClient;

    @RequestMapping(value = "/template", method = RequestMethod.POST)
    @ApiOperation(value = "新增模版")
    public Envelop create(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model) {

        try {
            TemplateModel templateModel = jsonToObj(model);
            ValidateResult validateResult = validate(templateModel);
            if(!validateResult.isRs())
                return failed(validateResult.getMsg());
            String filters =
                    "cdaVersion="+ templateModel.getCdaVersion() +
                    ";cdaDocumentId=" + templateModel.getCdaDocumentId() +
                    ";organizationCode=" + templateModel.getOrganizationCode();
            if(templateClient.getTemplates("id", filters, "", 1, 1).getBody().size()>0)
                return failed("该类型模版已存在！");
            templateClient.saveTemplate(model);
            return success(null);
        } catch (IOException e) {
            e.printStackTrace();
            return failed("数据模型解析错误");
        }  catch (Exception e){
            e.printStackTrace();
            return failed(e.getMessage());
        }
    }


    @RequestMapping(value = "/template/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改模版")
    public Envelop update(
            @ApiParam(name = "model", value = "数据模型")
            @RequestParam(value = "model") String model,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id,
            @ApiParam(name = "mode", value = "模式：copy（拷贝），update（更新）")
            @RequestParam(value = "mode") String mode) {

        try {
            TemplateModel templateModel = jsonToObj(model);
            ValidateResult validateResult = validate(templateModel);
            if(!validateResult.isRs())
                return failed(validateResult.getMsg());

            MTemplate template = templateClient.getTemplate(id);
            if("copy".equals(mode)
                    || !templateModel.getCdaVersion().equals(template.getCdaVersion())
                    || !templateModel.getCdaDocumentId().equals(template.getCdaDocumentId())
                    || !templateModel.getOrganizationCode().equals(template.getOrganizationCode())){
                String filters =
                        "cdaVersion="+ templateModel.getCdaVersion() +
                        ";cdaDocumentId=" + templateModel.getCdaDocumentId() +
                        ";organizationCode=" + templateModel.getOrganizationCode();
                if(templateClient.getTemplates("id", filters, "", 1, 1).getBody().size()>0)
                    return failed("该类型模版已存在！");
            }

            BeanUtils.copyProperties(templateModel, template, "cdaDocumentName", "organizationName", "pcTplURL", "mobileTplURL", "createTime", "province", "city");
            if("copy".equals(mode)){
                template.setId(0);
                template.setCreateTime(null);
                templateClient.saveTemplate(objToJson(template));
            }
            else
                templateClient.updateTemplate(id, objToJson(template));
            return success(null);
        } catch (IOException e) {
            e.printStackTrace();
            return failed("数据模型解析错误");
        }  catch (Exception e){
            e.printStackTrace();
            return failed(e.getMessage());
        }
    }


    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ApiOperation(value = "查询模版")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "searchName", value = "查询值（机构名称或模版名称）", defaultValue = "")
            @RequestParam(value = "searchName", required = false) String searchName,
            @ApiParam(name = "orgCode", value = "指定机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        if(!StringUtils.isEmpty(orgCode)){
            filters = "organizationCode="+ orgCode +";"+ filters;
            if(!StringUtils.isEmpty(searchName))
                filters = "title?"+searchName+";" + filters;
        }
        else if(!StringUtils.isEmpty(searchName)){
            filters = "title?"+searchName+" g1;" + filters;
            List<MOrganization> orgs = organizationClient.searchOrgs("orgCode,fullName", "fullName?"+searchName, "", 100, 1).getBody();
            if(orgs!=null && orgs.size()>0){
                String orgCodes = "";
                for(MOrganization org: orgs){
                    orgCodes += "," + org.getOrgCode();
                }
                filters = "organizationCode=" + orgCodes.substring(1) + " g1;" + filters;
            }
        }
        ResponseEntity<Collection<MTemplate>> rs = templateClient.getTemplates(fields, filters, sorts, size, page);
        List<MTemplate> tpls = (List<MTemplate>) rs.getBody();
        List<TemplateModel> tplModels =  convertToTplModels(tpls);
        return getResult(tplModels, getTotalCount(rs), page, size);
    }

    @RequestMapping(value = "/template/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取模版信息")
    public Envelop getById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            Envelop envelop = new Envelop();
            envelop.setObj(convertToTplModel(templateClient.getTemplate(id)));
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failed(ex.getMessage());
        }
    }


    @RequestMapping(value = "/template/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除模版")
    public Envelop delAdapterPlan(
            @ApiParam(name = "id", value = "模版编号")
            @PathVariable("id") int id) throws Exception {
        try {
            templateClient.deleteTemplate(id);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            return failed(e.getMessage());
        }
    }

    @RequestMapping(value = "/template/title/existence", method = RequestMethod.GET)
    @ApiOperation(value = "版本下标题是否不存在")
    public Envelop isNameExist(
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "title", value = "标题")
            @RequestParam(value = "title") String title,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {
        try {
            boolean b = templateClient.isNameExist(version, title, orgCode);
            Envelop envelop = success(null);
            envelop.setObj(b);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed(e.getMessage());
        }
    }

    private List<TemplateModel> convertToTplModels(List<MTemplate> tpls){
        if(tpls==null || tpls.size()==0)
            return new ArrayList<>();
        String orgFilters = "";
        for(MTemplate template : tpls){
            if(!StringUtils.isEmpty(template.getOrganizationCode()))
                orgFilters += "," + template.getOrganizationCode();
        }
        Map<String, String> orgMap = getOrgMap("orgCode=" + orgFilters.substring(1), tpls.size());
        TemplateModel templateModel;
        List<TemplateModel> tplMs = new ArrayList<>();
        for(MTemplate template : tpls){
            templateModel = convertToModel(template, TemplateModel.class);
            templateModel.setOrganizationName(orgMap.get(template.getOrganizationCode()));
            templateModel.setCdaDocumentName(getCdaName(template.getCdaDocumentId(), template.getCdaVersion()));
            tplMs.add(templateModel);
        }
        return tplMs;
    }

    private String getCdaName(String cdaId, String version){
        if(!StringUtils.isEmpty(cdaId)){
            MCDADocument document = cdaClient.getCDADocuments(version, cdaId);
            if(document!=null)
                return  document.getName();
        }
        return "";
    }

    private Map<String, String> getOrgMap(String filters, int size){
        List<MOrganization> orgs = organizationClient.searchOrgs("orgCode,fullName", filters, "", size, 1).getBody();
        Map<String, String> orgMap = new HashMap<>();
        if(orgs!=null)
            for(MOrganization org : orgs){
                orgMap.put(org.getOrgCode(), org.getFullName());
            }
        return  orgMap;
    }

    private TemplateModel convertToTplModel(MTemplate tpls){
        TemplateModel templateModel = convertToModel(tpls, TemplateModel.class);
        if(!StringUtils.isEmpty(tpls.getOrganizationCode())){
            MOrganization org = organizationClient.getOrg(tpls.getOrganizationCode());
            if(org!=null){
                templateModel.setOrganizationName(org.getFullName());
                if(!StringUtils.isEmpty(org.getLocation())){
                    MGeography geography = addressClient.getAddressById(org.getLocation());
                    if(geography!=null){
                        templateModel.setProvince(geography.getProvince());
                        templateModel.setCity(geography.getCity());
                    }
                }
            }
        }
        templateModel.setCdaDocumentName(
                getCdaName(tpls.getCdaDocumentId(), tpls.getCdaVersion()));
        return templateModel;
    }
}
