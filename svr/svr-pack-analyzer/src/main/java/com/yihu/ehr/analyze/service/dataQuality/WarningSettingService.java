package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import com.yihu.ehr.entity.quality.DqPaltformUploadWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yeshijie on 2018/5/28.
 */
@Service
public class WarningSettingService {

    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private DqPaltformReceiveWarningService dqPaltformReceiveWarningService;
    @Autowired
    private DqPaltformResourceWarningService dqPaltformResourceWarningService;
    @Autowired
    private DqPaltformUploadWarningService dqPaltformUploadWarningService;
    @Value("&{quality.orgCode}")
    private String defaultOrgCode;


    /**
     * 查找接收预警设置
     * @param orgCode
     */
    public DqPaltformReceiveWarning getReceiveWarning(String orgCode){

        DqPaltformReceiveWarning warning = dqPaltformReceiveWarningService.findByOrgCode(orgCode);
        if(warning==null){
            warning = dqPaltformReceiveWarningService.findByOrgCode(defaultOrgCode);
            orgCode = defaultOrgCode;
        }
        if(warning!=null){
            List<DqDatasetWarning> list = dqDatasetWarningService.findByOrgCodeAndType(orgCode,"1");
            warning.setDatasetWarningList(list);
        }
        return warning;
    }

    /**
     * 查找资源化预警设置
     * @param orgCode
     */
    public DqPaltformResourceWarning getResourceWarning(String orgCode){

        DqPaltformResourceWarning warning = dqPaltformResourceWarningService.findByOrgCode(orgCode);
        if(warning==null){
            warning = dqPaltformResourceWarningService.findByOrgCode(defaultOrgCode);
        }
        return warning;
    }

    /**
     * 查找上传预警设置
     * @param orgCode
     */
    public DqPaltformUploadWarning getUploadWarning(String orgCode){

        DqPaltformUploadWarning warning = dqPaltformUploadWarningService.findByOrgCode(orgCode);
        if(warning==null){
            warning = dqPaltformUploadWarningService.findByOrgCode(defaultOrgCode);
            orgCode = defaultOrgCode;
        }
        if(warning!=null){
            List<DqDatasetWarning> list = dqDatasetWarningService.findByOrgCodeAndType(orgCode,"2");
            warning.setDatasetWarningList(list);
        }
        return warning;
    }

}
