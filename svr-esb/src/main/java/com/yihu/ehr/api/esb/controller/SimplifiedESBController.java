package com.yihu.ehr.api.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.api.esb.model.HosLog;
import com.yihu.ehr.api.esb.model.HosSqlTask;
import com.yihu.ehr.api.esb.service.SimplifiedESBService;
import com.yihu.ehr.config.FastDFSConfig;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.DateFormatter;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * Created by chenweida on 2016/3/1.
 */

@RestController
@Api(protocols = "https", value = "simplified-esb", description = "简易ESB服务临时接口")
public class SimplifiedESBController {
    @Resource(name = "simplifiedESBService")
    private SimplifiedESBService simplifiedESBService;
    @Autowired
    private FastDFSConfig FastDFSConfig;

    /**
     * 上传日志
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadLog", method = RequestMethod.POST)
    public boolean uploadLog(
            @RequestParam(value = "orgCode", required = true) String orgCode,
            @RequestParam(value = "ip", required = true) String ip,
            @RequestParam(value = "file", required = true) String file) {
        try {
            InputStream in = new ByteArrayInputStream(file.getBytes());
            FastDFSUtil fdfs = FastDFSConfig.fastDFSUtil();
            ObjectNode jsonResult = fdfs.upload(in, "log", "");
            String filePath = jsonResult.get("fid").textValue();
            fdfs.download(jsonResult.get("groupName").textValue(), jsonResult.get("remoteFileName").textValue(), "E:\\");
            HosLog lh = new HosLog();
            lh.setOrgCode(orgCode);
            lh.setUploadTime(DateFormatter.simpleDateTimeFormat(new Date()));
            lh.setFilePath(filePath);
            lh.setId(ip);
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
    @RequestMapping(value = "/getUpdateFlag", method = RequestMethod.GET)
    public String getUpdateFlag(
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @RequestParam(value = "systemCode", required = true) String systemCode,
            @RequestParam(value = "orgCode", required = true) String orgCode) {
        try {
            HosEsbMiniRelease h = simplifiedESBService.getUpdateFlag(versionCode, systemCode, orgCode);
            if (h != null) {
                ObjectMapper om = new ObjectMapper();
                return om.writeValueAsString(om);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 下载项目
     *
     * @param systemCode
     * @param orgCode
     * @param response
     */
    @RequestMapping(value = "/downUpdateWar", method = RequestMethod.POST)
    public String downUpdateWar(
            @RequestParam(value = "versionCode", required = true) String systemCode,
            @RequestParam(value = "versionCode", required = true) String orgCode,
            @RequestParam(value = "versionCode", required = true) HttpServletResponse response) {
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
        return "";
    }

    /**
     * 上传客户端升级数据
     *
     * @param systemCode
     * @param orgCode
     * @param versionCode
     * @param versionName
     * @param updateDate
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadResult", method = RequestMethod.POST)
    public String uploadResult(
            @RequestParam(value = "systemCode", required = true) String systemCode,
            @RequestParam(value = "orgCode", required = true) String orgCode,
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @RequestParam(value = "versionName", required = true) String versionName,
            @RequestParam(value = "updateDate", required = true) String updateDate) {
        String hsa = null;
        try {
            hsa = simplifiedESBService.uploadResult(systemCode, orgCode, versionCode, versionName, updateDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hsa;
    }

    /**
     * 补采
     *
     * @param systemCode
     * @param orgCode
     * @return {"id":"xxxxx","startTime":"yyyy-MM-dd HH:mm:ss","endTime":"yyyy-MM-dd HH:mm:ss"}
     */
    @ResponseBody
    @RequestMapping(value = "/fillMining", method = RequestMethod.POST)
    public String fillMining(
            @RequestParam(value = "systemCode", required = true) String systemCode,
            @RequestParam(value = "orgCode", required = true) String orgCode) {
        String hsa = null;
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
    public String changeFillMiningStatus(
            @RequestParam(value = "message", required = true) String message,
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "status", required = true) String status) {
        try {
            simplifiedESBService.changeFillMiningStatus(id, message, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
    public String hisPenetration(
            @RequestParam(value = "systemCode", required = true) String systemCode,
            @RequestParam(value = "orgCode", required = true) String orgCode) {
        String returnString = "";
        try {
            HosSqlTask hq = simplifiedESBService.hisPenetration(systemCode, orgCode);
            if (hq != null) {
                returnString = "{\"id\":\"" + hq.getId() + "\",\"sql\":\"" + hq.getSqlscript() + "\"}";
            }
            return returnString;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 修改his穿透查询状态
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/changeHisPenetrationStatus", method = RequestMethod.POST)
    public String changeHisPenetrationStatus(
            @RequestParam(value = "result", required = true) String result,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "id", required = true) String id) {
        try {
            simplifiedESBService.changeHisPenetrationStatus(id, status, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
