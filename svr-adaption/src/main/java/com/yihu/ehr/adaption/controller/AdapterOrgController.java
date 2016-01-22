package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.AdapterOrg;
import com.yihu.ha.adapter.model.AdapterOrgModel;
import com.yihu.ha.adapter.model.XAdapterOrg;
import com.yihu.ha.adapter.model.XAdapterOrgManager;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.dict.model.common.XConventionalDictEntry;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/** 采集标准
 * Created by zqb on 2015/11/19.
 */
@Controller
@RequestMapping("/adapterorg")
public class AdapterOrgController extends BaseController {
    @Resource(name = Services.AdapterOrgManager)
    private XAdapterOrgManager adapterOrgManager;
    @Resource(name = Services.OrgManager)
    private XOrgManager orgManager;
    @Resource(name = Services.ConventionalDictEntry)
    private XConventionalDictEntry conventionalDictEntry;

    @RequestMapping("initialOld")
    public String adapterOrgInitOld() {
        return "/adapter/adapterOrg";
    }

    @RequestMapping("initial")
    public String adapterOrgInit(Model model) {
        model.addAttribute("contentPage","/adapter/adapterOrg/adapterOrg");
        return "pageView";
    }

    @RequestMapping("template/adapterOrgInfo")
    public String adapterOrgInfoTemplate(Model model, String code, String type, String mode) {
        AdapterOrgModel adapterOrgModel = new AdapterOrgModel();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            try{
                XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
                adapterOrgModel = adapterOrgManager.getAdapterOrg(adapterOrg);
            }catch (Exception ex){

            }
        }else{
            adapterOrgModel.setType(type);//初始类别
        }
        model.addAttribute("info", adapterOrgModel);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/adapter/adapterOrg/adapterOrgDialog");
        return "generalView";
    }

    @RequestMapping("searchAdapterOrg")
    @ResponseBody
    //适配采集标准
    public String searchAdapterOrg(String searchNm, int page, int rows,String type){
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("key", searchNm);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        //conditionMap.put("type", oryType);

        List typeLs = new ArrayList<>();
        if (StringUtil.isEmpty(type)) {
            typeLs.add(conventionalDictEntry.getAdapterType("1"));
            typeLs.add(conventionalDictEntry.getAdapterType("2"));
            typeLs.add(conventionalDictEntry.getAdapterType("3"));
        } else {
            typeLs.add(conventionalDictEntry.getAdapterType(type));
        }

        conditionMap.put("typeLs", typeLs);

        Result result = new Result();
        try {
            List<AdapterOrgModel> adapterOrgs = adapterOrgManager.searchAdapterOrgs(conditionMap);
            Integer totalCount = adapterOrgManager.searchAdapterOrgInt(conditionMap);
            result = getResult(adapterOrgs,totalCount, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("getAdapterOrg")
    @ResponseBody
    //获取采集标准
    public String getAdapterOrg(String code){
        Result result = new Result();
        try{
            XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
            AdapterOrgModel adapterOrgModel = adapterOrgManager.getAdapterOrg(adapterOrg);
            Map<String, AdapterOrgModel> data = new HashMap<>();
            data.put("adapterOrg", adapterOrgModel);
            result.setObj(data);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("addAdapterOrg")
    @ResponseBody
    //新增采集标准
    public String addAdapterOrg(AdapterOrgModel adapterOrgModel){
        Result result = new Result();
        try {
            XAdapterOrg adapterOrg = new AdapterOrg();
            String code=adapterOrgModel.getCode();
            if (adapterOrgManager.getAdapterOrg(code)!=null){
                result.setErrorMsg("该机构已存在采集标准");
                result.setSuccessFlg(false);
            }else {
                adapterOrg.setCode(code);
                adapterOrg.setName(adapterOrgModel.getName());
                adapterOrg.setDescription(adapterOrgModel.getDescription());
                adapterOrg.setParent(adapterOrgModel.getParent());
                adapterOrg.setOrg(orgManager.getOrg(adapterOrgModel.getOrg()));
                adapterOrg.setType(conventionalDictEntry.getAdapterType(adapterOrgModel.getType()));
                adapterOrg.setArea(adapterOrgModel.getArea());
                adapterOrgManager.addAdapterOrg(adapterOrg);
                result.setSuccessFlg(true);
            }
            return result.toJson();
        }catch (Exception e){
            result.setSuccessFlg(false);
            return result.toJson();
        }
    }

    @RequestMapping("updateAdapterOrg")
    @ResponseBody
    //更新采集标准
    public String updateAdapterOrg(String code,String name,String description){
        try {
            XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
            adapterOrg.setName(name);
            adapterOrg.setDescription(description);
            adapterOrgManager.updateAdapterOrg(adapterOrg);
            Result result = getSuccessResult(true);
            return result.toJson();
        }catch (Exception e){
            Result result = getSuccessResult(false);
            return result.toJson();
        }
    }

    @RequestMapping("delAdapterOrg")
    @ResponseBody
    //删除采集标准
    public String delAdapterOrg(String code){
        String codeTemp[] = code.split(",");
        List<String> codes = Arrays.asList(codeTemp);
        int rtn = adapterOrgManager.deleteAdapterOrg(codes);
        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
        return result.toJson();
    }

    @RequestMapping("getAdapterOrgList")
    @ResponseBody
    //获取初始标准列表
    public String getAdapterOrgList(String type){
        //根据类型获取所有采集标准
        Result result = new Result();
        try{
            List<XAdapterOrg> adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type));
            switch (type) {
                case "1":
                    //厂商，初始标准只能是厂商
                    break;
                case "2":
                    //医院，初始标准没有限制
                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1")));
                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("3")));
                    break;
                case "3":
                    //区域,初始标准只能选择厂商或区域
                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1")));
                    break;
            }
            List<String> adapterOrgs = new ArrayList<>();
            if (!adapterOrgList.isEmpty()){
                for (XAdapterOrg adapterOrg : adapterOrgList) {
                    adapterOrgs.add(adapterOrg.getCode()+','+adapterOrg.getName());
                }
            }
            result.setObj(adapterOrgs);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("getOrgList")
    @ResponseBody
    //采集机构列表
    public String getOrgList(String type){
        Result result = new Result();
        String searchWay="";
        if (type.equals("1")){
            //厂商
            searchWay = "ThirdPartyPlatform";
        }else if (type.equals("2")){
            //医院
            searchWay="Hospital";
        }
        try{
            List<XOrganization> organizations = orgManager.search(searchWay);
            List<String> orgs = new ArrayList<>();
            if (!organizations.isEmpty()){
                for (XOrganization organization : organizations) {
                    orgs.add(organization.getOrgCode() + ',' + organization.getFullName());
                }
            }
            result.setObj(orgs);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("searchOrgList")
    @ResponseBody
    //查询机构列表
    public String searchOrgList(String type, String param, int page, int rows){
        param = param==null?"":param;
        String searchWay="";
        if (type.equals("1")){
            //厂商
            searchWay = "ThirdPartyPlatform";
        }else if (type.equals("2")){
            //医院
            searchWay="Hospital";
        }
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("orgCode", param);
        conditionMap.put("fullName", param);
        conditionMap.put("orgType", searchWay);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);

        Result result = new Result();
        try{
            List<XOrganization> organizations = orgManager.search(conditionMap);
            int total = orgManager.searchInt(conditionMap);
            result = getResult(organizations, total, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("searchAdapterOrgList")
    @ResponseBody
    public String searchAdapterOrgList(String type, String param, int page, int rows){
        Result result = new Result();
        Map<String, Object> conditionMap = new HashMap<>();
        param = param==null?"":param;
        conditionMap.put("key", param);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        try{
            List typeLs = new ArrayList<>();
            conditionMap.put("typeLs", conventionalDictEntry.getAdapterType(type));
            switch (type) {
                case "1":
                    //厂商，初始标准只能是厂商
                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
                    break;
                case "2":
                    //医院，初始标准没有限制
                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
                    typeLs.add(conventionalDictEntry.getAdapterType("2"));
                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
                    break;
                case "3":
                    //区域,初始标准只能选择厂商或区域
                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
                    break;
            }
            conditionMap.put("typeLs", typeLs);
            List<XAdapterOrg> adapterOrgList=adapterOrgManager.searchAdapterOrg(conditionMap);
            int total = adapterOrgManager.searchAdapterOrgInt(conditionMap);
            result = getResult(adapterOrgList, total, page, rows);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }
}
