package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.feign.StandardServiceClient;
import com.yihu.ehr.analyze.service.dataQuality.*;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import com.yihu.ehr.entity.quality.DqPaltformUploadWarning;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.model.quality.MDqDatasetWarning;
import com.yihu.ehr.model.quality.MDqPaltformReceiveWarning;
import com.yihu.ehr.model.quality.MDqPaltformResourceWarning;
import com.yihu.ehr.model.quality.MDqPaltformUploadWarning;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.hos.model.standard.MStdDataSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yeshijie on 2018/5/28.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WarningSettingEndPoint", description = "质控-预警设置", tags = {"档案分析服务-质控-预警设置"})
public class WarningSettingEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private DqPaltformReceiveWarningService dqPaltformReceiveWarningService;
    @Autowired
    private DqPaltformResourceWarningService dqPaltformResourceWarningService;
    @Autowired
    private DqPaltformUploadWarningService dqPaltformUploadWarningService;
    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.version}")
    private String defaultQualityVersion;
    @Autowired
    private StandardServiceClient standardServiceClient;

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台接收预警列表")
    public Envelop paltformReceiveWarningList(
            @ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try {
            String filters = null;
            if(StringUtils.isNotBlank(orgCode)){
                filters = "orgCode="+orgCode;
            }else {
                filters = "orgCode<>"+defaultOrgCode;
            }
            String sorts = "-updateTime";
            List<DqPaltformReceiveWarning> list = dqPaltformReceiveWarningService.search(null, filters, sorts, page, size);
            List<MDqPaltformReceiveWarning> warnings = (List<MDqPaltformReceiveWarning>)convertToModels(list, new ArrayList<>(list.size()), MDqPaltformReceiveWarning.class, null);
            return getPageResult(warnings, (int)dqPaltformReceiveWarningService.getCount(filters), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台资源化预警列表")
    public Envelop paltformResourceWarningList(
            @ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try {
            String filters = null;
            if(StringUtils.isNotBlank(orgCode)){
                filters = "orgCode="+orgCode;
            }
            String sorts = "-updateTime";
            List<DqPaltformResourceWarning> list = dqPaltformResourceWarningService.search(null, filters, sorts, page, size);
            List<MDqPaltformResourceWarning> warnings = (List<MDqPaltformResourceWarning>)convertToModels(list, new ArrayList<>(list.size()), MDqPaltformResourceWarning.class, null);
            return getPageResult(warnings, (int)dqPaltformResourceWarningService.getCount(filters), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台上传预警列表")
    public Envelop paltformUploadWarningList(
            @ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try {
            String filters = null;
            if(StringUtils.isNotBlank(orgCode)){
                filters = "orgCode="+orgCode;
            }
            String sorts = "-updateTime";
            List<DqPaltformUploadWarning> list = dqPaltformUploadWarningService.search(null, filters, sorts, page, size);
            List<MDqPaltformUploadWarning> warnings = (List<MDqPaltformUploadWarning>)convertToModels(list, new ArrayList<>(list.size()), MDqPaltformUploadWarning.class, null);
            return getPageResult(warnings, (int)dqPaltformUploadWarningService.getCount(filters), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningDefault, method = RequestMethod.GET)
    @ApiOperation(value = "查找默认的平台接收预警")
    public Envelop paltformReceiveWarningDefault() {
        Envelop envelop = new Envelop();
        try {
            DqPaltformReceiveWarning warning =  dqPaltformReceiveWarningService.findByOrgCode(defaultOrgCode);
            List<DqDatasetWarning> warningList = dqDatasetWarningService.findByOrgCodeAndType(warning.getOrgCode(),"1");
            warning.setDatasetWarningNum(warningList.size());
            warning.setDatasetWarningList(warningList);
            return success(convertToModel(warning, MDqPaltformReceiveWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台接收预警")
    public Envelop getMDqPaltformReceiveWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id) {
        Envelop envelop = new Envelop();
        try {
            DqPaltformReceiveWarning warning =  dqPaltformReceiveWarningService.findById(id);
            List<DqDatasetWarning> warningList = dqDatasetWarningService.findByOrgCodeAndType(warning.getOrgCode(),"1");
            warning.setDatasetWarningNum(warningList.size());
            return success(convertToModel(warning, MDqPaltformReceiveWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台资源化预警")
    public Envelop getMDqPaltformResourceWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id) {
        Envelop envelop = new Envelop();
        try {
            DqPaltformResourceWarning warning =  dqPaltformResourceWarningService.findById(id);
            return success(convertToModel(warning, MDqPaltformResourceWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台上传预警")
    public Envelop getMDqPaltformUploadWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id) {
        Envelop envelop = new Envelop();
        try {
            DqPaltformUploadWarning warning =  dqPaltformUploadWarningService.findById(id);
            List<DqDatasetWarning> warningList = dqDatasetWarningService.findByOrgCodeAndType(warning.getOrgCode(),"2");
            warning.setDatasetWarningNum(warningList.size());
            return success(convertToModel(warning, MDqPaltformUploadWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.DatasetWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "预警数据集列表")
    public Envelop datasetWarningList(
            @ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = true) String orgCode,
            @ApiParam(name = "type", value = "类型(1平台接收，2平台上传)", defaultValue = "1")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try{
            String filters = "orgCode="+orgCode+";type="+type;
            String sorts = "-datasetCode";
            List<DqDatasetWarning> list = dqDatasetWarningService.search(null, filters, sorts, page, size);
            List<MDqDatasetWarning> warnings = (List<MDqDatasetWarning>)convertToModels(list, new ArrayList<>(list.size()), MDqDatasetWarning.class, null);
            return getPageResult(warnings,(int)dqDatasetWarningService.getCount(filters), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;

    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台接收预警")
    public Envelop paltformReceiveWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformReceiveWarning warning = toEntity(jsonData, DqPaltformReceiveWarning.class);
            warning = dqPaltformReceiveWarningService.paltformReceiveWarningAdd(warning);
            return success(convertToModel(warning, MDqPaltformReceiveWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台资源化预警")
    public Envelop paltformResourceWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformResourceWarning warning = toEntity(jsonData, DqPaltformResourceWarning.class);
            warning = dqPaltformResourceWarningService.paltformResourceWarningAdd(warning);
            return success(convertToModel(warning, MDqPaltformResourceWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台上传预警")
    public Envelop paltformUploadWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformUploadWarning warning = toEntity(jsonData, DqPaltformUploadWarning.class);
            warning = dqPaltformUploadWarningService.paltformUploadWarningAdd(warning);
            return success(convertToModel(warning, MDqPaltformUploadWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台接收预警")
    public Envelop paltformReceiveWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam Long id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            dqPaltformReceiveWarningService.delete(id);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台资源化预警")
    public Envelop paltformResourceWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam Long id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            dqPaltformResourceWarningService.delete(id);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台上传预警")
    public Envelop paltformUploadWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam Long id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            dqPaltformUploadWarningService.delete(id);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;

    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "修改平台接收预警")
    public Envelop paltformReceiveWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformReceiveWarning warning = toEntity(jsonData, DqPaltformReceiveWarning.class);
            warning = dqPaltformReceiveWarningService.paltformReceiveWarningUpd(warning);
            return success(convertToModel(warning, MDqPaltformReceiveWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "修改平台资源化预警")
    public Envelop paltformResourceWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformResourceWarning warning = toEntity(jsonData, DqPaltformResourceWarning.class);
            warning = dqPaltformResourceWarningService.paltformResourceWarningUpd(warning);
            return success(convertToModel(warning, MDqPaltformResourceWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "修改平台上传预警")
    public Envelop paltformUploadWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqPaltformUploadWarning warning = toEntity(jsonData, DqPaltformUploadWarning.class);
            warning = dqPaltformUploadWarningService.paltformUploadWarningUpd(warning);
            return success(convertToModel(warning, MDqPaltformUploadWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.DatasetWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集")
    public Envelop datasetWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestParam(value = "jsonData", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try {
            DqDatasetWarning warning = toEntity(jsonData, DqDatasetWarning.class);
            warning = dqDatasetWarningService.save(warning);
            return success(convertToModel(warning, MDqDatasetWarning.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.DatasetWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除数据集")
    public Envelop datasetWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam Long id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            dqDatasetWarningService.delete(id);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.DatasetList, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集")
    public Envelop datasetList(
            @ApiParam(name = "orgCode", value = "机构code（平台上传时可不传）", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "code", value = "数据集编码", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "数据集名称", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = true) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = true) int page) {
        Envelop envelop = new Envelop();
        try {
            String filters = "version="+defaultQualityVersion;
            if(StringUtils.isNotBlank(name)){
                filters+=";name?"+name;
            }
            if(StringUtils.isNotBlank(code)){
                filters+=";code?"+code;
            }
            ResponseEntity<List<MStdDataSet>> res = standardServiceClient.searchDataSets(null,filters,"-code",size,page,defaultQualityVersion);
            List<MStdDataSet> mStdDataSetList = res.getBody();
            List<MDataSet> mDataSets = new ArrayList<>(mStdDataSetList.size());
            mStdDataSetList.forEach(item->{
                MDataSet set = new MDataSet();
                set.setName(item.getName());
                set.setCode(item.getCode());
            });
            int totalCount = getTotalCount(res);
            return getPageResult(mDataSets,totalCount,page,size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.ImportDatasetExcel, method = RequestMethod.POST)
    @ApiOperation(value = "数据集导入")
    public void importDatasetExcel(MultipartFile file,
            @ApiParam(name = "orgCode", value = "机构code（平台上传时可不传）", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类型（1平台接收，2平台上传）", defaultValue = "")
            @RequestParam(value = "type", required = true) String type) {

        try {
            Workbook wb = Workbook.getWorkbook(file.getInputStream());
            Sheet[] sheets = wb.getSheets();
            Sheet sheet = sheets[0];
            int rows = sheet.getRows();
            List<String> codeList = new ArrayList<>(rows);
            if(rows>1){
                for(int row = 1;row<rows;row++){
                    String code = sheet.getCell(0, row).getContents();
                    if(StringUtils.isNotBlank(code)){
                        codeList.add(code);
                    }
                }
            }
            dqDatasetWarningService.importDatasetExcel(codeList,type,orgCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
