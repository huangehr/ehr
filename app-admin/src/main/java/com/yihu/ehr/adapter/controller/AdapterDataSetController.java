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

/**
 * 适配管理方案适配管理
 * Created by wq on 2015/11/1.
 */

@RequestMapping("/adapterDataSet")
@Controller
public class AdapterDataSetController extends ExtendController<AdapterDataSetService> {

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
    public Object search(String adapterPlanId, String searchNm, int page, int rows) {

        try {
            searchNm = nullToSpace(searchNm);

            return service.search(
                    service.searchUrl.replace("{plan_id}", adapterPlanId),
                    new PageParms(rows, page)
                            .addExt("code", searchNm)
                            .addExt("name", searchNm));
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }

    @RequestMapping("/metaDataList")
    @ResponseBody
    public Object searchmetaData(String adapterPlanId, String dataSetId, String searchNmEntry, int page, int rows) {

        try {
            if (StringUtils.isEmpty(adapterPlanId)
                    || StringUtils.isEmpty(dataSetId))
                return pramsError();

            String url = "/adapter/plan/meta_data/{plan_id}/{set_id}";
            url = url.replace("{plan_id}", adapterPlanId).replace("{set_id}", dataSetId);
            searchNmEntry = nullToSpace(searchNmEntry);

            return service.search(
                    url,
                    new PageParms(rows, page)
                            .addExt("code", searchNmEntry)
                            .addExt("name", searchNmEntry)
                            .setSorts("+inner_code"));
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }


    /**
     * 标准数据元的下拉
     *
     * @return
     */
    @RequestMapping("/getStdMetaData")
    @ResponseBody
    public Object getStdMetaData(Long adapterPlanId, Long dataSetId, String mode, String searchParm, int page, int rows) {

        try {

            return service.search(
                    "/adapter/std_meta_data/combo",
                    new PageParms(rows, page)
                            .addExt("plan_id", adapterPlanId)
                            .addExt("data_set_id", dataSetId)
                            .addExt("mode", mode)
                            .addExt("search_name", nullToSpace(searchParm)));
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 机构数据集下拉
     */
    @RequestMapping("/getOrgDataSet")
    @ResponseBody
    public Object getOrgDataSet(Long adapterPlanId, String searchParm, int page, int rows) {

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            Envelop rs = getEnvelop(
                    orgAdapterPlanService.getModel(
                        new PageParms().addExt("id", adapterPlanId)));

            if (rs.getObj() == null)
                return result;

            String resultStr = service.search(
                    "/adapter/org/data_sets",
                    new PageParms(rows, page)
                            .addEqual("organization", ((Map) rs.getObj()).get("org"))
                            .addGroupNotNull("name", PageParms.LIKE, searchParm, "g1")
                            .addGroupNotNull("code", PageParms.LIKE, searchParm, "g1"));

            return formatComboData(resultStr, "sequence", "name");
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 机构数据元下拉
     */
    @RequestMapping("/getOrgMetaData")
    @ResponseBody
    public Object getOrgMetaData(Integer parentId, Long adapterPlanId, String searchParm, int page, int rows) {

        try {
            Envelop result = new Envelop();
            result.setSuccessFlg(true);

            if(parentId == null || parentId == 0)
                return result;

            Envelop rs = getEnvelop(
                    orgAdapterPlanService.getModel(
                        new PageParms().addExt("id", adapterPlanId)));

            if (rs.getObj() == null)
                return result;

            String resultStr =
                    service.search(
                            "/adapter/org/meta_datas",
                            new PageParms(rows, page)
                                    .addEqual("organization", ((Map) rs.getObj()).get("org"))
                                    .addEqualNotNull("orgDataSet", parentId)
                                    .addGroupNotNull("name", PageParms.LIKE, searchParm, "g1")
                                    .addGroupNotNull("code", PageParms.LIKE, searchParm, "g1"));

            return formatComboData(resultStr, "sequence", "name");
        } catch (Exception e) {
            return systemError();
        }
    }
}
