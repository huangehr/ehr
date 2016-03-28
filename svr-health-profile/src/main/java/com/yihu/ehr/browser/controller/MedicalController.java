package com.yihu.ehr.browser.controller;

import com.yihu.ehr.browser.feign.OrganizationClient;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.util.DateFormatter;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * @author yzh
 * @version 1.0
 * @created 2016.01.14 18:43
 */
@RestController
@RequestMapping("/browser/medical")
public class MedicalController extends BaseRestController {

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private PersonalProfileRestController personalProfileRestController;



    @RequestMapping(value = "/detailData", produces="text/html;charset=UTF-8")
    @ResponseBody
    public String detailforyihu(HttpServletRequest request) throws ParseException {
        String demographicId = request.getParameter("demographic_id");
        String archiveType = request.getParameter("archive_type");
        String resource = request.getParameter("resource");
        String orgCode = request.getParameter("org_code");
        String province = request.getParameter("province");
        String city = request.getParameter("city");

        List<String> orgs = new ArrayList<>();
        if(StringUtils.isEmpty(orgCode)){
            if(!StringUtils.isEmpty(province)|| !StringUtils.isEmpty(city)){
                Map location = new HashMap<>();
                location.put("province",province);
                location.put("city",city);

                //// TODO: 2016/3/25
                //List<MOrganization> orgList = organizationClient.searchByAddress(location);
                List<MOrganization> orgList = new ArrayList<>();
                if(orgList.size() != 0){
                    for(MOrganization org : orgList){
                        orgs.add(org.getOrgCode());
                    }
                }
            }
        }else{
            orgs.add(orgCode);
        }

        Date from = DateFormatter.simpleDateParse(request.getParameter("from"));
        Date to = DateFormatter.simpleDateParse(request.getParameter("to"));
        //modify by cws 20160305 add condition for yi hu timeline
        personalProfileRestController.timeline2("v1.0",  demographicId, orgs.toArray(new String[orgs.size()]),archiveType,resource,from, to);
        return "success";
    }
}
