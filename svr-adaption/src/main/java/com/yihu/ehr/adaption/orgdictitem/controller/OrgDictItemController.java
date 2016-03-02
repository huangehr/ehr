package com.yihu.ehr.adaption.orgdictitem.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItem;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MOrgDictItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter/org")
@Api(protocols = "https", value = "orgdictitem", description = "机构字典项", tags = {"机构字典项"})
public class OrgDictItemController extends ExtendController<MOrgDictItem> {

    @Autowired
    private OrgDictItemService orgDictItemService;

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项信息")
    public MOrgDictItem getOrgDictItem(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

        return getModel(orgDictItemService.retrieve(id));
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    public MOrgDictItem createOrgDictItem(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDictItem orgDictItem = jsonToObj(model, OrgDictItem.class);
        if (orgDictItemService.isExistOrgDictItem(orgDictItem.getOrgDict(), orgDictItem.getOrganization(), orgDictItem.getCode()))
            throw new ApiException(ErrorCode.RepeatOrgDictItem, "该字典项已存在!");

        if (orgDictItem.getSort() == 0)
            orgDictItem.setSort(orgDictItemService.getNextSort(orgDictItem.getOrgDict()));

        orgDictItem.setCreateDate(new Date());
        return getModel(orgDictItemService.createOrgDictItem(orgDictItem));
    }


    @RequestMapping(value = "/item/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public boolean deleteOrgDictItem(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id", required = false) long id) throws Exception{

        orgDictItemService.delete(id);
        return true;
    }


    @RequestMapping(value = "/items", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典项")
    public boolean deleteOrgDictItemList(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        if (ids != null && ids.length() > 0)
            orgDictItemService.delete(ids.split(","));
        return true;
    }


    @RequestMapping(value = "/item", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项")
    public MOrgDictItem updateDictItem(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDictItem dataModel = jsonToObj(model, OrgDictItem.class);
        OrgDictItem orgDictItem = orgDictItemService.retrieve(dataModel.getId());
        if (orgDictItem == null) {
            throw errNotFound();
        } else {
            //重复验证
            boolean updateFlg = orgDictItem.getCode().equals(dataModel.getCode())
                    || !orgDictItemService.isExistOrgDictItem(dataModel.getOrgDict(), dataModel.getOrganization(), dataModel.getCode());
            if (updateFlg) {
                dataModel.setUpdateDate(new Date());
                return getModel(orgDictItemService.save(dataModel));
            }
            throw new ApiException(ErrorCode.RepeatOrgDictItem, "该字典项已存在!");
        }
    }


    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    public Collection<MOrgDictItem> searchOrgDictItems(
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

        List appList = orgDictItemService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgDictItemService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MOrgDictItem.class, fields);
    }

    @RequestMapping(value = "/items/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典项下拉")
    public List<String> getOrgDictEntry(
            @ApiParam(name = "orgDictSeq", value = "字典seq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) throws Exception{

        List<OrgDictItem> orgDictItemList = orgDictItemService.findByDict(orgDictSeq, orgCode);
        List<String> orgDictItems = new ArrayList<>();
        for (OrgDictItem orgDictItem : orgDictItemList) {
            orgDictItems.add(String.valueOf(orgDictItem.getSequence()) + ',' + orgDictItem.getName());
        }
        return orgDictItems;
    }
}
