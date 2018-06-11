package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.DqDatasetWarningService;
import com.yihu.ehr.analyze.service.dataQuality.DqPaltformReceiveWarningService;
import com.yihu.ehr.analyze.service.dataQuality.DqPaltformResourceWarningService;
import com.yihu.ehr.analyze.service.dataQuality.DqPaltformUploadWarningService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import com.yihu.ehr.entity.quality.DqPaltformUploadWarning;
import com.yihu.ehr.model.quality.MDqDatasetWarning;
import com.yihu.ehr.model.quality.MDqPaltformReceiveWarning;
import com.yihu.ehr.model.quality.MDqPaltformResourceWarning;
import com.yihu.ehr.model.quality.MDqPaltformUploadWarning;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
            }
            String sorts = "-updateTime";
            List<DqPaltformReceiveWarning> list = dqPaltformReceiveWarningService.search(null, filters, sorts, page, size);
            pagedResponse(request, response, dqPaltformReceiveWarningService.getCount(filters), page, size);
            return success(convertToModels(list, new ArrayList<>(list.size()), MDqPaltformReceiveWarning.class, null));
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
            pagedResponse(request, response, dqPaltformResourceWarningService.getCount(filters), page, size);
            return success(convertToModels(list, new ArrayList<>(list.size()), MDqPaltformResourceWarning.class, null));
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
            pagedResponse(request, response, dqPaltformUploadWarningService.getCount(filters), page, size);
            return success(convertToModels(list, new ArrayList<>(list.size()), MDqPaltformUploadWarning.class, null));
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
            pagedResponse(request, response, dqDatasetWarningService.getCount(filters), page, size);
            return success(convertToModels(list, new ArrayList<>(list.size()), MDqDatasetWarning.class, null));
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
            @RequestBody String jsonData) throws Exception {
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
            @RequestBody String jsonData) throws Exception {
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
            @RequestBody String jsonData) throws Exception {
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
    @ApiOperation(value = "新增平台接收预警")
    public Envelop paltformReceiveWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData) throws Exception {
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
    @ApiOperation(value = "新增平台资源化预警")
    public Envelop paltformResourceWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData) throws Exception {
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
    @ApiOperation(value = "新增平台上传预警")
    public Envelop paltformUploadWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData) throws Exception {
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
            @RequestBody String jsonData) throws Exception {
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

}
