//package com.yihu.ehr.browser.service;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.factory.ServiceFactory;
//import com.yihu.ha.organization.model.OrgManager;
//import com.yihu.ha.organization.model.XOrganization;
//import com.yihu.ha.util.operator.StringUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Sand
// * @version 1.0
// * @created 2015.12.26 16:08
// */
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
//public class HomePageEvent {
//    public String date;
//    public String areaCity;
//    public String orgCode;
//    public String orgName;
//    public String diseaseName;
//    public String summary;
//
//    public List<DocumentModel> documents = new ArrayList<>();
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getAreaCity() {
//        String areaCity = "";
//        OrgManager orgManager = ServiceFactory.getService(Services.OrgManager);
//        if(!StringUtil.isEmpty(orgCode)){
//            XOrganization org = orgManager.getOrg(orgCode);
//            if(org != null){
//                areaCity = org.getLocation().getCity();
//            }
//        }
//        return areaCity;
//    }
//
//    public String getOrgCode() {
//        return orgCode;
//    }
//
//    public void setOrgCode(String orgCode) {
//        this.orgCode = orgCode;
//    }
//
//    public String getOrgName() {
//        String orgName = "";
//        OrgManager orgManager = ServiceFactory.getService(Services.OrgManager);
//        if(!StringUtil.isEmpty(orgCode)){
//            XOrganization org = orgManager.getOrg(orgCode);
//            if(org != null){
//                orgName = org.getFullName();
//            }
//        }
//        return orgName;
//    }
//
//    public String getDiseaseName() {
//        return diseaseName;
//    }
//
//    public void setDiseaseName(String diseaseName) {
//        this.diseaseName = diseaseName;
//    }
//
//    public String getSummary() {
//        while(StringUtil.isEmpty(summary)){
//            switch(summary)
//            {
//                case "基本信息": summary = "社区";
//                case "疾病管理": summary = "社区";
//                case "社区健康体检": summary = "社区";
//                case "儿童保健": summary = "妇幼";
//                case "妇女保健": summary = "妇幼";
//                case "疾病控制": summary = "计划免疫";
//                case "健康体检": summary = "体检";
//            }
//        }
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//}
