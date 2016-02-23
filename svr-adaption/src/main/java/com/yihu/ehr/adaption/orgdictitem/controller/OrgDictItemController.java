package com.yihu.ehr.adaption.orgdictitem.controller;

import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemManager;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.FieldCondition;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.CommonVersion + "/orgdictitem")
@Api(protocols = "https", value = "orgdictitem", description = "机构字典项", tags = {"机构字典项"})
public class OrgDictItemController extends BaseRestController {

    @Autowired
    private OrgDictItemManager orgDictItemManager;

//    @RequestMapping(value = "/info", method = RequestMethod.GET)
//    @ApiOperation(value = "获取字典项信息")
//    public Result getOrgDictItem(
//            @ApiParam(name = "id", value = "编号", defaultValue = "")
//            @RequestParam(value = "id", required = false) long id) {
//        Result result = new Result();
//        try {
//            OrgDictItem orgDictItem = orgDictItemManager.findOne(id);
//            result.setObj(orgDictItem);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ApiOperation(value = "新增字典项")
//    public Result createOrgDictItem(
//            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
//            @RequestParam(value = "orgDictSeq") int orgDictSeq,
//            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
//            @RequestParam(value = "orgCode") String orgCode,
//            @ApiParam(name = "code", value = "code", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "name", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "description", value = "description", defaultValue = "")
//            @RequestParam(value = "description") String description,
//            @ApiParam(name = "sort", value = "sort", defaultValue = "")
//            @RequestParam(value = "sort") String sort,
//            @ApiParam(name = "userId", value = "userId", defaultValue = "")
//            @RequestParam(value = "userId") String userId) {
//
//        Result result = getSuccessResult(false);
//        try {
//            boolean isExist = orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code);   //重复验证
//
//            if (isExist) {
//                result.setErrorMsg("该字典项已存在");
//                return result;
//            }
//            OrgDictItem orgDictItem = new OrgDictItem();
//            int nextSort;
//            if (StringUtil.isEmpty(sort)) {
//                nextSort = orgDictItemManager.getNextSort(orgDictSeq);
//            } else {
//                nextSort = Integer.parseInt(sort);
//            }
//            orgDictItem.setCode(code);
//            orgDictItem.setName(name);
//            orgDictItem.setSort(nextSort);
//            orgDictItem.setOrgDict(orgDictSeq);
//            orgDictItem.setCreateDate(new Date());
//            orgDictItem.setCreateUser(userId);
//            orgDictItem.setDescription(description);
//            orgDictItem.setOrganization(orgCode);
//            orgDictItemManager.createOrgDictItem(orgDictItem);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//
//        }
//        return result;
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.DELETE)
//    @ApiOperation(value = "删除字典项")
//    public boolean deleteOrgDictItem(
//            @ApiParam(name = "id", value = "编号", defaultValue = "")
//            @RequestParam(value = "id", required = false) long id) {
//
//        try {
//            orgDictItemManager.delete(id);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
//    @ApiOperation(value = "批量删除字典项")
//    public boolean deleteOrgDictItemList(
//            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
//            @RequestParam(value = "ids[]") Long[] ids) {
//
//        Result result = new Result();
//        if (ids == null || ids.length == 0) {
//            return true;
//        } else {
//            try {
//                orgDictItemManager.delete(ids, "id");
//                return true;
//            } catch (Exception e) {
//
//                return false;
//            }
//        }
//    }
//
//
//    @RequestMapping(value = "", method = RequestMethod.PUT)
//    @ApiOperation(value = "修改字典项")
//    public Result updateDictItem(
//            @ApiParam(name = "id", value = "id", defaultValue = "")
//            @RequestParam(value = "id") Long id,
//            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
//            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
//            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
//            @RequestParam(value = "orgCode") String orgCode,
//            @ApiParam(name = "code", value = "code", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "name", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "description", value = "description", defaultValue = "")
//            @RequestParam(value = "description") String description,
//            @ApiParam(name = "sort", value = "sort", defaultValue = "")
//            @RequestParam(value = "sort") String sort,
//            @ApiParam(name = "userId", value = "userId", defaultValue = "")
//            @RequestParam(value = "userId") String userId) {
//
//        Result result = getSuccessResult(false);
//        try {
//            OrgDictItem orgDictItem = orgDictItemManager.findOne(id);
//            if (orgDictItem == null) {
//                result.setErrorMsg("该字典项不存在");
//                return result;
//            } else {
//                //重复验证
//                boolean updateFlg = orgDictItem.getCode().equals(code) || !orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code);
//                if (updateFlg) {
//                    orgDictItem.setCode(code);
//                    orgDictItem.setName(name);
//                    orgDictItem.setDescription(description);
//                    orgDictItem.setUpdateDate(new Date());
//                    orgDictItem.setUpdateUser(userId);
//                    orgDictItem.setSort(Integer.parseInt(sort));
//                    orgDictItem.setOrganization(orgCode);
//                    orgDictItemManager.save(orgDictItem);
//                    result.setSuccessFlg(true);
//                }
//                result.setErrorMsg("该字典项已存在");
//            }
//        } catch (Exception e) {
//            result.setErrorMsg("修改失败");
//        }
//        return result;
//    }
//
//
//    @RequestMapping(value = "/page", method = RequestMethod.GET)
//    @ApiOperation(value = "分页查询")
//    public Object searchOrgDictItems(
//            @ApiParam(name = "parmJson", value = "分页查询条件模型", defaultValue = "")
//            @RequestParam(value = "parmJson", required = false) String parmJson) {
//        Result result = new Result();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
//            result = orgDictItemManager.pagesToResult(pageModel);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    @RequestMapping(value = "/combo", method = RequestMethod.GET)
//    @ApiOperation(value = "机构字典项下拉")
//    public Result getOrgDictEntry(
//            @ApiParam(name = "orgDictSeq", value = "字典seq", defaultValue = "")
//            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
//            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
//            @RequestParam(value = "orgCode") Long orgCode){
//        Result result = new Result();
//        try {
//            PageModel pageModel = new PageModel();
//            pageModel.addFieldCondition(new FieldCondition("orgDict", "=", orgDictSeq));
//            pageModel.addFieldCondition(new FieldCondition("organization", "=", orgCode));
//            List<OrgDictItem> orgDictItemList = orgDictItemManager.pages(pageModel);
//            List<String> orgDictItems = new ArrayList<>();
//            for (OrgDictItem orgDictItem : orgDictItemList) {
//                orgDictItems.add(String.valueOf(orgDictItem.getSequence()) + ',' + orgDictItem.getName());
//            }
//            result.setSuccessFlg(true);
//            result.setDetailModelList(orgDictItems);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
}
