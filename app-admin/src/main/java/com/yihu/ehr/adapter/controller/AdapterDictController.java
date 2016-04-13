package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.AdapterDataSetService;
import com.yihu.ehr.adapter.service.AdapterDictService;
import com.yihu.ehr.adapter.service.OrgAdapterPlanService;
import com.yihu.ehr.adapter.service.PageParms;
import com.yihu.ehr.agModel.adapter.AdapterPlanModel;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/11/13.
 */
@RequestMapping("/adapterDict")
@Controller
public class AdapterDictController extends ExtendController<AdapterDictService> {

    public AdapterDictController() {
        this.init(
                "/adapter/adapterDataSet/grid",       //列表页面url
                "/adapter/adapterDataSet/dictDialog"      //编辑页面url
        );
    }

    @Autowired
    OrgAdapterPlanService orgAdapterPlanService;
    /**
     * 根据方案ID及查询条件查询字典适配关系
     */
    @RequestMapping("/searchAdapterDict")
    @ResponseBody
    public Object searchAdapterDict(String adapterPlanId, String searchNm, int page, int rows) {

        try {
            String url = "/adapter/plan/{plan_id}/dicts";
            url = url.replace("{plan_id}", adapterPlanId);
            Map<String, Object> params = new HashMap<>();
            searchNm = nullToSpace(searchNm);
            params.put("code", searchNm);
            params.put("name", searchNm);
            params.put("page", page);
            params.put("size", rows);
            String resultStr = service.search(url, params);
            return resultStr;
        } catch (Exception e) {
            return systemError();
        }
    }

    /**
     * 根据dictId搜索字典项适配关系
     */
    @RequestMapping("/searchAdapterDictEntry")
    @ResponseBody
    public Object searchAdapterDictEntry(String adapterPlanId, String dictId, String searchNmEntry, int page, int rows) {

        try {
            String url = "/adapter/plan/dict/entrys/{plan_id}/{dict_id}";
            url = url.replace("{plan_id}", adapterPlanId).replace("{dict_id}", dictId);
            Map<String, Object> params = new HashMap<>();
            searchNmEntry = nullToSpace(searchNmEntry);
            params.put("code", searchNmEntry);
            params.put("name", searchNmEntry);
            params.put("page", page);
            params.put("size", rows);
            params.put("sorts", "+code");
            String resultStr = service.search(url, params);
            return resultStr;
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 标准字典项的下拉
     */
    @RequestMapping("/getStdDictEntry")
    @ResponseBody
    public Object getStdDictEntry(Long adapterPlanId, Long dictId, String mode, String searchParm, int page, int rows){

        try {
            PageParms pageParms =
                    new PageParms(rows, page)
                    .addExt("plan_id", adapterPlanId)
                    .addExt("dict_id", dictId)
                    .addExt("mode", mode)
                    .addExt("search_name", nullToSpace(searchParm));

            String resultStr = service.search("/adapter/std_dict_entries/combo", pageParms);
            return resultStr;
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 机构字典下拉
     */
    @RequestMapping("/getOrgDict")
    @ResponseBody
    public Object getOrgDict(Long adapterPlanId, String searchParm, int page, int rows){

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            Map<String, Object> params = new HashMap<>();
            params.put("id",adapterPlanId);
            String modelJson = orgAdapterPlanService.getModel(params);
            Envelop rs = getEnvelop(modelJson);

            if(rs.getObj()==null)
                return result;

            PageParms pageParms = new PageParms(rows, page)
                    .addEqual("organization", ((Map) rs.getObj()).get("org"))
                    .addGroupNotNull("name", PageParms.LIKE, searchParm, "g1")
                    .addGroupNotNull("code", PageParms.LIKE, searchParm, "g1");

            String resultStr = service.search("/adapter/org/dicts", pageParms);
            return formatComboData(resultStr, "sequence", "name");
        } catch (Exception e) {
            return systemError();
        }
    }



    /**
     * 机构字典项下拉
     */
    @RequestMapping("/getOrgDictEntry")
    @ResponseBody
    public Object getOrgDictEntry(Integer parentId, Long adapterPlanId, String searchParm, int page, int rows){

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            if(parentId == null || parentId == 0)
                return result;

            Map<String, Object> params = new HashMap<>();
            params.put("id",adapterPlanId);
            String modelJson = orgAdapterPlanService.getModel(params);
            Envelop rs = getEnvelop(modelJson);

            if(rs.getObj()==null || parentId==null || parentId==0)
                return result;

            PageParms pageParms = new PageParms(rows, page)
                    .addEqual("organization", ((Map) rs.getObj()).get("org"))
                    .addEqual("orgDict", parentId)
                    .addGroupNotNull("name", PageParms.LIKE, searchParm, "g1")
                    .addGroupNotNull("code", PageParms.LIKE, searchParm, "g1");

            String resultStr = service.search("/adapter/org/items", pageParms);
            return formatComboData(resultStr, "sequence", "name");
        } catch (Exception e) {
            return systemError();
        }
    }

}
