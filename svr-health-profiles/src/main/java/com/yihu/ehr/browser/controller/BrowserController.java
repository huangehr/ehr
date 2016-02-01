package com.yihu.ehr.browser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ha.archives.model.ehr.XEhrArchive;
import com.yihu.ha.archives.model.ehr.XEhrArchiveManager;
import com.yihu.ha.archives.model.ehr.XEhrDataSet;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.patient.model.demographic.DemographicId;
import com.yihu.ha.template.model.XArchiveTpl;
import com.yihu.ha.template.model.XArchiveTplManager;
import com.yihu.ha.util.controller.BaseController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/browser")
public class BrowserController extends BaseController {
    @Resource(name = Services.ArchiveTemplateManager)
    private XArchiveTplManager templateManager;

    @Resource(name = Services.EhrArchiveManager)
    private XEhrArchiveManager archiveManager;

    @RequestMapping("/index")
    public String index(Model model,
                        @RequestParam(value = "demographic_id", required = true) String demographicId,
                        @RequestParam(value = "from", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
                        @RequestParam(value = "to", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date to){
        try {
            List<XEhrArchive> archiveList = archiveManager.getArchiveList(new DemographicId(demographicId), from, to, false, false);

            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode archiveArray = root.putArray("list");
            for (XEhrArchive archive : archiveList){
                archiveArray.addPOJO(archive.toJson());
            }

            model.addAttribute("data", root.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "/browser/index";
    }

    @RequestMapping("/dataset")
    public String dataSet(Model model,
                          @RequestParam(value = "datasetcode", required = true) String dataSetCode,
                          @RequestParam(value = "orgcode", required = true) String orgCode,
                          @RequestParam(value = "cdaversion", required = true) String cdaVersion,
                          @RequestParam(value = "rowkeys", required = true) String rowKeyList) {
        try {
            // 获取模板
            XArchiveTpl template = templateManager.getArchiveTemplate(orgCode, dataSetCode, cdaVersion);
            String htmlContent = template.getPcTplContent();
            model.addAttribute("html", htmlContent);

            // 获取数据
            String[] rowKeys = rowKeyList.split(File.pathSeparator);
            XEhrDataSet dataSet = archiveManager.getDataSet(cdaVersion, dataSetCode, rowKeys);

            model.addAttribute("data", dataSet.toJson().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "/browser/dataset";
    }
}
