package com.yihu.ehr.std.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:59
 */
@Controller
@RequestMapping("/std/dataset")
public class DataSetsController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;
//    @RequestMapping("/initial")
//    public String dataSetInitial() {
//        return "/std/dataset/dataSet";
//    }

    @RequestMapping("/initial")
    public String setInitial(Model model) {
        model.addAttribute("contentPage", "std/dataset/set");
        return "pageView";
    }

    @RequestMapping("setupdate")
    public String setUpdate(Model model) {
        model.addAttribute("contentPage", "std/dataset/setUpdate");
        return "generalView";
    }

    @RequestMapping("elementupdate")
    public String elementUpdate(Model model) {
        model.addAttribute("contentPage", "std/dataset/elementUpdate");
        return "generalView";
    }

    /**
     * 数据集版本号查询的方法
     *
     * @return
     */
    @RequestMapping("/searchVersion")
    @ResponseBody
    public Object searchVersion() {
        Envelop result = new Envelop();
        try {
            String url = "/version/allVersions";
            String  _rus = HttpClientUtil.doGet(comUrl+url,username,password);
            //todo:转成MAP
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            CDAVersionModel[] cdaVersionModels = objectMapper.readValue(_rus, CDAVersionModel[].class);
//            List<String> cdaVersionslist = new ArrayList<>();
//            for (CDAVersionModel cdaVersionModel : cdaVersionModels) {
//                cdaVersionslist.add(cdaVersionModel.getVersion());
//            }
//            result.setObj(cdaVersionslist);
            result.setObj(_rus);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
        }
        return result;

        /*Result result = new Result();
        try {
            XCDAVersion[] cdaVersions = xcdaVersionManager.getVersionList();
            List<String> cdaVersionslist = new ArrayList<String>();
            for (XCDAVersion xcdaVersion : cdaVersions) {
                cdaVersionslist.add(xcdaVersion.getVersion());
            }
            result.setObj(cdaVersionslist);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }*/
    }

    /**
     * 查询数据集的方法
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchDataSets")
    @ResponseBody
    public Object searchDataSets(String codename, String version, int page, int rows) {
        Envelop result = new Envelop();
        String strErrMessage = "";
        if (StringUtils.isEmpty(version)) {
            strErrMessage += "请选择版本号!";
        }
        if (!StringUtils.isEmpty(strErrMessage)) {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result;
        }
        String url = "/dataSet/dataSets";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",version);
            params.put("code",codename);
            params.put("name",codename);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDataSetListFailed.toString());
            }else{
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        String strErrMessage = "";
        if (version == null || version == "") {
            strErrMessage += "请选择版本号!";
        }
        if (strErrMessage != "") {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result.toJson();
        }
        cdaVersion.setVersion(version);
        List<DataSetModel> dataSet = null;
        Integer totalCount = null;
        try {
            dataSet = dataSetManager.searchDataSetList(codename, page, rows, cdaVersion);
            totalCount = dataSetManager.searchDataSetInt(cdaVersion);
            if (dataSet == null) {
                result.setSuccessFlg(false);
            } else {
                if (rows == 0)
                    rows = 1;
                result = getResult(dataSet, totalCount, page, rows);
                result.setSuccessFlg(true);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
        }
        return result.toJson();*/
    }

    /**
     * 删除数据集信息的方法
     *
     * @param dataSetId
     * @return
     */
    @RequestMapping("/deleteDataSet")
    @ResponseBody
    public Object deleteDataSet(Long dataSetId, String version) {
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(dataSetId) || dataSetId.equals(0)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("数据集id和版本号不能为空！");
            return result;
        }
        String url = "/dataSet/dataSet";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",version);
            params.put("dataSetId",dataSetId);
            String _msg = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDataSetFailed.toString());
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


        /*Result result = new Result();
        if (dataSetId == null || dataSetId.equals("") || dataSetId.equals(0)) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        try {
            int iResult = metaDataManager.removeMetaDataBySetId(version, dataSetId);
            if (iResult >= 0) {
                iResult = dataSetManager.deleteDataSet(dataSetId, version);
                if (iResult >= 0)
                    result.setSuccessFlg(true);
                else
                    result.setSuccessFlg(false);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();*/
    }

    @RequestMapping(value = "/getDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object getDataSet(Long dataSetId,String versionCode) {
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(dataSetId) || dataSetId.equals(0)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("数据集Id和版本号不能为空！");
            return result;
        }
        String url = "/dataSet/dataSet";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",versionCode);
            params.put("dataSetId",dataSetId);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDataSetFailed.toString());
            }else {
                result.setSuccessFlg(true);
                //TODO 有转化为对象在存储到result中
                result.setObj(_rus);
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(versionCode);
        Result result = new Result();
        if (dataSetId == null || dataSetId.equals("") || dataSetId.equals(0)) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        try {
            XDataSet dataSet = dataSetManager.getDataSet(dataSetId, cdaVersion);
            DataSetModel dataSetModel = dataSetManager.getDataSet(dataSet);
            if (dataSet == null) {
                result.setSuccessFlg(false);
            } else {
                result.setSuccessFlg(true);
                result.setObj(dataSetModel);
            }
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result.toJson();*/
    }

    /**
     * 数据集信息修改的方法
     *
     * @param id
     * @param code
     * @param name
     * @param type
     * @param refStandard
     * @param summary
     * @return
     */
    @RequestMapping("/saveDataSet")
    @ResponseBody
    public Object saveDataSet(long id, String code, String name, String type, String refStandard, String summary, String versionCode) {
        Envelop result = new Envelop();
        String strErrorMsg = "";
        if(StringUtils.isEmpty(code)) {
            strErrorMsg += "代码不能为空！";
        }
        if (StringUtils.isEmpty(name)) {
            strErrorMsg += "名称不能为空！";
        }
        if (StringUtils.isEmpty(type)) {
            strErrorMsg += "标准来源不能为空！";
        }
        if (StringUtils.isEmpty(refStandard)) {
            strErrorMsg += "标准版本不能为空！";
        }
        if (StringUtils.isEmpty(strErrorMsg)) {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrorMsg);
            return result;
        }
        String url = "/dataSet/dataSet";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            params.put("code",code);
            //TODO 提供code不能从复验证api
            params.put("name",name);
            params.put("type",type);
            params.put("refStandard",refStandard);
            params.put("summary",summary);
            params.put("versionCode",versionCode);
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SavedatasetFailed.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(versionCode);
        Result result = new Result();
        String strErrorMsg = "";
        try {
            if (code == null || code == "") {
                strErrorMsg += "代码不能为空！";
            }
            if (name == null || name == "") {
                strErrorMsg += "名称不能为空！";
            }
            if (refStandard == null || refStandard == "") {
                strErrorMsg += "标准来源不能为空！";
            }
            if (cdaVersion == null || cdaVersion.getVersion() == "") {
                strErrorMsg += "标准版本不能为空！";
            }
            XDataSet dataSet = new DataSet();
            //新增时代码不能重复
            if (id == 0) {
                XDataSet dataSetCode = (XDataSet) dataSetManager.getDataSet(code, cdaVersion.getVersion());
                if (dataSetCode != null) {
                    strErrorMsg += "代码不能重复！";
                }
            } else {
                //修改时代码不能重复
                dataSet = (XDataSet) dataSetManager.getDataSet(code, cdaVersion.getVersion());
                if (dataSet != null && dataSet.getId() != id) {
                    strErrorMsg += "代码不能重复！";
                }
                else {
                    dataSet = dataSetManager.getDataSet(id, cdaVersion);
                }
            }
            if (strErrorMsg != "") {
                result.setSuccessFlg(false);
                result.setErrorMsg(strErrorMsg);
                return result.toJson();
            }
             dataSet.setInnerVersion(cdaVersion);
            dataSet.setCode(code);
            dataSet.setName(name);
            dataSet.setReference(refStandard);
            dataSet.setSummary(summary);
            boolean bo = dataSetManager.saveDataSet(dataSet);
            if (bo) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ex.getMessage());
        }
        return result.toJson();*/
    }

    /**
     * 查询数据源的方法
     *
     * @param id
     * @param version
     * @param metaDataCode
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchMetaData")
    @ResponseBody
    public Object searchMetaData(Long id, String version, String metaDataCode, int page, int rows) {
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(id) || id.equals(0) || StringUtils.isEmpty(version)|| version.equals(0)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("数据元id、标准版本不能为空");
            return result;
        }
        String url = "/dataSet/metaDatas";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            params.put("versionCode",version);
            params.put("metaDataCode",metaDataCode);
            params.put("metaDataName",metaDataCode);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SavedatasetFailed.toString());
            }else{
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*List<MetaDataModel> metaDataModel = new ArrayList<>();
        CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        if (id == null || id.equals(0) || id.equals("") || version == null || version.equals(0) || version.equals("")) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        DataSet dataSetModel = new DataSet();
        cdaVersion.setVersion(version);
        dataSetModel.setId(id);
        dataSetModel.setInnerVersion(cdaVersion);
        dataSetModel.setCode(metaDataCode);
        dataSetModel.setName(metaDataCode);
        Integer totalCount = null;
        try {
            metaDataModel = metaDataManager.searchMetaDataList(dataSetModel, page, rows);

            totalCount = metaDataManager.searchDataSetInt(dataSetModel);
            result.setSuccessFlg(true);
            result = getResult(metaDataModel, totalCount, page, rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        return result.toJson();*/
    }

    /**
     * 删除数据元的方法
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteMetaData")
    @ResponseBody
    public Object deleteMetaData(String ids, String version) {
        Envelop result = new Envelop();
        String strErrMessage = "";
        if (StringUtils.isEmpty(ids)) {
            strErrMessage += "请选择数据元!";
        }
        if (StringUtils.isEmpty(version)) {
            strErrMessage += "请选择标准版本!";
        }
        if (!strErrMessage.equals("")) {
            result.setErrorMsg(strErrMessage);
            result.setSuccessFlg(false);
            return result;
        }
        try {
            String url = "/dataSet/metaData";
            Map<String,Object> params = new HashMap<>();
            params.put("ids",ids);// TODO api参数为Long ids  ---批量删除传递过来的是ids字符串
            params.put("versionCode",version);
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteMetaDataFailed.toString());
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        String strErrMessage = "";
        if (ids == null || ids.equals("")) {
            strErrMessage += "请选择数据元!";
        }
        if (version == null || version.equals("")) {
            strErrMessage += "请选择标准版本!";
        }
        if (!strErrMessage.equals("")) {
            result.setErrorMsg(strErrMessage);
            result.setSuccessFlg(false);
            return result.toJson();
        }
        try {
            int res = metaDataManager.deleteMetaDatas(ids, version);
            if (res > -1) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
            return result.toJson();
        }
        return result.toJson();*/
    }

    @RequestMapping(value = "/getMetaData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object getMetaData(Long dataSetId, Long metaDataId, String version) {
        Envelop result = new Envelop();
        String strErrMessage = "";
        if (StringUtils.isEmpty(dataSetId) || dataSetId.equals(0)) {
            strErrMessage += "请先选择数据集!";
        }
        if (StringUtils.isEmpty(metaDataId) || metaDataId.equals(0)) {
            strErrMessage += "请先选择数据元!";
        }
        if (StringUtils.isEmpty(version) || version.equals(0)) {
            strErrMessage += "请先选择标准版本!";
        }
        if (strErrMessage != "") {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result;
        }
        try {
            String url = "/dataSet/getMetaData";
            Map<String,Object> params = new HashMap<>();
            params.put("dataSetId",dataSetId);
            params.put("metaDataId",metaDataId);
            params.put("versionCode",version);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetMetaDataFailed.toString());
            }else {
                //TODO 有转化成为对象在存入result中
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                XMetaDataForInterface metaData = objectMapper.readValue(_rus,XMetaDataForInterface.class);
//                List<XMetaDataForInterface> elementList = new ArrayList<>();
//                elementList.add(metaData);
                result.setSuccessFlg(true);
                result.setObj(_rus);
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            return result;
        }
        return result;


        /*CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        String strErrMessage = "";
        if (dataSetId == null || dataSetId.equals(0) || dataSetId.equals("")) {
            strErrMessage += "请先选择数据集!";
        }
        if (metaDataId == null || metaDataId.equals(0) || metaDataId.equals("")) {
            strErrMessage += "请先选择数据元!";
        }
        if (version == null || version.equals(0) || version.equals("")) {
            strErrMessage += "请先选择标准版本!";
        }
        if (strErrMessage != "") {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result.toJson();
        }
        DataSet dataSet = new DataSet();
        dataSet.setId(dataSetId);
        cdaVersion.setVersion(version);
        dataSet.setInnerVersion(cdaVersion);
        List<XMetaDataForInterface> elementList = null;
        try {
            List<XMetaData> metaDataList = metaDataManager.getMetaDatas(dataSet, metaDataId);
            if (metaDataList.size() > 0) {
                elementList = new ArrayList<>();
                for (int i = 0; i < metaDataList.size(); i++) {
                    XMetaData metaData = (XMetaData) metaDataList.get(i);
                    XMetaDataForInterface info = new MetaDataForInterface();
                    XMetaDataMapping xMetaDataMapping = (XMetaDataMapping) metaData;
                    info.setDatasetId(String.valueOf(metaData.getDataSetId()));
                    info.setCode(metaData.getCode());
                    info.setInnerCode(metaData.getInnerCode());
                    info.setName(metaData.getName());
                    info.setType(metaData.getType());
                    info.setFormatType(metaData.getFormat());
                    info.setDefinition(metaData.getDefinition());
                    info.setNullable(xMetaDataMapping.isNullable() ? "1" : "0");
                    info.setColumnType(xMetaDataMapping.getColumnType());
                    info.setColumnName(xMetaDataMapping.getColumnName());
                    info.setColumnLength(xMetaDataMapping.getColumnLength());
                    info.setPrimaryKey(xMetaDataMapping.isPrimaryKey() ? "1" : "0");
                    info.setHashCode(String.valueOf(metaData.getHashCode()));
                    info.setId(String.valueOf(metaData.getId()));
                    info.setDictId(String.valueOf(metaData.getDictId()));
                    info.setDictName(metaData.getDictName());
                    elementList.add(info);
                }
                result.setSuccessFlg(true);
                result.setDetailModelList(elementList);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            return result.toJson();
        }
        return result.toJson();*/

    }

    @RequestMapping(value = "/updataMetaSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updataMetaSet(String info) {
        Envelop result = new Envelop();
        try {
            String strErrMessage = "";
            //todo: 前台js不能为空判断
//            if (StringUtil.isEmpty(info.getVersion()) || info.getVersion().equals(0)) {
//                strErrMessage += "请先选择标准版本!";
//            }
//            if (StringUtil.isEmpty(info.getDatasetId())|| info.getDatasetId().equals(0)) {
//                strErrMessage += "请先选择数据集!";
//            }
//            if (StringUtil.isEmpty(info.getCode())|| info.getCode().equals(0)) {
//                strErrMessage += "代码不能为空!";
//            }
//            if (StringUtil.isEmpty(info.getName())|| info.getName().equals(0)) {
//                strErrMessage += "名称不能为空!";
//            }
//            if (StringUtil.isEmpty(info.getInnerCode()) || info.getInnerCode().equals(0)) {
//                strErrMessage += "内部代码不能为空!";
//            }
//            if (StringUtil.isEmpty(info.getColumnName())|| info.getColumnName().equals(0)) {
//                strErrMessage += "字段名称不能为空!";
//            }
            if (!strErrMessage.equals("")) {
                result.setErrorMsg(strErrMessage);
                result.setSuccessFlg(false);
                return result;
            }
            String url = "/dataSet/updataMetaSet";
            // TODO 有writeValueAsString(info);操作
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            String _MetaDataForInterface = objectMapper.writeValueAsString(info);
            Map<String,Object> params = new HashMap<>();
            params.put("metaDataJson",info);
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if (StringUtils.isEmpty(_rus)) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SaveMetaDataFailed.toString());
            } else {
                result.setSuccessFlg(true);
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        try {
            String strErrMessage = "";
            if (info.getVersion() == null || info.getVersion().equals(0) || info.getVersion().equals("")) {
                strErrMessage += "请先选择标准版本!";
            }
            if (info.getDatasetId() == null || info.getDatasetId().equals(0) || info.getDatasetId().equals("")) {
                strErrMessage += "请先选择数据集!";
            }
            if (info.getCode() == null || info.getCode().equals(0) || info.getCode().equals("")) {
                strErrMessage += "代码不能为空!";
            }
            if (info.getName() == null || info.getName().equals(0) || info.getName().equals("")) {
                strErrMessage += "名称不能为空!";
            }
            if (info.getInnerCode() == null || info.getInnerCode().equals(0) || info.getInnerCode().equals("")) {
                strErrMessage += "内部代码不能为空!";
            }
            if (info.getColumnName() == null || info.getColumnName().equals(0) || info.getColumnName().equals("")) {
                strErrMessage += "字段名称不能为空!";
            }
            if (info.getId().equals("0")) {
                if (metaDataManager.getCountByCode(info.getVersion(), info.getInnerCode(), info.getDatasetId()) > 0) {
                    strErrMessage += "代码不能重复!";
                }
                if (metaDataManager.getCountByColumnName(info.getVersion(), info.getColumnName(), info.getDatasetId()) > 0) {
                    strErrMessage += "字段名不能重复!";
                }
            }else {
                //if ()
            }
            if (!strErrMessage.equals("")) {
                result.setErrorMsg(strErrMessage);
                result.setSuccessFlg(false);
                return result.toJson();
            }
            DataSet dataSetModel = new DataSet();
            MetaData metaData = new MetaData();
            cdaVersion.setVersion(info.getVersion());
            dataSetModel.setInnerVersion(cdaVersion);
            dataSetModel.setId(Long.parseLong(info.getDatasetId()));
            metaData.setId(Long.parseLong(info.getId()));
            metaData.setDataSet(dataSetModel);
            metaData.setCode(info.getCode());
            metaData.setName(info.getName());
            metaData.setInnerCode(info.getInnerCode());
            metaData.setType(info.getType());
            metaData.setDictId(info.getDictId() == "" ? 0 : Long.parseLong(info.getDictId()));
            metaData.setFormat(info.getFormatType());
            metaData.setDefinition(info.getDefinition());
            metaData.setColumnName(info.getColumnName());
            metaData.setColumnLength(info.getColumnLength());
            metaData.setColumnType(info.getColumnType());
            if (info.getPrimaryKey().equals("1")) {
                metaData.setPrimaryKey(true);
            } else {
                metaData.setPrimaryKey(false);
            }
            if (info.getNullable().equals("1")) {
                metaData.setNullable(true);
            } else {
                metaData.setNullable(false);
            }
            int res = metaDataManager.saveMetaData(dataSetModel, metaData);
            if (res > 0) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
        }
        return result.toJson();*/
    }

    /**
     * 标准来源下拉框的查询
     *
     * @param version
     * @return
     */
    @RequestMapping("getStdSourceList")
    @ResponseBody
    public Object getStdSourceList(String version) {
        Envelop result = new Envelop();
        String strJson = "";
        try {
            if (StringUtils.isEmpty(version)) {
                result.setSuccessFlg(false);
                result.setErrorMsg("版本不能为空！");
                return result;
            }
            String url = "/stdSource/standardSources";
            Map<String,Object> params = new HashMap<>();
            params.put("",version);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if (StringUtils.isEmpty(_rus)) {
                result.setSuccessFlg(false);
                result.setErrorMsg("查找不到标准来源！");
                return result;
            }
            //TODO 有 writeValueAsString(resultInfos);操作
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            List<StandardSourceForInterface> resultInfos = Arrays.asList(objectMapper.readValue(_rus,StandardSourceForInterface[].class));
//            strJson = objectMapper.writeValueAsString(resultInfos);
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg("获取标准来源失败！");
            return result;
        }
        return result;
        /*Result result = new Result();
        String strJson = "";
        try {
            if (version == null || version == "") {
                return missParameter("version_code");
            }
            XStandardSource[] xStandardSources = xStandardSourceManager.getSourceList();
            if (xStandardSources == null) {
                return failed(ErrorCode.GetStandardSourceFailed);
            }
            List<StandardSourceForInterface> resultInfos = GetStandardSourceForInterface(xStandardSources);
            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            strJson = objectMapper.writeValueAsString(resultInfos);
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            return failed(ErrorCode.GetStandardSourceFailed);
        }
        RestEcho echo = new RestEcho().success();
        echo.putResultToList(strJson);
        return echo;*/
    }


   /* public List<StandardSourceForInterface> GetStandardSourceForInterface(XStandardSource[] xStandardSources) {
        List<StandardSourceForInterface> results = new ArrayList<>();
        for (XStandardSource xStandardSource : xStandardSources) {
            StandardSourceForInterface info = new StandardSourceForInterface();
            info.setId(xStandardSource.getId());
            info.setCode(xStandardSource.getCode());
            info.setName(xStandardSource.getName());
            info.setSourceType(xStandardSource.getSourceType().getCode());
            info.setDescription(xStandardSource.getDescription());
            results.add(info);
        }

        return results;
    }
*/

    /**
     * 检验字典查询的方法
     *
     * @param version
     * @param key
     * @return
     */
    @RequestMapping(value = "/getMetaDataDict", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getMetaDataDict(String version, String key) {
        String strResult = "[]";
        try {
            //TODO 无对应
            String url = "/******";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",version);
            params.put("code",key);
            params.put("name",key);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if (!StringUtils.isEmpty(_rus)) {
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                List<XDictForInterface> xDictForInterfaces = Arrays.asList(objectMapper.readValue(_rus,XDictForInterface.class));
//                strResult = objectMapper.writeValueAsString(xDictForInterfaces);
                return _rus;
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
        }
        return strResult;

        /*CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(version);
        String strResult = "[]";
        XDict[] xDicts = null;
        try {
            xDicts = dictManager.getDictLists(cdaVersion, key);
            if (xDicts != null) {
                XDictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (XDict xDict : xDicts) {
                    XDictForInterface info = new DictForInterface();
                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersionId());
                    infos[i] = info;
                    i++;
                }
                List<XDictForInterface> xDictForInterfaces = Arrays.asList(infos);
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strResult = objectMapper.writeValueAsString(xDictForInterfaces);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
        }
        return strResult;*/
    }

    @RequestMapping("/validatorMetadata")
    @ResponseBody
    public Object validatorMetadata(String version, String datasetId, String searchNm, String metaDataCodeMsg) {
        Envelop result = new Envelop();
        String url ="/dataSet/validatorMetadata";
        String _msg = "";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",version);
            params.put("datasetId",datasetId);
            params.put("searchNm",searchNm);
            params.put("metaDataCodeMsg",metaDataCodeMsg);
            _msg = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg("验证失败");
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


       /* if (metaDataCodeMsg.equals("metaDataCodeMsg")) {
            if (metaDataManager.getCountByCode(version, searchNm, datasetId) > 0) {
                return getSuccessResult(false).toJson();
            }
        }
        if (metaDataCodeMsg.equals("fieldNameMsg")) {
            if (metaDataManager.getCountByColumnName(version, searchNm, datasetId) > 0) {
                return getSuccessResult(false).toJson();
            }
        }
        return getSuccessResult(true).toJson();*/
    }

    /**
     * 将CDA归属的数据集信息转换的XML信息
     * @param setId 数据集ID
     * @param versionCode 版本号
     * @return xml信息*/
    @RequestMapping("/getXMLInfoByDataSetId")
    @ResponseBody
    public Object getXMLInfoByDataSetId(String setId, String versionCode) {
        //TODO 没有对应
        Envelop result = new Envelop();
        try {
            String url = "/dataSet/*******";
            Map<String,Object> params = new  HashMap<>();
            params.put("setId",setId);
            params.put("versionCode",versionCode);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据获取失败！");
            }else{
                result.setSuccessFlg(true);
                result.setObj(_rus);
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        String strResult = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>";
        try {
            List<String> listIds = Arrays.asList(setId.split(","));
            XDataSet[] xDataSets = dataSetManager.getDataSetByIds(listIds, versionCode);
            if (xDataSets.length > 0) {
                for (int i = 0; i < xDataSets.length; i++) {
                    String strCode = xDataSets[i].getCode();
                    strResult += "<" + strCode + ">";
                    List<XMetaData> xMetaDatas = xDataSets[i].getMetaDataList();
                    if (xMetaDatas.size() > 0) {
                        for (int j = 0; j < xMetaDatas.size(); j++) {
                            String strColumnName = xMetaDatas.get(j).getColumnName();
                            strResult += "<" + strColumnName + "></" + strColumnName + ">";
                        }
                    }
                    strResult += "</" + strCode + ">";
                }
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
        }
        strResult += "</root>";
        //  result=result.replaceAll("<","&lt;").replaceAll(">","&gt;");
        result.setSuccessFlg(true);
        result.setObj(strResult);
        return result;*/
    }

//    public void exportToExcel(){
//        //todo：test 导出测试
//        List<String> ids = new ArrayList<>();
//        ids.add("1");
//        ids.add("2");
//        ids.add("3");
//        XDataSet[] dataSets = dataSetManager.getDataSetByIds(ids, xcdaVersionManager.getLatestVersion().getVersion());
//        dataSetManager.exportToExcel("E:/workspaces/excel/testExport.xls",dataSets);
//    }
//
//    public void importFromExcel(){
//        //todo：test导入测试
//        dataSetManager.importFromExcel("E:/workspaces/excel/测试excel导入.xls", xcdaVersionManager.getLatestVersion());
//    }
}