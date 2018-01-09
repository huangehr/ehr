package com.yihu.ehr.basic.org.controller;

import com.yihu.ehr.basic.org.model.OrgHealthCategory;
import com.yihu.ehr.basic.org.service.OrgHealthCategoryService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgHealthCategory;
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
 * 卫生机构类别 接口
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "卫生机构类别接口", tags = {"机构管理--卫生机构类别接口"})
public class OrgHealthCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgHealthCategoryService orgHealthCategoryService;

    @ApiOperation("根据ID获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MOrgHealthCategory mOrgHealthCategory = convertToModel(orgHealthCategoryService.getById(id), MOrgHealthCategory.class);
            envelop.setObj(mOrgHealthCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("获取所有的卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<OrgHealthCategory> list = orgHealthCategoryService.findAll();
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取所有的卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取所有的卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "卫生机构类别编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<OrgHealthCategory> resultList = new ArrayList<>();

            // 获取最顶层的卫生机构类别集合
            List<OrgHealthCategory> topNodeList = orgHealthCategoryService.getChildrenByPid(-1);
            if (topNodeList.size() == 0) {
                envelop.setDetailModelList(resultList);
                return envelop;
            }

            // 暂存最顶层卫生机构类别中，满足条件的集合
            List<OrgHealthCategory> topNodeListIn = new ArrayList<>();
            // 暂存最顶层卫生机构类别中，不满足条件的集合
            List<OrgHealthCategory> topNodeListOut = new ArrayList<>();

            if (StringUtils.isEmpty(codeName)) {
                resultList = orgHealthCategoryService.getTreeByParents(topNodeList);
                envelop.setDetailModelList(resultList);
                return envelop;
            }

            for (OrgHealthCategory reportCategory : topNodeList) {
                if (reportCategory.getCode().contains(codeName) || reportCategory.getName().contains(codeName)) {
                    topNodeListIn.add(reportCategory);
                    continue;
                }
                topNodeListOut.add(reportCategory);
            }
            if (topNodeListIn.size() != 0) {
                List<OrgHealthCategory> inList = orgHealthCategoryService.getTreeByParents(topNodeListIn);
                resultList.addAll(inList);
            }
            List<OrgHealthCategory> outList = orgHealthCategoryService.getTreeByParentsAndCodeName(topNodeListOut, codeName);
            resultList.addAll(outList);

            envelop.setDetailModelList(resultList);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "卫生机构类别JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            OrgHealthCategory newEntity = objectMapper.readValue(entityJson, OrgHealthCategory.class);
            newEntity.setTopPid(orgHealthCategoryService.getTopPidByPid(newEntity.getPid()));
            newEntity = orgHealthCategoryService.save(newEntity);

            MOrgHealthCategory mOrgHealthCategory = convertToModel(newEntity, MOrgHealthCategory.class);
            envelop.setObj(mOrgHealthCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "卫生机构类别JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            OrgHealthCategory updateEntity = objectMapper.readValue(entityJson, OrgHealthCategory.class);
            updateEntity = orgHealthCategoryService.save(updateEntity);

            MOrgHealthCategory mOrgHealthCategory = convertToModel(updateEntity, MOrgHealthCategory.class);
            envelop.setObj(mOrgHealthCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            orgHealthCategoryService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除卫生机构类别。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除卫生机构类别发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证卫生机构类别编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "卫生机构类别编码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = orgHealthCategoryService.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该卫生机构类别编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证卫生机构类别名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "卫生机构类别名称", required = true)
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = orgHealthCategoryService.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该卫生机构类别名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
