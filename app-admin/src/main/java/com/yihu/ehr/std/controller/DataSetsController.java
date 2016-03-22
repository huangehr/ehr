package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:59
 */
@Controller
@RequestMapping("/std/dataset")
public class DataSetsController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

//    @RequestMapping("/initial")
//    public String dataSetInitial() {
//        return "/std/dataset/dataSet";
//    }
    @Autowired
    ObjectMapper objectMapper ;

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
            //todo:ת��MAP
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
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        if (StringUtils.isEmpty(version)) {
            envelop.setErrorMsg("请选择版本号！");
            return envelop;
        }
        String filters = "";
        if (!StringUtils.isEmpty(codename)){
            //filters += "code?"+codename+" g1;name?"+codename+" g1;";
            filters += "name?%"+codename+"%";

        }
        String url = "/std/data_sets";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters",filters);
            params.put("sorts","");
            params.put("page",page);
            params.put("size",rows);
            params.put("version",version);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return envelopStr;
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;

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
            result.setErrorMsg("数据集id和版本号不能为空!");
            return result;
        }
        String url = "/std/data_set";
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
            result.setErrorMsg("数据集Id和版本号不能为空!");
            return result;
        }
        String url = "/std/data_set";
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
                //TODO 要转化为对象在存储到result中
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
        Envelop envelop = new Envelop();
        String strErrorMsg = "";
        if(StringUtils.isEmpty(code)) {
            strErrorMsg += "代码不能为空!";
        }
        if (StringUtils.isEmpty(name)) {
            strErrorMsg += "名称不能为空!";
        }
        if (StringUtils.isEmpty(type)) {
            strErrorMsg += "标准来源不能为空!";
        }
        if (StringUtils.isEmpty(refStandard)) {
            strErrorMsg += "标准版本不能为空!";
        }
        if (StringUtils.isEmpty(strErrorMsg)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(strErrorMsg);
            return envelop;
        }
        String url = "/std/data_set";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("version_code",versionCode);
            //新增数据集
            if(id == 0){
                DataSetModel modelNew = new DataSetModel();
                modelNew.setCode(code);
                modelNew.setName(name);
                modelNew.setReference(refStandard);
                modelNew.setSummary(summary);
                //TODO 数库保存的是版本名称，待查询
                modelNew.setStdVersion(versionCode);
                String jsonDataNew = objectMapper.writeValueAsString(modelNew);
                params.put("json_data",jsonDataNew);
                String envelopStrNew = HttpClientUtil.doPost(comUrl+url,params,username,password);
                return envelopStrNew;
            }

            //修改数据集
            //获取原数据集对象
            String urlGet = "std/data_set";
            Map<String,Object> args = new HashMap<>();
            args.put("id",id);
            args.put("version",versionCode);
            String envelopStrGet = HttpClientUtil.doGet(comUrl+urlGet,params,username,password);
            Envelop envelopGet = objectMapper.readValue(envelopStrGet,Envelop.class);
            if (!envelopGet.isSuccessFlg()){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("获取标准数据集失败！");
                return envelop;
            }
            DataSetModel modelForUpdate = getEnvelopModel(envelopGet.getObj(),DataSetModel.class);
            modelForUpdate.setCode(code);
            modelForUpdate.setName(name);
            modelForUpdate.setReference(refStandard);
            modelForUpdate.setSummary(summary);
            String dataJsonUpdate = objectMapper.writeValueAsString(modelForUpdate);
            params.put("json_data",dataJsonUpdate);
            String envelopStrUpdate = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return envelopStrUpdate;
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;

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
            result.setErrorMsg("数据元id、标准版本不能为空!");
            return result;
        }
        String url = "/std/meta_datas";
        String filters = "dataSetId="+id+";";
        if(!StringUtils.isEmpty(metaDataCode)){
            //TODO
//            filters += "code?"+metaDataCode+" g1;name?"+metaDataCode+" g1;";
        }
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters",filters);
            params.put("sorts","");
            params.put("page",page);
            params.put("size",rows);
            params.put("version",version);
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
            String url = "/std/meta_data";
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
        Envelop envelop = new Envelop();
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
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(strErrMessage);
            return envelop;
        }
        try {
            String url = "/std/meta_datas";
            Map<String,Object> params = new HashMap<>();
            params.put("metaDataId",metaDataId);
            params.put("versionCode",version);
            String envulopStr = HttpClientUtil.doGet(comUrl+url,params,username,password);
            Envelop envelopGet = objectMapper.readValue(envulopStr,Envelop.class);
            if(envelopGet.isSuccessFlg()){
                MetaDataModel metaDataModel = getEnvelopModel(envelopGet.getObj(),MetaDataModel.class);
                List<MetaDataModel> list = new ArrayList<>();
                list.add(metaDataModel);
                envelop.setSuccessFlg(true);
                envelop.setDetailModelList(list);
                String _rus = objectMapper.writeValueAsString(envelop);
                return _rus;
            }
            return envulopStr;
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @RequestMapping(value = "/updataMetaSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updataMetaSet(String info,String version) {
        //TODO 新增、合二为一
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MetaDataModel model = objectMapper.readValue(info,MetaDataModel.class);
            if(StringUtils.isEmpty(version)){
                envelop.setErrorMsg("标准版本号不能为空！");
                return envelop;
            }
            if(model.getDataSetId() == 0){
                envelop.setErrorMsg("数据集id不能为空！");
                return envelop;
            }
            if(StringUtils.isEmpty(model.getCode())){
                envelop.setErrorMsg("数据元编码不能为空！");
                return envelop;
            }
            if(StringUtils.isEmpty(model.getName())){
                envelop.setErrorMsg("数据元名称不能为空！");
                return envelop;
            }
            if(StringUtils.isEmpty(model.getInnerCode())){
                envelop.setErrorMsg("数据元内部编码不能为空！");
                return envelop;
            }
            if(StringUtils.isEmpty(model.getColumnName())){
                envelop.setErrorMsg("数据元对应列名不能为空！");
                return envelop;
            }

            String url = "/std/meta_data";
            Map<String,Object> params = new HashMap<>();
            params.put("version",version);

            //新增数据元操作
            if(model.getId() ==0){
                params.put("metaDataJson",info);
                String envelopStrNew = HttpClientUtil.doPost(comUrl+url,params,username,password);
                return envelopStrNew;
            }

            //修改数据元操作
            //获取原数据元对象
            String urlGet = "/std/meta_data";
            Map<String,Object> args = new HashMap<>();
            args.put("versionCode",version);
            args.put("metaDataId",model.getId());
            String envelopStrGet = HttpClientUtil.doGet(comUrl+urlGet,params,username,password);
            Envelop envelopGet = objectMapper.readValue(envelopStrGet,Envelop.class);
            if(!envelopGet.isSuccessFlg()){
                envelop.setErrorMsg("数据元不存在！");
                return envelop;
            }
            MetaDataModel modelForUpdate = getEnvelopModel(envelop.getObj(),MetaDataModel.class);

            //设置修改值
            modelForUpdate.setCode(model.getCode());
            modelForUpdate.setName(model.getName());
            modelForUpdate.setInnerCode(model.getInnerCode());
            modelForUpdate.setType(model.getType());
            modelForUpdate.setDictId(model.getDictId());
            modelForUpdate.setFormat(model.getFormat());
            modelForUpdate.setDefinition(model.getDefinition());
            modelForUpdate.setColumnName(model.getColumnName());
            modelForUpdate.setColumnLength(model.getColumnLength());
            modelForUpdate.setColumnType(model.getColumnType());
            modelForUpdate.setNullable(model.isNullable());
            modelForUpdate.setPrimaryKey(model.isPrimaryKey());
            String metaDataJson = objectMapper.writeValueAsString(modelForUpdate);
            params.put("metaDataJson",metaDataJson);
            String envelopStrUpdate = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return envelopStrUpdate;
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
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
        //TODO 页面调用的是标准字典Controller的方法

        //临时测试用方法
        Envelop envelop = new Envelop();
        String url = "/standard_source/sources";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters","");
            params.put("sorts","");
            params.put("size",100);
            params.put("page",1);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(envelopStr)){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            }else{
                return envelopStr;
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
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
        String url ="/std/dataSet/validatorMetadata";
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
        //TODO 无对应
        Envelop result = new Envelop();
        try {
            String url = "/std/dataSet/*******";
            Map<String,Object> params = new  HashMap<>();
            params.put("setId",setId);
            params.put("versionCode",versionCode);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据获取失败!");
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
//        dataSetManager.importFromExcel("E:/workspaces/excel/����excel����.xls", xcdaVersionManager.getLatestVersion());
//    }
}