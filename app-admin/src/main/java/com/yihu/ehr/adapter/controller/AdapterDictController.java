package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/11/13.
 */
@RequestMapping("/adapterDict")
@Controller
public class AdapterDictController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("template/adapterMetaDataInfo")
    public Object adapterMetaDataInfoTemplate(Model model, Long id, String mode) {
        String url = "/adapterDict/adapterDict";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                //todo 后台转换成model后传前台
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }
            model.addAttribute("info", resultStr);
            model.addAttribute("mode",mode);

            model.addAttribute("contentPage","/adapter/adapterDataSet/dictDialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

//        XAdapterDict adapterDict = new AdapterDict();
//        if(mode.equals("view") || mode.equals("modify")){
//            try {
//                adapterDict = adapterDictManager.getAdapterDict(id);
//                model.addAttribute("rs", "success");
//            } catch (Exception e) {
//                model.addAttribute("rs", "error");
//            }
//        }
//        model.addAttribute("info", adapterDict);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/adapter/adapterDataSet/dictDialog");
//        return "simpleView";
    }

    /**
     * 根据方案ID及查询条件查询字典适配关系
     * @param adapterPlanId
     * @param strKey
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterDict")
    @ResponseBody
    public Object searchAdapterDict(Long adapterPlanId, String strKey,int page, int rows){
        String url = "/adapterDict/adapterDicts";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("adapterPlanId", adapterPlanId);
        params.put("code", strKey);
        params.put("name", strKey);
        params.put("page", page);
        params.put("rows", rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//
//        try {
//            List<AdapterDictModel> dict = adapterDictManager.searchAdapterDict(adapterPlanId, strKey, page, rows);
//            int totalCount = adapterDictManager.searchDictInt(adapterPlanId, strKey);
//            result.setSuccessFlg(true);
//            result = getResult(dict,totalCount,page,rows);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
    }

    /**
     * 根据dictId搜索字典项适配关系
     * @param adapterPlanId
     * @param dictId
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchAdapterDictEntry")
    @ResponseBody
    public Object searchAdapterDictEntry(Long adapterPlanId, Long dictId,String strKey,int page, int rows){
        String url = "/adapterDict/adapterDictEntrys";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("adapterPlanId", adapterPlanId);
        params.put("dictId", dictId);
        params.put("code", strKey);
        params.put("name", strKey);
        params.put("page", page);
        params.put("rows", rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//
//        List<AdapterDictModel> adapterDictModels;
//        int totalCount;
//        try {
//            adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, strKey, page, rows);
//            totalCount = adapterDictManager.searchDictEntryInt(adapterPlanId, dictId, strKey);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
//        result.setSuccessFlg(true);
//        result = getResult(adapterDictModels,totalCount,page,rows);
//        return result.toJson();
    }

    /**
     * 根据字典ID获取字典项适配关系明细
     * @param id
     * @return
     */
    //    todo:前端没有找到该路径的请求
    @RequestMapping("/getAdapterDictEntry")
    @ResponseBody
    public Object getAdapterDictEntry(Long id){
        String url = "/adapterDict/adapterDictEntry";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            XAdapterDict adapterDict = mapper.readValue(resultStr, XAdapterDict.class);
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
//            XAdapterDict adapterDict = adapterDictManager.getAdapterDict(id);
//
//            result.setObj(adapterDict);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }

    }

