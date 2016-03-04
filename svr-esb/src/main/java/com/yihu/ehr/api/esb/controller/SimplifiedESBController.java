package com.yihu.ehr.api.esb.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.esb.Util.NetworkUtil;
import com.yihu.ehr.api.esb.model.HosAcqTask;
import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.api.esb.model.HosLog;
import com.yihu.ehr.api.esb.model.HosSqlTask;
import com.yihu.ehr.api.esb.service.SimplifiedESBService;
import com.yihu.ehr.config.FastDFSConfig;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.DateFormatter;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by chenweida on 2016/3/1.
 */
@Controller
@RequestMapping(ApiVersion.Version1_0 + "/esb")
@Api(protocols = "https", value = "esb", description = "esb管理", tags = {"esb管理"})
public class SimplifiedESBController {
    @Resource(name = "simplifiedESBService")
    private SimplifiedESBService simplifiedESBService;
    @Autowired
    private FastDFSConfig FastDFSConfig;

    /**
     * 上传日志
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadLog", method = RequestMethod.POST)
    public boolean uploadLog(String orgCode, HttpServletRequest request) {
        try {
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
            FastDFSUtil fdfs=FastDFSConfig.fastDFSUtil();
            ObjectNode jsonResult = fdfs.upload(file.getInputStream(), "txt", "");
            String filePath = jsonResult.get("fid").textValue();
            HosLog lh = new HosLog();
            lh.setOrgCode(orgCode);
            lh.setIp(NetworkUtil.getIpAddress(request));
            lh.setUploadTime(DateFormatter.simpleDateTimeFormat(new Date()));
            lh.setFilePath(filePath);
            simplifiedESBService.saveHosLog(lh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询版本是否需要更新
     *
     * @param versionCode
     * @param systemCode
     * @param orgCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUpdateFlag", method = RequestMethod.POST)
    public boolean getUpdateFlag(String versionCode, String systemCode, String orgCode) {
        boolean isUpdateFlag = false;
        try {
            isUpdateFlag = simplifiedESBService.getUpdateFlag(versionCode, systemCode, orgCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isUpdateFlag;
    }

    /**
     * 下载项目
     *
     * @param systemCode
     * @param orgCode
     * @param response
     */
    @RequestMapping(value = "/downUpdateWar", method = RequestMethod.POST)
    public void downUpdateWar(String systemCode, String orgCode, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            HosEsbMiniRelease he = simplifiedESBService.getSimplifiedESBBySystemCodes(systemCode, orgCode);
            File file = new File(he.getFile());
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(he.getFile()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 补采
     *
     * @param systemCode
     * @param orgCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fillMining", method = RequestMethod.POST)
    public List<HosAcqTask> fillMining(String systemCode, String orgCode) {
        List<HosAcqTask> hsa = null;
        try {
            hsa = simplifiedESBService.fillMining(systemCode, orgCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hsa;
    }

    /**
     * 修改补采状态
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeFillMiningStatus", method = RequestMethod.POST)
    public void changeFillMiningStatus(String result, String message, String id) {
        try {
            simplifiedESBService.changeFillMiningStatus(id, message, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * his穿透查询
     *
     * @param systemCode
     * @param orgCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/hisPenetration", method = RequestMethod.POST)
    public String hisPenetration(String systemCode, String orgCode) {
        String returnString = "";
        try {
            HosSqlTask hq = simplifiedESBService.hisPenetration(systemCode, orgCode);
            if (hq != null) {
                returnString = "{\"id\":\"" + hq.getId() + "\",\"sql\":\"" + hq.getSql() + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnString;
    }

    /**
     * 修改his穿透查询状态
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeHisPenetrationStatus", method = RequestMethod.POST)
    public void changeHisPenetrationStatus(String result, String message, String id) {
        try {
            simplifiedESBService.changeHisPenetrationStatus(id, message, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
