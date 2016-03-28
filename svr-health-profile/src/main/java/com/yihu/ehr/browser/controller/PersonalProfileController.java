package com.yihu.ehr.browser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping("/browser/personal-profile")
public class PersonalProfileController extends BaseRestController {
    String ApiVersion = "v1.0";
    //String PersonalDetailInfo = "HDSA00_01";

//    @Resource(name = Services.ArchiveTemplateManager)
//    private XArchiveTplManager templateManager;


    @Autowired
    private PersonalProfileRestController personalProfileRestController;



//    /**
//     * 档案浏览器
//     *
//     * @param model
//     * @param source        请求来源，如PC，手机端
//     * @param demographicId
//     * @param from
//     * @param to
//     * @return
//     */
//    @RequestMapping("/index")
//    public String index(Model model,
//                        @RequestParam(value = "source", required = false) String source,
//                        @RequestParam(value = "demographic_id", required = true) String demographicId,
//                        @RequestParam(value = "orgs",required = false)  String[] orgs,
//                        @RequestParam(value = "archivetype",required = false)  String archivetype,
//                        @RequestParam(value = "resource",required = false)  String resource,
//                        @RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
//                        @RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
//        //modify by cws 20160305 add condition for yi hu timeline
//        personalProfileRestController.timeline2(ApiVersion, demographicId,orgs,archivetype,resource, from, to);
//        //RestEcho restEcho = personalProfileRestController.timeline(ApiVersion, demographicId, from, to);
//
//        model.addAttribute("data", "success");
//        return "/browser/index";
//    }
//
//    /**
//     * 加载具体CDA文档的所有数据集数据，并与HTML模板一起返回。
//     * <p>
//     * CDA文档的加载与CDA文档相关，这些数据由CDA文档-数据集之间的关系维护。
//     *
//     * @param model
//     * @param source
//     * @param orgCode
//     * @param cdaDocumentId
//     * @param cdaVersion
//     * @param dataSetList
//     * @return
//     */
//    @RequestMapping("/document")
//    public String document(Model model,
//                           @RequestParam(value = "source", required = false) String source,
//                           @RequestParam(value = "org_code", required = true) String orgCode,
//                           @RequestParam(value = "cda_document_Id", required = true) String cdaDocumentId,
//                           @RequestParam(value = "cda_version", required = true) String cdaVersion,
//                           @RequestParam(value = "data_set_list", required = true) String dataSetList,
//                           @RequestParam(value = "origin_data", required = true) boolean originData) {
//        // 获取数据
//        personalProfileRestController.loadDocumentByDataSet(ApiVersion, cdaVersion, dataSetList, originData);
//
//        XArchiveTpl archiveTpl = templateManager.getArchiveTemplate(orgCode, cdaDocumentId, cdaVersion);
//
//        String tplContent;
//        if (StringUtils.isEmpty(source) || source.equals("pc")) {
//            tplContent = archiveTpl.getPcTplContent();
//        } else {
//            tplContent = archiveTpl.getMobileTplContent();
//        }
//
//        model.addAttribute("html", tplContent);
//        model.addAttribute("data", restEcho.getResult().toString());
//        return "/browser/archive";
//    }
//
//
//    /**
//     * 加载具体CDA文档的所有数据集数据，并与HTML模板一起返回。
//     * <p>
//     * CDA文档的加载与CDA文档相关，这些数据由CDA文档-数据集之间的关系维护。
//     *
//     * @param source
//     * @param orgCode
//     * @param cdaDocumentId
//     * @param cdaVersion
//     * @param dataSetList
//     * @return
//     */
//    @RequestMapping(value = "/document_data", produces="application/json;charset=UTF-8")
//    @ResponseBody
//    public Object document(
//           @RequestParam(value = "source", required = false) String source,
//           @RequestParam(value = "org_code", required = true) String orgCode,
//           @RequestParam(value = "cda_document_Id", required = true) String cdaDocumentId,
//           @RequestParam(value = "cda_version", required = true) String cdaVersion,
//           @RequestParam(value = "data_set_list", required = true) String dataSetList,
//           @RequestParam(value = "origin_data", required = true) boolean originData) {
//
//        Map map = new HashMap<>();
//        String tplContent;
//        XArchiveTpl archiveTpl = null;
//        personalProfileRestController.loadDocumentByDataSet(ApiVersion, cdaVersion, dataSetList, originData);
//        archiveTpl = templateManager.getArchiveTemplate(orgCode, cdaDocumentId, cdaVersion);
//
//        if (StringUtils.isEmpty(source) || source.equals("pc")) {
//            tplContent = archiveTpl.getPcTplContent();
//        } else {
//            tplContent = archiveTpl.getMobileTplContent();
//        }
//
//        map.put("html", tplContent);
//
//        map.put("htmlUrl", archiveTpl.getMobileTemplate());
//        map.put("data", restEcho.getResult().toString());
//        return map;
//    }

    /**
     * 只请求数据集内容。
     *
     * @param cdaVersion
     * @param dataSetList
     * @param originData
     * @return
     */
    @RequestMapping("/data")
    @ResponseBody
    public String document(
                           @RequestParam(value = "cda_version", required = true) String cdaVersion,
                           @RequestParam(value = "data_set_list", required = true) String dataSetList,
                           @RequestParam(value = "origin_data", required = true) boolean originData) {
        personalProfileRestController.loadDocumentByDataSet(ApiVersion, cdaVersion, dataSetList, originData);

        return "success";
    }
}
