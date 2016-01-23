package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.Controllers;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.constrant.SessionAttributeKeys;
import com.yihu.ha.user.model.XUser;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/orgdataset")
@Controller(Controllers.OrgDataSetController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class OrgDataSetController extends BaseController {

    @Resource(name = Services.OrgDataSetManager)
    private XOrgDataSetManager orgDataSetManager;

    @Resource(name = Services.OrgMetaDataManager)
    private XOrgMetaDataManager orgMetaDataManager;

    @RequestMapping("/initialOld")
    public String orgDataSetInitOld(HttpServletRequest request,String adapterOrg){
        request.setAttribute("adapterOrg",encodeStr(adapterOrg));
        return "/adapter/orgCollection";
    }

    @RequestMapping("/initial")
    public String orgDataSetInit(Model model, String adapterOrg){
        model.addAttribute("adapterOrg",adapterOrg);
        model.addAttribute("contentPage","/adapter/orgCollection/grid");
        return "pageView";
    }

    @RequestMapping("template/orgDataInfo")
    public String orgDataInfoTemplate(Model model, String id, String mode) {
        OrgDataSetModel orgDataSetModel = new OrgDataSetModel();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            try{
                OrgDataSet orgDataSet = (OrgDataSet) orgDataSetManager.getOrgDataSet(Long.parseLong(id));
                orgDataSetModel.setId(String.valueOf(orgDataSet.getId()));
                orgDataSetModel.setCode(orgDataSet.getCode());
                orgDataSetModel.setName(orgDataSet.getName());
                orgDataSetModel.setDescription(orgDataSet.getDescription());
                model.addAttribute("rs","success");
            }catch (Exception es){
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("sort","");
        model.addAttribute("info", orgDataSetModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
        return "generalView";
    }

    @RequestMapping("template/orgMetaDataInfo")
    public String orgMetaDataInfoTemplate(Model model, String id, String mode) {
        OrgMetaDataModel orgMetaDataModel = new OrgMetaDataModel();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            try{
                OrgMetaData orgMetaData = (OrgMetaData) orgMetaDataManager.getOrgMetaData(Long.parseLong(id));
                orgMetaDataModel.setId(String.valueOf(orgMetaData.getId()));
                orgMetaDataModel.setCode(orgMetaData.getCode());
                orgMetaDataModel.setName(orgMetaData.getName());
                orgMetaDataModel.setDescription(orgMetaData.getDescription());
                model.addAttribute("rs","success");
            }catch (Exception ex){
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("sort","");
        model.addAttribute("info", orgMetaDataModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
        return "generalView";
    }

    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping("getOrgDataSet")
    @ResponseBody
    public String getOrgDataSet(String id) {
        Result result = new Result();
        try{
            OrgDataSet orgDataSet = (OrgDataSet) orgDataSetManager.getOrgDataSet(Long.parseLong(id));
            OrgDataSetModel model = new OrgDataSetModel();
            model.setCode(orgDataSet.getCode());
            model.setName(orgDataSet.getName());
            model.setDescription(orgDataSet.getDescription());
            result.setObj(model);
            result.setSuccessFlg(true);
        }catch (Exception es){
            result.setSuccessFlg(false);
        }

        return result.toJson();
    }

    /**
     * 创建机构数据集
     *
     * @param code
     * @param name
     * @param description
     * @param orgCode
     * @param user
     * @return
     */
    @RequestMapping(value = "createOrgDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgDataSet(String code,
                                   String name,
                                   String description,
                                   String orgCode,
                                   @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {

        Result result = new Result();

        try {
            boolean isExist = orgDataSetManager.isExistOrgDataSet(orgCode, code, name);   //重复校验
            if (isExist) {
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据集已存在！");
                return result.toJson();
            }
            OrgDataSet orgDataSet = new OrgDataSet();
            orgDataSet.setCode(code);
            orgDataSet.setName(name);
            orgDataSet.setDescription(description);
            orgDataSet.setOrganization(orgCode);
            orgDataSet.setCreateDate(new Date());
            orgDataSet.setCreateUser(user);

            if (orgDataSetManager.createOrgDataSet(orgDataSet) == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("创建数据集失败！");
                return result.toJson();
            }

            OrgDataSetModel model = new OrgDataSetModel();
            model.setCode(orgDataSet.getCode());
            model.setName(orgDataSet.getName());
            model.setDescription(orgDataSet.getDescription());
            result.setSuccessFlg(true);
            result.setObj(model);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 删除机构数据集
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDataSet")
    @ResponseBody
    public String deleteOrgDataSet(long id) {

        Result result = new Result();
        XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);

        if (orgDataSet == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("该数据集不存在！");
            return result.toJson();
        }
        try {
            orgDataSetManager.deleteOrgDataSet(id);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("删除数据集失败！");
            return result.toJson();
        }
    }

    /**
     * 修改机构数据集
     *
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value = "updateOrgDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateOrgDataSet(String orgCode,
                                   long id,
                                   String code,
                                   String name,
                                   String description,
                                   @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {

        Result result = new Result();
        try{
            XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);
            if(orgDataSet == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据集不存在！");
            }else {
                //重复校验
                boolean updateFlg = orgDataSet.getCode().equals(code) || !orgDataSetManager.isExistOrgDataSet(orgCode, code, name);
                if (updateFlg) {
                    orgDataSet.setCode(code);
                    orgDataSet.setName(name);
                    orgDataSet.setDescription(description);
                    orgDataSet.setUpdateDate(new Date());
                    orgDataSet.setUpdateUser(user);
                    orgDataSetManager.updateOrgDataSet(orgDataSet);
                    result.setSuccessFlg(true);
                } else {
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该数据集已存在！");
                }
            }
            return  result.toJson();
        }catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("修改数据集失败！");
            return result.toJson();
        }
    }


    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDataSets")
    @ResponseBody
    public String searchOrgDataSets(String orgCode, String codename, int page, int rows) {
        Result result = new Result();
        try {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgCode", orgCode);
            conditionMap.put("code", codename);
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);
            List<OrgDataSetModel> orgDataSetModel = orgDataSetManager.searchOrgDataSets(conditionMap);
            Integer totalCount = orgDataSetManager.searchTotalCount(conditionMap);
            result = getResult(orgDataSetModel, totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


    //---------------------------以上是机构数据集部分，以下是机构数据元详情部分---------------------------


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping("getOrgMetaData")
    @ResponseBody
    public String getOrgMetaData(String id) {
        Result result = new Result();
        try{
            OrgMetaData orgMetaData = (OrgMetaData) orgMetaDataManager.getOrgMetaData(Long.parseLong(id));
            OrgMetaDataModel model = new OrgMetaDataModel();
            model.setCode(orgMetaData.getCode());
            model.setName(orgMetaData.getName());
            model.setDescription(orgMetaData.getDescription());
            result.setObj(model);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }

        return result.toJson();
    }


    /**
     * 创建机构数据元
     *
     * @param orgDataSetSeq
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value = "createOrgMetaData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgMetaData(Integer orgDataSetSeq,
                                    String orgCode,
                                    String code,
                                    String name,
                                    String description,
                                    @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {

        Result result = new Result();
        try {
            boolean isExist = orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq,orgCode, code, name);   //重复校验

            if (isExist) {
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据元已存在！");
                return result.toJson();
            }
            OrgMetaData orgMetaData = new OrgMetaData();
            orgMetaData.setCode(code);
            orgMetaData.setName(name);
            orgMetaData.setOrgDataSet(orgDataSetSeq);
            orgMetaData.setCreateDate(new Date());
            orgMetaData.setCreateUser(user);
            orgMetaData.setOrganization(orgCode);
            orgMetaData.setDescription(description);
            if (orgMetaDataManager.createOrgMetaData(orgMetaData) == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("创建数据元失败！");
                return result.toJson();
            }
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    /**
     * 删除机构数据元
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgMetaData")
    @ResponseBody
    public String deleteOrgMetaData(long id) {

        Result result = new Result();
        try {
            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);

            if (orgMetaData == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据元不存在！");
                return result.toJson();
            }
            orgMetaDataManager.deleteOrgMetaData(id);
            result.setSuccessFlg(true);
            return result.toJson();

        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("删除数据元失败！");
            return result.toJson();
        }
    }

    /**
     * 批量删除机构数据元
     *
     * @param ids
     * @return
     */
    @RequestMapping("deleteOrgMetaDataList")
    @ResponseBody
    public String deleteOrgMetaDataList(@RequestParam("ids[]") Long[] ids) {
        Result result = new Result();

        if (StringUtil.isEmpty(ids)) {
            result.setSuccessFlg(false);
            return result.toJson();
        } else {
            try {
                orgMetaDataManager.deleteOrgMetaDataList(ids);
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg("删除数据元失败！");
                return result.toJson();
            }
            result.setSuccessFlg(true);

            return result.toJson();
        }
    }

    /**
     * 修改机构数据元
     *
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value = "updateOrgMetaData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateOrgMetaData(Integer orgDataSetSeq,
                                    String orgCode,
                                    long id,
                                    String code,
                                    String name,
                                    String description,
                                    @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {


        Result result = new Result();
        try{
            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
            if(orgMetaData == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据元不存在！");
            }else {
                //重复校验
                boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code, name);
                if (updateFlg) {
                    orgMetaData.setCode(code);
                    orgMetaData.setName(name);
                    orgMetaData.setDescription(description);
                    orgMetaData.setUpdateDate(new Date());
                    orgMetaData.setUpdateUser(user);
                    orgMetaData.setOrganization(orgCode);
                    orgMetaDataManager.updateOrgMetaData(orgMetaData);
                    result.setSuccessFlg(true);
                } else {
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该数据元已存在！");
                }
            }
            return  result.toJson();
        }catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("修改数据元失败！");
            return result.toJson();
        }
    }

    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgMetaDatas")
    @ResponseBody
    public String searchOrgMetaDatas(String orgCode,Integer orgDataSetSeq, String codename, int page, int rows) {
        Result result =new Result();
        try {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgCode", orgCode);
            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
            conditionMap.put("code", codename);
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);
            List<OrgMetaDataModel> orgMetaDatas = orgMetaDataManager.searchOrgMetaDatas(conditionMap);
            Integer totalCount = orgMetaDataManager.searchTotalCount(conditionMap);

            result = getResult(orgMetaDatas, totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception es){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


}
