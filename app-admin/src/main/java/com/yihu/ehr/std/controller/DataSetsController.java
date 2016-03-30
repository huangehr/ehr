package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
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

import java.util.*;


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
    @Value("${service-gateway.stdurl}")
    private String comUrl;
    @Value("${service-gateway.url}")
    private String adminUrl;

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
     * 数据集分页查询
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
            filters += "name?"+codename+" g1;code?"+codename+" g1;";

        }
        String url = "/data_sets";
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
    }

    @RequestMapping("/deleteDataSet")
    @ResponseBody
    public Object deleteDataSet(Long dataSetId, String version) {
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(dataSetId) || dataSetId.equals(0)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("数据集id和版本号不能为空!");
            return envelop;
        }
        String url = "/data_set/"+dataSetId;
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("version",version);
            params.put("id",dataSetId);
            String envelopStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            return envelopStr;
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping(value = "/getDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object getDataSet(Long dataSetId,String versionCode) {
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(dataSetId) || dataSetId.equals(0)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("数据集Id和版本号不能为空!");
            return envelop;
        }
        String url = "/data_set/"+dataSetId;
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("version",versionCode);
            params.put("id",dataSetId);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return envelopStr;
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping("/saveDataSet")
    @ResponseBody
    public Object saveDataSet(long id, String code, String name, String type, String refStandard, String summary, String versionCode) {
        //新增、修改数据集
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
        String url = "/data_set";
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
            String urlGet = "/data_set/"+id;
            Map<String,Object> args = new HashMap<>();
            args.put("id",id);
            args.put("version",versionCode);
            String envelopStrGet = HttpClientUtil.doGet(comUrl+urlGet,args,username,password);
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
    }

    @RequestMapping("/searchMetaData")
    @ResponseBody
    public Object searchMetaData(Long id, String version, String metaDataCode, int page, int rows) {
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(id) || id.equals(0) || StringUtils.isEmpty(version)|| version.equals(0)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("数据元id、标准版本不能为空!");
            return result;
        }
        String url = "/meta_datas";
        String filters = "dataSetId="+id+";";
        if(!StringUtils.isEmpty(metaDataCode)){
            filters += "innerCode?"+metaDataCode+" g1;name?"+metaDataCode+" g1;";
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
    }

    @RequestMapping("/deleteMetaData")
    @ResponseBody
    public Object deleteMetaData(String ids, String version) {
        Envelop envelop = new Envelop();
        String strErrMessage = "";
        if (StringUtils.isEmpty(ids)) {
            strErrMessage += "请选择数据元!";
        }
        if (StringUtils.isEmpty(version)) {
            strErrMessage += "请选择标准版本!";
        }
        if (!strErrMessage.equals("")) {
            envelop.setErrorMsg(strErrMessage);
            envelop.setSuccessFlg(false);
            return envelop;
        }
        try {
            String url = "/meta_data";
            Map<String,Object> params = new HashMap<>();
            params.put("ids",ids);
            params.put("version_code",version);
            String envelopStr = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
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
            String url = "/meta_data";
            Map<String,Object> params = new HashMap<>();
            params.put("metaDataId",metaDataId);
            params.put("versionCode",version);
            String envelopStr = HttpClientUtil.doGet(comUrl+url,params,username,password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @RequestMapping(value = "/updataMetaSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updataMetaSet(String info,String version) {
        //新增、合二为一
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

            String url = "/meta_data";
            Map<String,Object> params = new HashMap<>();
            params.put("version",version);

            //新增数据元操作
            if(model.getId() ==0){
                params.put("json_data",info);
                String envelopStrNew = HttpClientUtil.doPost(comUrl+url,params,username,password);
                return envelopStrNew;
            }

            //修改数据元操作
            //获取原数据元对象
            String urlGet = "/meta_data";
            Map<String,Object> args = new HashMap<>();
            args.put("versionCode",version);
            args.put("metaDataId",model.getId());
            String envelopStrGet = HttpClientUtil.doGet(comUrl+urlGet,args,username,password);
            Envelop envelopGet = objectMapper.readValue(envelopStrGet,Envelop.class);
            if(!envelopGet.isSuccessFlg()){
                envelop.setErrorMsg("数据元不存在！");
                return envelop;
            }
            String mStr = objectMapper.writeValueAsString(envelopGet.getObj());
            MetaDataModel modelForUpdate = objectMapper.readValue(mStr, MetaDataModel.class);
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
            params.put("json_data",metaDataJson);
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
     * 检验字典查询的方法
     * @param version
     * @return
     */
    @RequestMapping(value = "/getMetaDataDict", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getMetaDataDict(String version) {
        String strResult = "[]";
        try {
            //TODO
            String url = "/std/dicts";
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters","");
            params.put("sorts","");
            params.put("page","1");
            params.put("size","999");
            params.put("version",version);
            String envelopStr = HttpClientUtil.doGet(adminUrl+url,params,username,password);
            Envelop envelopGet = objectMapper.readValue(envelopStr,Envelop.class);
            if (envelopGet.isSuccessFlg()) {
                List<DictModel> dictModels = (List<DictModel>)getEnvelopList(envelopGet.getDetailModelList(),new ArrayList<DictModel>(),DictModel.class);
                strResult = objectMapper.writeValueAsString(dictModels);
            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
        }
        return strResult;
    }


    /**
     * 验证数据元内部编码是否存在
     * @param version
     * @param datasetId
     * @param searchNm
     * @param metaDataCodeMsg
     * @return
     */
    @RequestMapping("/validatorMetadata")
    @ResponseBody
    public Object validatorMetadata(String version, String datasetId, String searchNm, String metaDataCodeMsg) {
        Envelop envelop = new Envelop();
        String url ="/meta_data/is_exist/inner_code";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("version",version);
            params.put("dataSetId",datasetId);
            params.put("inner_code",searchNm);
            String _msg = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("验证失败");
            }
        }catch(Exception ex){
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
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
            String url = "/dataSet/*******";
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