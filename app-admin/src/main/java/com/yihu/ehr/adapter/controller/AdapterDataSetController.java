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
    }
}
