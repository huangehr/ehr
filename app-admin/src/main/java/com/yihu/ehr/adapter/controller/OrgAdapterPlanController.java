package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.OrgAdapterPlanService;
import com.yihu.ehr.adapter.service.PageParms;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     *
     * @param model
     * @param planId
     * @param version
     * @return
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
            model.addAttribute("contentPage", "adapter/adapterCustomize");
            return "generalView";
        } catch (Exception e) {
            return systemError();
        }
    }


    /**
     * 定制数据集
     * 2015-12-31  定制速度优化以及添加事务控制 by lincl
     *
     * @param planId
     * @param customizeData
     * @return
     */
    @RequestMapping("/adapterDataSet")
    @ResponseBody
    public Object adapterDataSet(Long planId, String customizeData) {

        try {
            customizeData = customizeData.replace("DataSet", "").replace("MetaData", "");
            String url = "/adapter/plan/adapterDataSet/" + planId;
            String resultStr = "";
            Envelop result = new Envelop();
            Map<String, Object> params = new HashMap<>();
            params.put("customizeData", customizeData);

            resultStr = HttpClientUtil.doPost(service.comUrl + url, params, service.username, service.password);
//            RestTemplate template = new RestTemplate();
//            Map<String,Object> conditionMap = new HashMap<>();
//            conditionMap.put("customizeData", toJson(customizeData));
//            resultStr = template.postForObject(service.comUrl+url,conditionMap,String.class);
//            RestTemplates template = new RestTemplates();
//            MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<String, String>();
//            conditionMap.add("customizeData", customizeData);
//            resultStr = template.doPost(service.comUrl+url, conditionMap);
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
    public Object getOrgList(String type, String version, String mode) {

        try {
            String adapterOrgs = "";
            if(!"modify".equals(mode)){
                String rs =service.search(new PageParms("version=" + version));
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
            PageParms pageParms = new PageParms(
                    StringUtils.isEmpty(type) ? "" : "type=" + type
                    + (adapterOrgs.length()>0 ? ";org<>" + adapterOrgs.substring(1) :"") );
            String resultStr = service.search(url, pageParms);
            return resultStr;
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
