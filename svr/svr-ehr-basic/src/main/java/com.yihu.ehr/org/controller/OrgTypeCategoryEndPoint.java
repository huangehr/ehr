package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgTypeCategory;
import com.yihu.ehr.org.model.OrgTypeCategory;
import com.yihu.ehr.org.service.OrgTypeCategoryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构类型管理 接口
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "机构类型管理接口", tags = {"机构管理--机构类型管理接口"})
public class OrgTypeCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgTypeCategoryService orgTypeCategoryService;

    @ApiOperation("根据ID获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MOrgTypeCategory mOrgTypeCategory = convertToModel(orgTypeCategoryService.getById(id), MOrgTypeCategory.class);
            envelop.setObj(mOrgTypeCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("获取所有的机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<OrgTypeCategory> list = orgTypeCategoryService.findAll();
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取所有的机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取所有的机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "机构类型编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<OrgTypeCategory> resultList = new ArrayList<>();

            // 获取最顶层的机构类型集合
            List<OrgTypeCategory> topNodeList = orgTypeCategoryService.getChildrenByPid(-1);
            if (topNodeList.size() == 0) {
                envelop.setDetailModelList(resultList);
                return envelop;
            }

            // 暂存最顶层机构类型中，满足条件的集合
            List<OrgTypeCategory> topNodeListIn = new ArrayList<>();
            // 暂存最顶层机构类型中，不满足条件的集合
            List<OrgTypeCategory> topNodeListOut = new ArrayList<>();

            if (StringUtils.isEmpty(codeName)) {
                resultList = orgTypeCategoryService.getTreeByParents(topNodeList);
                envelop.setDetailModelList(resultList);
                return envelop;
            }

            for (OrgTypeCategory reportCategory : topNodeList) {
                if (reportCategory.getCode().contains(codeName) || reportCategory.getName().contains(codeName)) {
                    topNodeListIn.add(reportCategory);
                    continue;
                }
                topNodeListOut.add(reportCategory);
            }
            if (topNodeListIn.size() != 0) {
                List<OrgTypeCategory> inList = orgTypeCategoryService.getTreeByParents(topNodeListIn);
                resultList.addAll(inList);
            }
            List<OrgTypeCategory> outList = orgTypeCategoryService.getTreeByParentsAndCodeName(topNodeListOut, codeName);
            resultList.addAll(outList);

            envelop.setDetailModelList(resultList);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "机构类型JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            OrgTypeCategory newEntity = objectMapper.readValue(entityJson, OrgTypeCategory.class);
            newEntity.setTopPid(orgTypeCategoryService.getTopPidByPid(newEntity.getPid()));
            newEntity = orgTypeCategoryService.save(newEntity);

            MOrgTypeCategory mOrgTypeCategory = convertToModel(newEntity, MOrgTypeCategory.class);
            envelop.setObj(mOrgTypeCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "机构类型JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            OrgTypeCategory updateEntity = objectMapper.readValue(entityJson, OrgTypeCategory.class);
            updateEntity = orgTypeCategoryService.save(updateEntity);

            MOrgTypeCategory mOrgTypeCategory = convertToModel(updateEntity, MOrgTypeCategory.class);
            envelop.setObj(mOrgTypeCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            orgTypeCategoryService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除机构类型。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除机构类型发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证机构分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "消息队列编码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = orgTypeCategoryService.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该机构分类编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证机构分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "消息订阅者ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "消息队列名称", required = true)
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = orgTypeCategoryService.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该机构分类名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
