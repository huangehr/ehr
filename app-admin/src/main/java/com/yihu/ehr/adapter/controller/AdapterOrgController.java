package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集标准
 * Created by zqb on 2015/11/19.
 */
@Controller
@RequestMapping("/adapterorg")
public class AdapterOrgController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initialOld")
    public String adapterOrgInitOld() {
        return "/adapter/adapterOrg";
    }

    @RequestMapping("initial")
    public String adapterOrgInit(Model model) {
        model.addAttribute("contentPage", "/adapter/adapterOrg/adapterOrg");
        return "pageView";
    }

    @RequestMapping("template/adapterOrgInfo")
    public Object adapterOrgInfoTemplate(Model model, String code, String type, String mode) {
        String url = "/adapterOrg/adapterOrg";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
//      params.put("mode",mode);
        params.put("type",type);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                //todo 后台转换成model后传前台
                //todo 新增时要初始type
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }
            model.addAttribute("info", resultStr);
            model.addAttribute("mode",mode);

            model.addAttribute("contentPage","/adapter/adapterOrg/adapterOrgDialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        AdapterOrgModel adapterOrgModel = new AdapterOrgModel();
//        //mode定义：new modify view三种模式，新增，修改，查看
//        if (mode.equals("view") || mode.equals("modify")) {
//            try {
//                XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
//                adapterOrgModel.setCode(StringUtil.latinString(adapterOrg.getCode()));
//                adapterOrgModel.setName(StringUtil.latinString(adapterOrg.getName()));
//                adapterOrgModel.setDescription(StringUtil.latinString(adapterOrg.getDescription()));
//                String parent = adapterOrg.getParent();
//                if (parent != null && !parent.equals("")) {
//                    adapterOrgModel.setParent(parent);
//                    adapterOrgModel.setParentValue(StringUtil.latinString(adapterOrgManager.getAdapterOrg(parent).getName()));
//                }
//                adapterOrgModel.setOrg(adapterOrg.getOrg().getOrgCode());
//                adapterOrgModel.setOrgValue(StringUtil.latinString(adapterOrg.getOrg().getFullName()));
//                adapterOrgModel.setArea((Address) adapterOrg.getArea());
//                adapterOrgModel.setType(adapterOrg.getType().getCode());
//                adapterOrgModel.setTypeValue(adapterOrg.getType().getValue());
//                if (adapterOrg.getArea() != null) {
//                    adapterOrgModel.setArea((Address) adapterOrg.getArea());
//                }
//            } catch (Exception ex) {
//
//            }
//        } else {
//            adapterOrgModel.setType(type);//初始类别
//        }
//        model.addAttribute("info", adapterOrgModel);
//        model.addAttribute("mode", mode);
//        model.addAttribute("contentPage", "/adapter/adapterOrg/adapterOrgDialog");
//        return "simpleView";
    }

    @RequestMapping("searchAdapterOrg")
    @ResponseBody
    //适配采集标准
    public Object searchAdapterOrg(String searchNm, int page, int rows, String type) {
        String url = "/adapterOrg/adapterOrgs";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",searchNm);
        params.put("name",searchNm);
        params.put("type",type);
        params.put("page",page);
        params.put("rows",rows);
        try{
            //todo 后台转换成result后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Map<String, Object> conditionMap = new HashMap<>();
//        conditionMap.put("key", searchNm);
//        conditionMap.put("page", page);
//        conditionMap.put("pageSize", rows);
//        //conditionMap.put("type", oryType);
//
//        List typeLs = new ArrayList<>();
//        if (StringUtil.isEmpty(type)) {
//            typeLs.add(conventionalDictEntry.getAdapterType("1"));
//            typeLs.add(conventionalDictEntry.getAdapterType("2"));
//            typeLs.add(conventionalDictEntry.getAdapterType("3"));
//        } else {
//            typeLs.add(conventionalDictEntry.getAdapterType(type));
//        }
//
//        conditionMap.put("typeLs", typeLs);
//
//        Result result = new Result();
//        try {
//            List<AdapterOrgModel> adapterOrgs = adapterOrgManager.searchAdapterOrgs(conditionMap);
//            Integer totalCount = adapterOrgManager.searchAdapterOrgInt(conditionMap);
//            result = getResult(adapterOrgs, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("getAdapterOrg")
    @ResponseBody
    //获取采集标准
    public Object getAdapterOrg(String code) {
        String url = "/adapterOrg/adapterOrg";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            AdapterOrgModel adapterOrgModel = mapper.readValue(resultStr, AdapterOrgModel.class);
//            Map<String, AdapterOrgModel> data = new HashMap<>();
//            data.put("adapterOrg", adapterOrgModel);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
//            AdapterOrgModel adapterOrgModel = adapterOrgManager.getAdapterOrg(adapterOrg);
//            Map<String, AdapterOrgModel> data = new HashMap<>();
//            data.put("adapterOrg", adapterOrgModel);
//            result.setObj(data);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("addAdapterOrg")
    @ResponseBody
    //新增采集标准
    public Object addAdapterOrg(String code,String name,String description,String parent,String org,String type,String area) {
//        String code = adapterOrgModel.getCode();
//        String name = adapterOrgModel.getName();
//        String description = adapterOrgModel.getDescription();
//        String parent = adapterOrgModel.getParent();
//        String org = adapterOrgModel.getOrg();
//        String type = adapterOrgModel.getType();
//        Address area = adapterOrgModel.getArea();
        String url="";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/adapterOrg/isAdapterOrgExist";//todo:网关没有该对应的接口
            params.put("code",code);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("该标准已存在！");
                return result;
            }

            //todo ：网关有orgName属性，没有area属性
            url="/adapterOrg/addAdapterOrg";
            params.put("name", name);
            params.put("description",description);
            params.put("parent",parent);
            params.put("orgCode",org);
            params.put("type",type);
            params.put("area",area);
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//创建第三方标准
//            ObjectMapper mapper = new ObjectMapper();
//            AdapterOrgModel adapterOrgModelNew = mapper.readValue(resultStr, AdapterOrgModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XAdapterOrg adapterOrg = new AdapterOrg();
//            String code = adapterOrgModel.getCode();
//            if (adapterOrgManager.getAdapterOrg(code) != null) {
//                result.setErrorMsg("该机构已存在采集标准");
//                result.setSuccessFlg(false);
//            } else {
//                adapterOrg.setCode(code);
//                adapterOrg.setName(adapterOrgModel.getName());
//                adapterOrg.setDescription(adapterOrgModel.getDescription());
//                adapterOrg.setParent(adapterOrgModel.getParent());
//                adapterOrg.setOrg(orgManager.getOrg(adapterOrgModel.getOrg()));
//                adapterOrg.setType(conventionalDictEntry.getAdapterType(adapterOrgModel.getType()));
//                adapterOrg.setArea(adapterOrgModel.getArea());
//                adapterOrgManager.addAdapterOrg(adapterOrg);
//                result.setSuccessFlg(true);
//            }
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("updateAdapterOrg")
    @ResponseBody
    //更新采集标准
    public Object updateAdapterOrg(String code, String name, String description) {
        String url="/adapterOrg/updateAdapterOrg";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",code);
        params.put("name",name);
        params.put("description",description);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            AdapterOrgModel adapterOrgModelNew = mapper.readValue(resultStr, AdapterOrgModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        try {
//            XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
//            adapterOrg.setName(name);
//            adapterOrg.setDescription(description);
//            adapterOrgManager.updateAdapterOrg(adapterOrg);
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("delAdapterOrg")
    @ResponseBody
    //删除采集标准
    public Object delAdapterOrg(String code) {
        String codeTemp[] = code.split(",");
        List<String> codes = Arrays.asList(codeTemp);

        String url = "/adapterOrg/adapterOrg";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("code",codes);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        String codeTemp[] = code.split(",");
//        List<String> codes = Arrays.asList(codeTemp);
//        int rtn = adapterOrgManager.deleteAdapterOrg(codes);
//        Result result = rtn > 0 ? getSuccessResult(true) : getSuccessResult(false);
//        return result.toJson();
    }

    @RequestMapping("getAdapterOrgList")
    @ResponseBody
    //获取初始标准列表
    public Object getAdapterOrgList(String type) {
        String url = "/adapterOrg/getAdapterOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> adapterOrgList = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        //根据类型获取所有采集标准
//        Result result = new Result();
//        List<String> orglist = new ArrayList<>();
//        try {
//            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type), orglist);
//            switch (type) {
//                case "1":
//                    //厂商，初始标准只能是厂商
//                    break;
//                case "2":
//                    //医院，初始标准没有限制
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1"), orglist));
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("3"), orglist));
//                    break;
//                case "3":
//                    //区域,初始标准只能选择厂商或区域
//                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1"), orglist));
//                    break;
//            }
//            List<String> adapterOrgs = new ArrayList<>();
//            if (!adapterOrgList.isEmpty()) {
//                for (XAdapterOrg adapterOrg : adapterOrgList) {
//                    adapterOrgs.add(adapterOrg.getCode() + ',' + adapterOrg.getName());
//                }
//            }
//            result.setObj(adapterOrgs);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    //todo：前端请求方法已被注释，目前没有用到该方法
    @RequestMapping("getOrgList")
    @ResponseBody
    //机构列表
    public Object getOrgList(String type) {
        String url = "/adapterOrg/getAdapterOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> orgList = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        String searchWay = "";
//        if (type.equals("1")) {
//            //厂商
//            searchWay = "ThirdPartyPlatform";
//        } else if (type.equals("2")) {
//            //医院
//            searchWay = "Hospital";
//        }
//        try {
//            List<XOrganization> organizations = orgManager.search(searchWay);
//            List<String> orgs = new ArrayList<>();
//            if (!organizations.isEmpty()) {
//                for (XOrganization organization : organizations) {
//                    orgs.add(organization.getOrgCode() + ',' + organization.getFullName());
//                }
//            }
//            result.setObj(orgs);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("searchOrgList")
    @ResponseBody
    //查询机构列表
    public Object searchOrgList(String type, String param, int page, int rows) {
        String url = "/adapterOrg/searchOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        params.put("orgCode",param);
        params.put("page",page);
        params.put("rows",rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        List typeLs = new ArrayList<>();
//        Map<String, Object> adapterOrgMap = new HashMap<>();
//        Map<String, Object> conditionMap = new HashMap<>();
//
//        param = param == null ? "" : param;
//        String searchWay = "";
//        if (type.equals("1")) {
//            //厂商
//            searchWay = "ThirdPartyPlatform";
//        } else if (type.equals("2")) {
//            //医院
//            searchWay = "Hospital";
//        }
//
//        typeLs.add(conventionalDictEntry.getAdapterType("1"));
//        typeLs.add(conventionalDictEntry.getAdapterType("2"));
//        typeLs.add(conventionalDictEntry.getAdapterType("3"));
//
//        adapterOrgMap.put("typeLs", typeLs);
//        adapterOrgMap.put("key", "");
//
//        conditionMap.put("orgCode", param);
//        conditionMap.put("fullName", param);
//        conditionMap.put("orgType", searchWay);
//        conditionMap.put("page", page);
//        conditionMap.put("pageSize", rows);
//
//        Result result = new Result();
//        try {
//            //排除已经存在的第三方标准的机构   adapterOrgs == 已经存在的第三方标准的机构列表
//            List<AdapterOrgModel> adapterOrgs = adapterOrgManager.searchAdapterOrgs(adapterOrgMap);
//            List<XOrganization> organizations = orgManager.search(conditionMap,adapterOrgs);
//            conditionMap.put("adapterOrgs", adapterOrgs);
//            int total = orgManager.searchInt(conditionMap);
//            result = getResult(organizations, total, page, rows);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    @RequestMapping("searchAdapterOrgList")
    @ResponseBody
    public Object searchAdapterOrgList(String type, String param, int page, int rows) {
        String url = "/adapterOrg/searchAdapterOrgList";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("type",type);
        params.put("code",param);
        params.put("name",param);
        params.put("page",page);
        params.put("rows",rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }
//        Result result = new Result();
//        Map<String, Object> conditionMap = new HashMap<>();
//        param = param == null ? "" : param;
//        conditionMap.put("key", param);
//        conditionMap.put("page", page);
//        conditionMap.put("pageSize", rows);
//        try {
//            List typeLs = new ArrayList<>();
//            conditionMap.put("typeLs", conventionalDictEntry.getAdapterType(type));
//            switch (type) {
//                case "1":
//                    //厂商，初始标准只能是厂商
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    break;
//                case "2":
//                    //医院，初始标准没有限制
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("2"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
//                    break;
//                case "3":
//                    //区域,初始标准只能选择厂商或区域
//                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
//                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
//                    break;
//            }
//            conditionMap.put("typeLs", typeLs);
//            List<XAdapterOrg> adapterOrgList = adapterOrgManager.searchAdapterOrg(conditionMap);
//            int total = adapterOrgManager.searchAdapterOrgInt(conditionMap);
//            result = getResult(adapterOrgList, total, page, rows);
//            result.setSuccessFlg(true);
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
//    }
}
