package com.yihu.ehr.adapter.controller;


import com.yihu.ehr.adapter.service.AdapterDataSetService;
import com.yihu.ehr.adapter.service.OrgAdapterPlanService;
import com.yihu.ehr.adapter.service.PageParms;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 适配管理方案适配管理
 * Created by wq on 2015/11/1.
 */

@RequestMapping("/adapterDataSet")
@Controller
public class AdapterDataSetController extends ExtendController<AdapterDataSetService>{

    @Autowired
    OrgAdapterPlanService orgAdapterPlanService;

    public AdapterDataSetController() {
        this.init(
                "/adapter/adapterDataSet/grid",       //列表页面url
                "/adapter/adapterDataSet/dialog"      //编辑页面url
        );
    }

    @RequestMapping("/dataSetList")
    @ResponseBody
    public Object search(String adapterPlanId, String searchNm, int page, int rows){

        try{
            Map<String, Object> params = new HashMap<>();
            params.put("code", nullToSpace(searchNm));
            params.put("name", nullToSpace(searchNm));
            params.put("page", page);
            params.put("size", rows);
            String resultStr = service.search(service.searchUrl.replace("{plan_id}", adapterPlanId), params);
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }

    @RequestMapping("/metaDataList")
    @ResponseBody
    public Object searchmetaData(String adapterPlanId, String dataSetId, String searchNmEntry, int page, int rows){

        try{
            if(StringUtils.isEmpty(adapterPlanId)
                    || StringUtils.isEmpty(dataSetId))
                return systemError();

            String url = "/adapter/plan/meta_data/{plan_id}/{set_id}";
            url = url.replace("{plan_id}", adapterPlanId).replace("{set_id}", dataSetId);
            Map<String, Object> params = new HashMap<>();
            params.put("code", nullToSpace(searchNmEntry));
            params.put("name", nullToSpace(searchNmEntry));
            params.put("page", page);
            params.put("size", rows);
            String resultStr = service.search(url, params);
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }


    /**
     * 标准数据元的下拉
     * @return
     */
    @RequestMapping("/getStdMetaData")
    @ResponseBody
    public Object getStdMetaData(Long adapterPlanId, Long dataSetId, String mode){

        try {
            String stdMetaDataComboUrl = "/adapter/std_meta_data/combo";
            Map<String, Object> params = new HashMap<>();
            params.put("plan_id",adapterPlanId);
            params.put("data_set_id",dataSetId);
            params.put("mode",mode);
            String resultStr = service.search(stdMetaDataComboUrl, params);
            return resultStr;
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 机构数据集下拉
     */
    @RequestMapping("/getOrgDataSet")
    @ResponseBody
    public Object getOrgDataSet(Long adapterPlanId){

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            Map<String, Object> params = new HashMap<>();
            params.put("id",adapterPlanId);
            String modelJson = orgAdapterPlanService.getModel(params);
            Envelop rs = getEnvelop(modelJson);

            if(rs.getObj()==null)
                return result;

            String url = "/adapter/org/data_sets";
            PageParms pageParms = new PageParms("organization=" + ((Map) rs.getObj()).get("org"));
            String resultStr = service.search(url, pageParms);
            rs = getEnvelop(resultStr);
            List<Map> orgDataList = rs.getDetailModelList()!=null ? rs.getDetailModelList() : new ArrayList<>();

            List<String> orgDicts = new ArrayList<>();
            for (Map orgDict : orgDataList) {
                orgDicts.add(String.valueOf(orgDict.get("sequence")) + ',' + orgDict.get("name"));
            }

            result.setObj(orgDicts);
            return result;
        } catch (Exception e) {
            return systemError();
        }
    }



    /**
     * 机构数据元下拉
     */
    @RequestMapping("/getOrgMetaData")
    @ResponseBody
    public Object getOrgMetaData(Integer orgDataSetSeq, Long adapterPlanId){

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            Map<String, Object> params = new HashMap<>();
            params.put("id",adapterPlanId);
            String modelJson = orgAdapterPlanService.getModel(params);
            Envelop rs = getEnvelop(modelJson);

            if(rs.getObj()==null)
                return result;
            String url = "/adapter/org/meta_datas";
            PageParms pageParms = new PageParms(
                    "organization=" + ((Map) rs.getObj()).get("org")
                            + (orgDataSetSeq==null ? "" : ";orgDataSet=" + orgDataSetSeq)
            );
            String resultStr = service.search(url, pageParms);
            rs = getEnvelop(resultStr);
            List<Map> orgDictList = rs.getDetailModelList()!=null ? rs.getDetailModelList() : new ArrayList<>();

            List<String> orgDicts = new ArrayList<>();
            for (Map orgDict : orgDictList) {
                orgDicts.add(String.valueOf(orgDict.get("sequence")) + ',' + orgDict.get("name"));
            }

            result.setObj(orgDicts);
            return result;
        } catch (Exception e) {
            return systemError();
        }

//        Result result = new Result();
//        try {
//            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
//            String orgCode = orgAdapterPlan.getOrg();
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
//            conditionMap.put("orgCode", orgCode);
//            List<XOrgMetaData> orgMetaDataList = orgMetaDataManager.searchOrgMetaData(conditionMap);
//            List<String> orgMetaDatas = new ArrayList<>();
//            if (!orgMetaDataList.isEmpty()){
//                for (XOrgMetaData orgMetaData : orgMetaDataList) {
//                    orgMetaDatas.add(String.valueOf(orgMetaData.getSequence())+','+orgMetaData.getName());
//                }
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//            }
//            result.setObj(orgMetaDatas);
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

//    @RequestMapping("template/adapterMetaDataInfo")
//    public Object adapterMetaDataInfoTemplate(Model model, Long id, String mode) {
//
//        try {
//            String resultStr = "";
//            Envelop result = new Envelop();
//            Map<String, Object> params = new HashMap<>();
//            params.put("id",id);
//            if(mode.equals("view") || mode.equals("modify")) {
//                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//                model.addAttribute("rs", "success");
//            }
//            model.addAttribute("info", resultStr);
//            model.addAttribute("mode",mode);
//
//            model.addAttribute("contentPage","/adapter/adapterDataSet/dialog");
//            return "simpleView";
//        } catch (Exception e) {
//            return systemError();
//        }
////        XAdapterDataSet adapterDataSet = new AdapterDataSet();
////        if(mode.equals("view") || mode.equals("modify")){
////            try {
////                adapterDataSet = adapterDataSetManager.getAdapterMetaData(id);
////                model.addAttribute("rs", "success");
////            } catch (Exception e) {
////                model.addAttribute("rs", "error");
////            }
////        }
////        model.addAttribute("info", adapterDataSet);
////        model.addAttribute("mode",mode);
////        model.addAttribute("contentPage","/adapter/adapterDataSet/dialog");
////        return "simpleView";
//    }
//
//    /**
//     * 根据方案ID及查询条件查询数据集适配关系
//     * @param adapterPlanId
//     * @param strKey
//     * @param page
//     * @param rows
//     * @return
//     */
//    @RequestMapping("/searchAdapterDataSet")
//    @ResponseBody
//    public Object searchAdapterDataSet(Long adapterPlanId, String strKey,int page, int rows){
//        String url = "/adapterSet/adapterDataSets";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("planId", adapterPlanId);
//        params.put("code", strKey);
//        params.put("name", strKey);
//        params.put("page", page);
//        params.put("rows", rows);
//        try {
//            //todo 返回result.toJson()
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            return resultStr;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////
////        try {
////            List<DataSetModel> dataSet = adapterDataSetManager.searchAdapterDataSet(adapterPlanId, strKey, page, rows);
////            int totalCount = adapterDataSetManager.searchDataSetInt(adapterPlanId, strKey);
////            result.setSuccessFlg(true);
////            result = getResult(dataSet,totalCount,page,rows);
////            return result.toJson();
////        } catch (Exception e) {
////            result.setSuccessFlg(false);
////            return result.toJson();
////        }
//    }
//
//    /**
//     * 根据dataSetId搜索数据元适配关系
//     * @param adapterPlanId
//     * @param dataSetId
//     * @param page
//     * @param rows
//     * @return
//     */
//    @RequestMapping("/searchAdapterMetaData")
//    @ResponseBody
//    public Object searchAdapterMetaData(Long adapterPlanId, Long dataSetId,String strKey,int page, int rows){
//        String url = "/adapterSet/adapterMetaDatas";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("planId", adapterPlanId);
//        params.put("dataSetId", dataSetId);
//        params.put("code", strKey);
//        params.put("name", strKey);
//        params.put("page", page);
//        params.put("rows", rows);
//        try {
//            //todo 返回result.toJson()
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            return resultStr;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////
////        List<AdapterDataSetModel> adapterDataSetModels;
////        int totalCount;
////        try {
////            adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, strKey, page, rows);
////            totalCount = adapterDataSetManager.searchMetaDataInt(adapterPlanId, dataSetId,strKey);
////        } catch (Exception e) {
////            result.setSuccessFlg(false);
////            return result.toJson();
////        }
////        result.setSuccessFlg(true);
////        result = getResult(adapterDataSetModels,totalCount,page,rows);
////        return result.toJson();
//    }
//
//    /**
//     * 根据数据集ID获取数据元适配关系明细
//     * @param id
//     * @return
//     */
////    todo:前端没有找到该路径的请求
//    @RequestMapping("/getAdapterMetaData")
//    @ResponseBody
//    public Object getAdapterMetaData(Long id){
//        String url = "/adapterSet/adapterMetaData";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("id",id);
//        try{
//            //todo 后台转换成model后传前台
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
////            ObjectMapper mapper = new ObjectMapper();
////            XAdapterDataSet adapterDataSet = mapper.readValue(resultStr, XAdapterDataSet.class);
//            result.setObj(resultStr);
//            result.setSuccessFlg(true);
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////        try {
////            XAdapterDataSet adapterDataSet = adapterDataSetManager.getAdapterMetaData(id);
////
////            result.setObj(adapterDataSet);
////            result.setSuccessFlg(true);
////            return result.toJson();
////        } catch (Exception e) {
////            result.setSuccessFlg(false);
////            return result.toJson();
////        }
//    }
//
////    private XAdapterDataSet getAdapterDataSet(Long id) {
////        return id==null? new AdapterDataSet(): adapterDataSetManager.getAdapterMetaData(id);
////    }
//
//    /**
//     * 修改数据元映射关系
//     * @param
//     * @return
//     */
//    @RequestMapping("/updateAdapterMetaData")
//    @ResponseBody
//    public Object updateAdapterMetaData(String adapterDataSetModel){
//        String url = "";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("adapterDataSetModel",adapterDataSetModel);
//        try {
//            //更新或新增 todo:可放内部去做判断
////            if (adapterDataSetModel.getId()==null) {
////                url = "/adapterSet/addAdapterMetaData";
////                params.put("adapterPlanId",adapterDataSetModel.getAdapterPlanId());
////            }else {
////                url = "/adapterSet/updateAdapterMetaData";
////            }
//            url="/adapterSet/adapterMetaData";
//            //todo 失败，返回的错误信息怎么体现？
//            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
////            ObjectMapper mapper = new ObjectMapper();
////            AdapterDataSetModel adapterDataSetModelNew = mapper.readValue(resultStr, AdapterDataSetModel.class);
//            result.setObj(resultStr);
//            result.setSuccessFlg(true);
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////        try {
////            XAdapterDataSet adapterDataSet = getAdapterDataSet(adapterDataSetModel.getId());
////            adapterDataSet.setAdapterPlanId(adapterDataSetModel.getAdapterPlanId());
////            adapterDataSet.setMetaDataId(adapterDataSetModel.getMetaDataId());
////            adapterDataSet.setDataSetId(adapterDataSetModel.getDataSetId());
////            adapterDataSet.setOrgDataSetSeq(adapterDataSetModel.getOrgDataSetSeq());
////            adapterDataSet.setOrgMetaDataSeq(adapterDataSetModel.getOrgMetaDataSeq());
////            adapterDataSet.setDataType(adapterDataSetModel.getDataType());
////            adapterDataSet.setDescription(adapterDataSetModel.getDescription());
////            if (adapterDataSetModel.getId()==null){
////                XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterDataSetModel.getAdapterPlanId());
////                adapterDataSetManager.addAdapterDataSet(adapterDataSet, orgAdapterPlan);
////            }else {
////                adapterDataSetManager.updateAdapterDataSet(adapterDataSet);
////            }
////            result.setSuccessFlg(true);
////        } catch (Exception e) {
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//    }
//
//    /**
//     * 删除数据元映射的方法
//     * @param id
//     * @return
//     */
//    @RequestMapping("/delMetaData")
//    @ResponseBody
//    public Object delMetaData(@RequestParam("id") Long[] id){
//        String url = "/adapterSet/adapterMetaData";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("ids",id);
//        try {
//            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
//            if(Boolean.parseBoolean(resultStr)){
//                result.setSuccessFlg(true);
//            }
//            else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
//            }
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        int rtn = adapterDataSetManager.deleteAdapterDataSet(id);
////        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
////        return result.toJson();
//    }
//
//    /**
//     * 标准数据元的下拉
//     * @return
//     */
//    @RequestMapping("/getStdMetaData")
//    @ResponseBody
//    public Object getStdMetaData(Long adapterPlanId,Long dataSetId,String mode){
//        String url = "/adapterSet/getStdMetaData";
//        String resultStr = "";
//        Envelop result = new Envelop();
//        Map<String, Object> params = new HashMap<>();
//        params.put("adapterPlanId",adapterPlanId);
//        params.put("dataSetId",dataSetId);
//        params.put("mode",mode);
//        try {
//            //todo 失败，返回的错误信息怎么体现？
//            //todo 新增时要过滤掉已经存在的标准
//            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
////            ObjectMapper mapper = new ObjectMapper();
////            List<String> stdMetaData = Arrays.asList(mapper.readValue(resultStr, String[].class));
//            result.setSuccessFlg(true);
//            result.setObj(resultStr);
//            return result;
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result;
//        }
////        Result result = new Result();
////        try {
////            XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(adapterPlanId);
////            XCDAVersion version = orgAdapterPlan.getVersion();
////            List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSetManager.getDataSet(dataSetId,version));
////            List<String> stdMetaData = new ArrayList<>();
////            if (!metaDataList.isEmpty()){
////                if("modify".equals(mode) || "view".equals(mode)){
////                    for (XMetaData metaData : metaDataList) {
////                        stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
////                    }
////                }
////                else{
////                    List<AdapterDataSetModel> adapterDataSetModels = adapterDataSetManager.searchAdapterMetaData(adapterPlanId, dataSetId, "", 0, 0);
////                    boolean exist = false;
////                    for (XMetaData metaData : metaDataList) {
////                        exist = false;
////                        for(AdapterDataSetModel model :adapterDataSetModels){
////                            if(model.getMetaDataId()==metaData.getId()){
////                                exist = true;
////                                break;
////                            }
////                        }
////                        if(!exist)
////                            stdMetaData.add(String.valueOf(metaData.getId())+','+metaData.getName());
////                    }
////                }
////                result.setSuccessFlg(true);
////            } else {
////                result.setSuccessFlg(false);
////            }
////            result.setObj(stdMetaData);
////        } catch (Exception e) {
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//    }
//
}
