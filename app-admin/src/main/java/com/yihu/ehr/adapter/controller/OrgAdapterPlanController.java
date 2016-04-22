package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.OrgAdapterPlanService;
import com.yihu.ehr.adapter.service.PageParms;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/10/27.
 */
@Controller
@RequestMapping("/adapter")
public class OrgAdapterPlanController extends ExtendController<OrgAdapterPlanService> {
    @Value("${service-gateway.adaption}")
    public String adaptionUrl;

    public OrgAdapterPlanController() {
        this.init(
                "/adapter/adapter",                 //列表页面url
                "/adapter/adapterInfoDialog"      //编辑页面url
        );

        Map<String, String> comboKv = new HashMap<>();
        comboKv.put("code", "id");
        comboKv.put("value", "name");
        comboKv.put("org", "org");
        this.comboKv = comboKv;
    }

    /**
     * 跳转至定制页面
     */
    @RequestMapping("/getAdapterCustomize")
    public Object getAdapterCustomize(Model model, Long planId, String version) {

        try {
            String url = "/adapter/plan/adapterCustomizes/" + planId;
            String resultStr = "";
            Map<String, Object> params = new HashMap<>();
            params.put("version", version);
            resultStr = HttpClientUtil.doGet(service.comUrl + url, params, service.username, service.password);
            model.addAttribute("planId", planId);
            model.addAttribute("allData", resultStr);
            model.addAttribute("contentPage", "/adapter/adapterCustomize");
            return "generalView";
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 定制数据集
     */
    @RequestMapping("/adapterDataSet")
    @ResponseBody
    public Object adapterDataSet(String planId, String customizeData) {

        try {
            customizeData = customizeData.replace("DataSet", "").replace("MetaData", "");
            String url = adaptionUrl + "/plan/adapterDataSet" ;
            String resultStr = "";
            Envelop result = new Envelop();
            MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<String, String>();
            conditionMap.add("customizeData", customizeData);
            conditionMap.add("planId", planId);

            RestTemplates template = new RestTemplates();
            resultStr = template.doPost(url, conditionMap);
//            resultStr = service.doLargePost(url, params);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg("定制失败！");
            }
            return result;
        } catch (Exception e) {
            return systemError();
        }
    }

    @RequestMapping("/versions")
    @ResponseBody
    public Object searchVersions(String searchNm){

        try {
            String url = "/version/versions";
            String filters = "";
            if(!StringUtils.isEmpty(searchNm)){
                filters = "version?"+searchNm+" g1;versionName?"+searchNm+" g1;";
            }
            String envelopStr = service.search(url, new PageParms(filters));
            return envelopStr;
        } catch (Exception ex) {
            return systemError();
        }
    }


    @RequestMapping("/getOrgList")
    @ResponseBody
    public Object getOrgList(String type, String version, String mode, String searchParm, int page, int rows) {

        try {
            if(!"notVersion".equals(version) && (StringUtils.isEmpty(type) || StringUtils.isEmpty(version)))
                return new Envelop();

            String adapterOrgs = "";
            if(!"modify".equals(mode)){
                String rs =service.search(new PageParms().addEqual("version", version));
                Envelop envelop = getEnvelop(rs);
                if(!envelop.isSuccessFlg())
                    return systemError();
                if(envelop.getDetailModelList()!=null){
                    List<Map<String, String>> ls = envelop.getDetailModelList();
                    for(Map<String, String> map : ls){
                        adapterOrgs += "," + map.get("org");
                    }
                }
            }

            String url = "/adapterOrg/orgs";
            PageParms pageParms = new PageParms(rows, page)
                    .addEqualNotNull("type", type)
                    .addNotEqualNotNull("org", adapterOrgs.length()>0 ? adapterOrgs.substring(1) : "")
                    .addLikeNotNull("name", searchParm);
            String resultStr = service.search(url, pageParms);
            return formatComboData(resultStr, "code", "name");
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 适配版本发布
     * 1.生成适配版本文件并记录文件位置；2.修改适配方案状态
     */
    @RequestMapping("/adapterDispatch")
    @ResponseBody
    public Envelop adapterDispatch(String planId) {
        try {
            String url = "/adapter/plan/"+ planId +"/public";
            boolean resultStr = Boolean.parseBoolean(service.doPut(service.comUrl + url, new HashMap<>()));
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(resultStr);
            return envelop;
        } catch (Exception ex) {
            return systemError();
        }
    }
}
