package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.Controllers;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.constrant.SessionAttributeKeys;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.user.model.XUser;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/orgdict")
@Controller(Controllers.OrgDictController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class OrgDictController extends BaseController {

    @Resource(name = Services.OrgDictManager)
    private XOrgDictManager orgDictManager;

    @Resource(name = Services.OrgDictItemManager)
    private XOrgDictItemManager orgDictItemManager;

    @Resource(name = Services.OrgManager)
    private XOrgManager orgManager;

    @RequestMapping("initial")
    public String orgDictInit() {
        return "/adapter/orgdict/index";
    }

    @RequestMapping("template/orgDictInfo")
    public String orgDictInfoTemplate(Model model, String id, String mode) {
        OrgDictModel orgDictModel = new OrgDictModel();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            try {
                OrgDict orgDict  = (OrgDict) orgDictManager.getOrgDict(Long.parseLong(id));
                orgDictModel.setId(String.valueOf(orgDict.getId()));
                orgDictModel.setCode(orgDict.getCode());
                orgDictModel.setName(orgDict.getName());
                orgDictModel.setDescription(orgDict.getDescription());
                model.addAttribute("rs","success");
            }catch (Exception ex){
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("sort","");
        model.addAttribute("info", orgDictModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
        return "generalView";
    }

    @RequestMapping("template/orgDictItemsInfo")
    public String orgDictItemsInfoTemplate(Model model, String id, String mode) {
        OrgDictItemModel orgDictItemModel = new OrgDictItemModel();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            try {
                OrgDictItem orgDictItem  = (OrgDictItem) orgDictItemManager.getOrgDictItem(Long.parseLong(id));
                orgDictItemModel.setId(String.valueOf(orgDictItem.getId()));
                orgDictItemModel.setCode(orgDictItem.getCode());
                orgDictItemModel.setName(orgDictItem.getName());
                orgDictItemModel.setDescription(orgDictItem.getDescription());
                model.addAttribute("sort",Integer.toString(orgDictItem.getSort()));
                model.addAttribute("rs","success");
            }catch (Exception ex){
                model.addAttribute("rs", "error");
            }
        }
        model.addAttribute("info", orgDictItemModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
        return "generalView";
    }

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping("getOrgDict")
    @ResponseBody
    public String getOrgDict(String id) {
        Result result = new Result();
        try {
            OrgDict orgDict  = (OrgDict) orgDictManager.getOrgDict(Long.parseLong(id));
            OrgDictModel model = new OrgDictModel();
            model.setCode(orgDict.getCode());
            model.setName(orgDict.getName());
            model.setDescription(orgDict.getDescription());
            result.setObj(model);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }

        return result.toJson();
    }

    /**
     * 创建机构字典
     * @param code
     * @param name
     * @param description
     * @param orgCode
     * @param user
     * @return
     */
    @RequestMapping(value = "createOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgDict(String orgCode,String code,String name,String description,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user){

        Result result = new Result();
        try {
            boolean isExist = orgDictManager.isExistOrgDict(orgCode,code,name);   //重复校验

            if(isExist){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典已存在！");
                return  result.toJson();
            }
            OrgDict orgDict = new OrgDict();
            orgDict.setCode(code);
            orgDict.setName(name);
            orgDict.setDescription(description);
            orgDict.setOrganization(orgCode);
            orgDict.setCreateDate(new Date());
            orgDict.setCreateUser(user);
            if(orgDictManager.createOrgDict(orgDict)==null){

                result.setSuccessFlg(false);
                result.setErrorMsg("创建字典失败！");
                return  result.toJson();
            }
            OrgDictModel model = new OrgDictModel();
            model.setCode(orgDict.getCode());
            model.setName(orgDict.getName());
            model.setDescription(orgDict.getDescription());
            result.setSuccessFlg(true);
            result.setObj(model);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return  result.toJson();
    }

    /**
     * 删除机构字典
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDict")
    @ResponseBody
    public String deleteOrgDict(long id) {

        Result result = new Result();
        try {
            XOrgDict orgDict = orgDictManager.getOrgDict(id);
            if(orgDict == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典不存在！");
                return  result.toJson();
            }
            orgDictManager.deleteOrgDict(id);

            result.setSuccessFlg(true);
            return  result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("删除字典失败！");
            return  result.toJson();
        }
    }

    /**
     * 修改机构字典
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="updateOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateOrgDict(String orgCode,long id,String code,String name,String description,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user) {

        Result result = new Result();
        try{
            XOrgDict orgDict = orgDictManager.getOrgDict(id);
            if(orgDict == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典不存在！");
            }else {
                //重复校验
                boolean updateFlg = orgDict.getCode().equals(code) || !orgDictManager.isExistOrgDict(orgCode, code, name);
                if (updateFlg) {
                    orgDict.setCode(code);
                    orgDict.setName(name);
                    orgDict.setDescription(description);
                    orgDict.setUpdateDate(new Date());
                    orgDict.setUpdateUser(user);
                    orgDictManager.updateOrgDict(orgDict);
                    result.setSuccessFlg(true);
                } else {
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该字典已存在！");
                }
            }
            return  result.toJson();
        }catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("修改字典失败！");
            return result.toJson();
        }
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDicts")
    @ResponseBody
    public String searchOrgDicts(String orgCode,String codename,int page, int rows) {
        Result result=new Result();
        try {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgCode", orgCode);
            conditionMap.put("code", codename);
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);
            List<OrgDictModel> detailModelList = orgDictManager.searchOrgDicts(conditionMap);
            Integer totalCount = orgDictManager.searchTotalCount(conditionMap);
            result = getResult(detailModelList, totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    //---------------------------以上是机构字典部分，以下是机构字典详情部分---------------------------

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping("getOrgDictItem")
    @ResponseBody
    public String getOrgDictItem(String id) {
        Result result = new Result();
        try {
            OrgDictItem orgDictItem  = (OrgDictItem) orgDictItemManager.getOrgDictItem(Long.parseLong(id));
            OrgDictItemModel model = new OrgDictItemModel();
            model.setCode(orgDictItem.getCode());
            model.setName(orgDictItem.getName());
            model.setSort(Integer.toString(orgDictItem.getSort()));
            model.setDescription(orgDictItem.getDescription());
            result.setObj(model);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


    /**
     * 创建机构字典数据
     * @param orgDictSeq
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="createOrgDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgDictItem(Integer orgDictSeq,String orgCode,String code,String name,String description,String sort,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user){

        Result result = new Result();
        try {
            boolean isExist = orgDictItemManager.isExistOrgDictItem(orgDictSeq,orgCode,code, name);   //重复校验

            if(isExist){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典项已存在！");
                return  result.toJson();
            }
            OrgDictItem orgDictItem = new OrgDictItem();
            int nextSort;
            if(StringUtil.isEmpty(sort)){
                nextSort = orgDictItemManager.getNextSort(orgDictSeq);
            }else{
                nextSort = Integer.parseInt(sort);
            }
            orgDictItem.setCode(code);
            orgDictItem.setName(name);
            orgDictItem.setSort(nextSort);
            orgDictItem.setOrgDict(orgDictSeq);
            orgDictItem.setCreateDate(new Date());
            orgDictItem.setCreateUser(user);
            orgDictItem.setDescription(description);
            orgDictItem.setOrganization(orgCode);
            if(orgDictItemManager.createOrgDictItem(orgDictItem)==null){
                result.setSuccessFlg(false);
                result.setErrorMsg("创建字典项失败！");
                return  result.toJson();
            }
            OrgDictItemModel model = new OrgDictItemModel();
            model.setCode(orgDictItem.getCode());
            model.setName(orgDictItem.getName());
            model.setDescription(orgDictItem.getDescription());
            result.setSuccessFlg(true);
            result.setObj(model);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }

        return  result.toJson();
    }

    /**
     * 删除机构字典数据
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDictItem")
    @ResponseBody
    public String deleteOrgDictItem(long id) {

        Result result = new Result();
        try {
            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
            if(orgDictItem == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典项不存在！");
                return  result.toJson();
            }
            orgDictItemManager.deleteOrgDictItem(id);
            result.setSuccessFlg(true);
            return  result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("删除字典项失败！");
            return  result.toJson();
        }
    }

    /**
     * 批量删除机构字典数据
     * @param ids
     * @return
     */
    @RequestMapping("deleteOrgDictItemList")
    @ResponseBody
    public String deleteOrgDictItemList(@RequestParam("ids[]") Long[] ids) {
        Result result = new Result();

        if(StringUtil.isEmpty(ids)){
            result.setSuccessFlg(false);
            return result.toJson();
        }else {
            try {
                orgDictItemManager.deleteOrgDictItemList(ids);
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg("删除字典项失败！");
                return result.toJson();
            }
            result.setSuccessFlg(true);

            return result.toJson();
        }
    }

    /**
     * 修改机构字典数据
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="updateDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateDictItem(Long id,Integer orgDictSeq,String orgCode,String code,String name,String description,String sort,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user) {

        Result result = new Result();
        try{
            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
            if(orgDictItem == null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典项不存在！");
            }else {
                //重复校验
                boolean updateFlg = orgDictItem.getCode().equals(code) || !orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code, name);
                if (updateFlg) {
                    orgDictItem.setCode(code);
                    orgDictItem.setName(name);
                    orgDictItem.setDescription(description);
                    orgDictItem.setUpdateDate(new Date());
                    orgDictItem.setUpdateUser(user);
                    orgDictItem.setSort(Integer.parseInt(sort));
                    orgDictItem.setOrganization(orgCode);
                    orgDictItemManager.updateOrgDictItem(orgDictItem);
                    result.setSuccessFlg(true);
                } else {
                    result.setSuccessFlg(false);
                    result.setErrorMsg("该字典项已存在！");
                }
            }
            return  result.toJson();
        }catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("修改字典项失败！");
            return result.toJson();
        }
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDictItems")
    @ResponseBody
    public String searchOrgDictItems(Integer orgDictSeq,String orgCode,String codename,int page, int rows) {
        Result result=new Result();
        try {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("orgDictSeq", orgDictSeq);
            conditionMap.put("orgCode", orgCode);
            conditionMap.put("code", codename);
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);
            List<OrgDictItemModel> detailModelList = orgDictItemManager.searchOrgDictItems(conditionMap);
            Integer totalCount = orgDictItemManager.searchTotalCount(conditionMap);
            result = getResult(detailModelList, totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping(value = "getOrganizationList",produces = "text/html;charset=UTF-8")
    @ResponseBody
    //获取机构列表
    public String getOrganizationList(){
        Result result = new Result();
        try {
            List<XOrganization> organizations = orgDictItemManager.getOrganizationList();
            List<String> orgCodeName =new ArrayList<>();
            for (XOrganization organization : organizations) {
                orgCodeName.add(organization.getOrgCode()+','+organization.getFullName());
            }
            result.setObj(orgCodeName);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


}