//    private XAdapterDict getAdapterDict(Long id) {
//        return id==null? new AdapterDict(): adapterDictManager.getAdapterDict(id);
//    }

    /**
     * 修改字典项映射关系
     * @param
     * @return
     */
    @RequestMapping("/updateAdapterDictEntry")
    @ResponseBody
    public Object updateAdapterDictEntry(String adapterDictModel){
        String url = "";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("adapterDictModel",adapterDictModel);
        try {
            //更新或新增 todo:可放内部去做判断
//            if (adapterDictModel.getId()==null) {
//                url = "/adapterDict/addAdapterDictEntry";
//            }else {
//                url = "/adapterDict/updateAdapterDictEntry";
//            }
            url="/adapterDict/adapterDictEntry";
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            AdapterDictModel adapterDictModelNew = mapper.readValue(resultStr, AdapterDictModel.class);
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
//            XAdapterDict adapterDict = getAdapterDict(adapterDictModel.getId());
//            adapterDict.setAdapterPlanId(adapterDictModel.getAdapterPlanId());
//            adapterDict.setDictEntryId(adapterDictModel.getDictEntryId());
//            adapterDict.setDictId(adapterDictModel.getDictId());
//            adapterDict.setOrgDictSeq(adapterDictModel.getOrgDictSeq());
//            adapterDict.setOrgDictItemSeq(adapterDictModel.getOrgDictEntrySeq());
//            adapterDict.setDescription(adapterDictModel.getDescription());
//            if (adapterDictModel.getId()==null){
//                adapterDictManager.addAdapterDict(adapterDict);
//            }else {
//                adapterDictManager.updateAdapterDict(adapterDict);
//            }
//            result.setSuccessFlg(true);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    /**
     * 删除字典项映射的方法
     * @param id
     * @return
     */
    @RequestMapping("/delDictEntry")
    @ResponseBody
    public Object delDictEntry(@RequestParam("id") Long[] id){
        String url = "/adapterDict/adapterDictEntry";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("ids",id);
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
//        Result result = new Result();
//        if (id == null || id.length == 0) {
//            result.setErrorMsg("没有要删除信息！");
//            result.setSuccessFlg(false);
//        }else{
//            if (adapterDictManager.deleteAdapterDictRemain(id)<1){
//                result.setErrorMsg("至少保留一条！");//适配的字典由数据元关联得来，字典不能全部删除
//                result.setSuccessFlg(false);
//            }else{
//                int rtn = adapterDictManager.deleteAdapterDict(id);
//                result = rtn>0?getSuccessResult(true):getSuccessResult(false);
//            }
//        }
//
//        return result.toJson();
    }

    /**
     * 标准字典项的下拉
     * @return
     */
    @RequestMapping("/getStdDictEntry")
    @ResponseBody
    public Object getStdDictEntry(Long adapterPlanId,Long dictId,String mode){
        String url = "/adapterDict/getStdDictEntry";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("adapterPlanId",adapterPlanId);
        params.put("dictId",dictId);
        params.put("mode",mode);
        try {
            //todo 失败，返回的错误信息怎么体现？
            //todo 新增时要过滤掉已经存在的标准
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> stdDict = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setSuccessFlg(true);
            result.setObj(resultStr);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            XCDAVersion version = orgAdapterPlan.getVersion();
//            List<XDictEntry> dictEntryList = Arrays.asList(dictManager.getDictEntries(dictManager.getDict(dictId, version)));
//            List<String> dictEntries = new ArrayList<>();
//            if (!dictEntryList.isEmpty()){
//                if("modify".equals(mode) || "view".equals(mode)){
//                    for (XDictEntry dictEntry : dictEntryList) {
//                        dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
//                    }
//                }
//                else{
//                    List<AdapterDictModel> adapterDictModels = adapterDictManager.searchAdapterDictEntry(adapterPlanId, dictId, "", 0, 0);
//                    boolean exist = false;
//                    for (XDictEntry dictEntry : dictEntryList) {
//                        exist = false;
//                        for(AdapterDictModel model : adapterDictModels){
//                            if(dictEntry.getId()==model.getDictEntryId()){
//                                exist = true;
//                                break;
//                            }
//                        }
//                        if(!exist)
//                            dictEntries.add(String.valueOf(dictEntry.getId())+','+dictEntry.getValue());
//                    }
//                }
//
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(dictEntries);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    /**
     * 机构字典下拉
     * @return
     */
    @RequestMapping("/getOrgDict")
    @ResponseBody
    public Object getOrgDict(Long adapterPlanId){
        String url = "/adapterDict/getOrgDict";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("adapterPlanId",adapterPlanId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> orgDict = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setSuccessFlg(true);
            result.setObj(resultStr);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgDict>  orgDictList = orgDictManager.searchOrgDict(conditionMap);
//            List<String> orgDicts = new ArrayList<>();
//            if (!orgDictList.isEmpty()){
//                for (XOrgDict orgDict : orgDictList) {
//                    orgDicts.add(String.valueOf(orgDict.getSequence())+','+orgDict.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgDicts);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    /**
     * 机构字典项下拉
     * @param orgDictSeq
     * @return
     */
    @RequestMapping("/getOrgDictEntry")
    @ResponseBody
    public Object getOrgDictEntry(Integer orgDictSeq,Long adapterPlanId){
        //网关没有url的请求方式
        String url = "/adapterDict/getOrgDictEntry";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgDictId",orgDictSeq);
        params.put("adapterPlanId",adapterPlanId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> orgMetaData = Arrays.asList(mapper.readValue(resultStr, String[].class));
            result.setSuccessFlg(true);
            result.setObj(resultStr);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDictSeq", orgDictSeq);
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgDictItem> orgDictItemList = orgDictItemManager.searchOrgDictItem(conditionMap);
//            List<String> orgDictItems = new ArrayList<>();
//            if (!orgDictItemList.isEmpty()){
//                for (XOrgDictItem orgDictItem : orgDictItemList) {
//                    orgDictItems.add(String.valueOf(orgDictItem.getSequence())+','+orgDictItem.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgDictItems);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }
}
