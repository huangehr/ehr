package com.yihu.ehr.adaption.orgdict.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.adaption.orgdict.service.OrgDictService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MOrgDict;
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
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter/org")
@Api(value = "orgdict", description = "机构字典管理接口", tags = {"机构字典"})
public class OrgDictController extends ExtendController<MOrgDict> {

    @Autowired
    private OrgDictService orgDictService;

    @RequestMapping(value = "/dict", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public MOrgDict getOrgDict(
            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id) throws Exception{

       return getModel(orgDictService.retrieve(id));
    }

    @RequestMapping(value = "/dict", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    public MOrgDict createOrgDict(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDict orgDict = jsonToObj(model, OrgDict.class);

        //orgDict.setCreateDate(new Date());
        return getModel(orgDictService.createOrgDict(orgDict));
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    public boolean deleteOrgDict(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

        orgDictService.deleteOrgDict(id);
        return true;
    }


    @RequestMapping(value = "/dict", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典")
    public MOrgDict updateOrgDict(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        OrgDict dataModel = jsonToObj(model, OrgDict.class);

        return getModel(orgDictService.save(dataModel));

    }



    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Collection<MOrgDict> searchOrgDicts(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        List appList = orgDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgDictService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MOrgDict.class, fields);
    }


    @RequestMapping(value = "/dict/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典下拉")
    public List getOrgDict(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode) throws Exception{

        List<OrgDict> orgDictList = orgDictService.findByField("organization", orgCode);
        List<String> orgDicts = new ArrayList<>();
        for (OrgDict orgDict : orgDictList) {
            orgDicts.add(String.valueOf(orgDict.getSequence())+','+orgDict.getName());
        }
        return orgDicts;
    }

    @RequestMapping(value = "/dict/is_exist",method = RequestMethod.GET)
    public boolean isExistDict(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "dict_code") String dictCode){
        return orgDictService.isExistOrgDict(orgCode, dictCode);
    }

    @RequestMapping(value = "/dict/org_dict",method = RequestMethod.GET)
    public MOrgDict getOrgDictBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence) {
        return convertToModel(orgDictService.getOrgDictBySequence(orgCode, sequence),MOrgDict.class);
    }
}
