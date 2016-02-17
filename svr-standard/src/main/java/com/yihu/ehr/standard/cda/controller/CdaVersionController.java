package com.yihu.ehr.standard.cda.controller;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.standard.cda.service.CDAVersion;
import com.yihu.ehr.standard.cda.service.CDAVersionManager;
import com.yihu.ehr.standard.cda.service.CDAVersionModel;
import com.yihu.ehr.util.controller.BaseRestController;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015/12/30.
 */
@RequestMapping("/cdaVersion")
@RestController
public class CdaVersionController extends BaseRestController {

    @Autowired
    private CDAVersionManager cdaVersionManager;

    @RequestMapping("getVersionList")
    @ResponseBody
    public Object getCDAVersions(int page, int rows){
        List<CDAVersionModel> models = new ArrayList<>();
        List<CDAVersion> cdaVersions =cdaVersionManager.getVersionList();
        return cdaVersions;
    }

    @RequestMapping("isLatestVersion")
    @ResponseBody
    public boolean isLatestVersion(String strVersion){
        CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
        String latestVersion = xcdaVersion.getVersion();
        if(latestVersion!=null&&latestVersion.equals(strVersion)){
            return true;
        }else{
            return false;
        }
    }


    @RequestMapping("addVersion")
    @ResponseBody
    public boolean addVersion(String latestVersion,String versionName,String userId) throws Exception {
        CDAVersion baseVersion = cdaVersionManager.getLatestVersion();
        //String author = user.getLoginCode();
        MUser author = new MUser();
        CDAVersion xcdaVersion = cdaVersionManager.createStageVersion(baseVersion,userId);
        if (xcdaVersion!=null){
            return true;
        }else{
            return false;
        }
    }

    @RequestMapping("searchVersions")
    @ResponseBody
    public Object searchVersions(String searchNm,int page, int rows){
        List<CDAVersionModel> models = new ArrayList<>();;
        Map<String, Object> args = new HashMap<>();
        args.put("version",searchNm);
        args.put("versionName",searchNm);
        args.put("page",page);
        args.put("rows",rows);
        Integer totalCount=0;
        List<CDAVersion> xcdaVersionList = cdaVersionManager.searchVersions(args);
        for(CDAVersion s:xcdaVersionList){
            CDAVersionModel model = new CDAVersionModel();
            model.setAuthor(s.getAuthor());
            model.setBaseVersion(s.getBaseVersion());
            model.setCommitTime(s.getCommitTime()+"");
            model.setStage(s.isInStage());
            model.setVersion(s.getVersion());
            model.setVersionName(s.getVersionName());
            models.add(model);
        }
        if(page!=0) {
            totalCount = cdaVersionManager.searchVersionInt(args);
        }else {
            page=1;
            rows=1;
        }
        return getResult(models,totalCount,page,rows);

    }

    @RequestMapping("dropCDAVersion")
    public boolean dropCDAVersion(String strVersion){
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(strVersion);
        cdaVersionManager.dropVersion(cdaVersion);
        return true;
    }

    @RequestMapping("getVersionById")
    @ResponseBody
    public Object getVersionById(Model model, String strVersion, String mode){
        CDAVersionModel cdaVersionModel = new CDAVersionModel();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
        if(cdaVersion!=null) {
            cdaVersionModel.setAuthor(cdaVersion.getAuthor());
            cdaVersionModel.setBaseVersion(cdaVersion.getBaseVersion());
            cdaVersionModel.setCommitTime(sdf.format(cdaVersion.getCommitTime()));
            cdaVersionModel.setStage(cdaVersion.isInStage());
            cdaVersionModel.setVersion(cdaVersion.getVersion());
            cdaVersionModel.setVersionName(cdaVersion.getVersionName());
        }
        return cdaVersionModel;
    }

    // 删除编辑状态的版本
    @RequestMapping("revertVersion")
    @ResponseBody
    public boolean revertVersion(String strVersion){
        CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
        cdaVersionManager.revertVersion(cdaVersion);
        return true;
    }

    //发布新版本
    @RequestMapping("commitVersion")
    @ResponseBody
    public boolean commitVersion(String strVersion){
        CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
        cdaVersionManager.commitVersion(cdaVersion);
        return true;
    }

    //将最新的已发布版本回滚为编辑状态
    @RequestMapping("rollbackToStage")
    @ResponseBody
    public boolean rollbackToStage(String strVersion){
        CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
        cdaVersionManager.rollbackToStage(cdaVersion);
        return true;
    }

    @RequestMapping("updateVersion")
    @ResponseBody
    public boolean updateVersion(String cdaVersionModelJsonData) throws IOException {
        CDAVersion cdaVersion = new CDAVersion();
        String versionModel = cdaVersionModelJsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        CDAVersionModel model = objectMapper.readValue(versionModel, CDAVersionModel.class);
        cdaVersion.setVersion(model.getVersion());
        cdaVersion.setVersionName(model.getVersionName());
        cdaVersion.setAuthor(model.getAuthor());
        cdaVersion.setCommitTime(new Date());
        cdaVersion.setInStage(model.getStage());
        cdaVersion.setBaseVersion(model.getBaseVersion());
        cdaVersionManager.updateVersion(cdaVersion);
        return true;
    }

    //将最新的已发布版本回滚为编辑状态
    @RequestMapping("checkVersionName")
    @ResponseBody
    public boolean checkVersionName(String versionName){
        int num = cdaVersionManager.checkVersionName(versionName);
        if (num>0){
            return true;
        }else{
            return false;
        }
    }

    // 检查是否存在处于编辑状态的版本
    @RequestMapping("existInStage")
    @ResponseBody
    public boolean existInStage(){
        String baseVersion = "";
        int num = cdaVersionManager.searchInStage();
        if (num>0){
            return true;
        }else{
            CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
            if (xcdaVersion==null){
                baseVersion = CDAVersionManager.FBVersion;
            }else{
                baseVersion = xcdaVersion.getVersion();
            }
            return false;
        }
    }
}