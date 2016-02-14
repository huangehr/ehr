package com.yihu.ehr.standard.cda.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.cda.service.*;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RequestMapping("/cda")
@RestController
public class CdaController extends BaseRestController{

    @Autowired
    private CDADocumentManager cdaDocumentManager;

    @Autowired
    private CdaDatasetRelationshipManager cdaDatasetRelationshipManager;

    @Autowired
    private FastDFSUtil fastDFSUtil;


//    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
//    private static   String username = ResourceProperties.getProperty("username");
//    private static   String password = ResourceProperties.getProperty("password");


    @RequestMapping("GetCdaListByKey")
    @ResponseBody
    public Object GetCdaListByKey(String strKey, String strVersion, String strType, Integer page, Integer rows) {
        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(strVersion, strKey, strType, page, rows);
        int resultCount = cdaDocumentManager.getDocumentCount(strVersion, strKey,strType);
        return getResult(xcdaDocuments, resultCount, page, rows);
    }

    @RequestMapping("getCDAInfoById")
    @ResponseBody
    public Object getCDAInfoById(String strId, String strVersion) {
        List<String> listId = Arrays.asList(strId.split(","));
        CDADocument[] xcdaDocuments = cdaDocumentManager.getDocumentList(strVersion, listId);
        return xcdaDocuments;
    }


    @RequestMapping("getRelationByCdaId")
    @ResponseBody
    public Object getRelationByCdaId(String cdaId, String strVersionCode, String strkey, Integer page, Integer rows) {
        List<CdaDatasetRelationship> relations = cdaDatasetRelationshipManager.getRelationshipByCdaId(cdaId, strVersionCode, strkey, page, rows);
        int resultCount = cdaDatasetRelationshipManager.getRelationshipCountByCdaId(cdaId, strVersionCode, strkey);
        return getResult(relations, resultCount, page, rows);
    }

    @RequestMapping("getALLRelationByCdaId")
    @ResponseBody
    public Object getALLRelationByCdaId(String cdaId, String strVersionCode) {
        List<CdaDatasetRelationshipForInterface> listResult = new ArrayList<>();
        CdaDatasetRelationship[] relations = cdaDatasetRelationshipManager.getRelationshipByCdaId(cdaId, strVersionCode);


        return relations;
    }

    @RequestMapping("SaveCdaInfo")
    @ResponseBody
    public Object SaveCdaInfo(CDAForInterface info) {
        CDADocument cdaInfo = new CDADocument();
        BeanUtils.copyProperties(info,CDADocument.class);
        if(cdaDocumentManager.isDocumentExist(info.getVersionCode(), info.getCode(), info.getId())){
            return false;
        }
        return  cdaDocumentManager.saveDocument(cdaInfo);
    }

    @RequestMapping("deleteCdaInfo")
    @ResponseBody
    public Object deleteCdaInfo(String ids, String strVersionCode) {
        String strErrorMsg = "";
        if (strVersionCode == null || strVersionCode == "") {
            strErrorMsg += "标准版本不能为空!";
        }
        if (ids == null || ids == "") {
            strErrorMsg += "请先选择将要删除的CDA！";
        }

        if (strErrorMsg != "") {
            return false;
        }

        List<String> listIds = Arrays.asList(ids.split(","));
        int iReault = cdaDocumentManager.deleteDocument(strVersionCode, listIds);
        if (iReault >= 0) {
            return true;
        } else {
            return false;
            //CDA删除失败
        }
    }

    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，在新增信息
     * @param strDatasetIds 关联的数据集
     * @param strCdaId  cda文档 ID
     * @param strVersionCode 版本号
     * @param xmlInfo xml 文件内容
     * @return 操作结果
     */
    @RequestMapping("SaveRelationship")
    @ResponseBody
    public Object SaveRelationship(String strDatasetIds, String strCdaId, String strVersionCode, String xmlInfo) throws Exception {
        String strErrorMsg = "";
        if (strVersionCode == null || strVersionCode == "") {
            strErrorMsg += "标准版本不能为空!";
        }
        if (strCdaId == null || strCdaId == "") {
            strErrorMsg += "请先选择CDA!";
        }

        if (strErrorMsg != "") {
            return false;
        }

        List<String> listCdaId = Arrays.asList(strCdaId.split(","));
        int iDelRes = cdaDatasetRelationshipManager.deleteRelationshipByCdaId(strVersionCode, listCdaId);

        if (iDelRes < 0) {
            return false;
            //关系保存失败
        }

        List<String> listIds = new ArrayList<>();
        listIds.add(strCdaId);
        CDADocument[] xcdaDocuments = cdaDocumentManager.getDocumentList(strVersionCode, listIds);
        if (xcdaDocuments.length <= 0) {
            return false;
            //请先选择CDA
        }
        if (xcdaDocuments[0].getFileGroup() != null && !xcdaDocuments[0].getFileGroup().equals("") && xcdaDocuments[0].getSchema() != null && !xcdaDocuments[0].getSchema().equals("")) {
            fastDFSUtil.delete(xcdaDocuments[0].getFileGroup(), xcdaDocuments[0].getSchema());
        }

        if (strDatasetIds == null || strDatasetIds == "") {
            //关系保存成功
            return true;

        }

        strDatasetIds = strDatasetIds.substring(0, strDatasetIds.length() - 1);

        List<String> datasetIds = Arrays.asList(strDatasetIds.split(","));

        CdaDatasetRelationship[] infos = new CdaDatasetRelationship[datasetIds.size()];
        for (int i = 0; i < infos.length; i++) {
            String datasetId = datasetIds.get(i);
            CdaDatasetRelationship info = new CdaDatasetRelationship();
            info.setCdaId(strCdaId);
            info.setDatasetId(datasetId);
            info.setVersionCode(strVersionCode);
            infos[i] = info;
        }

        int iResult = cdaDatasetRelationshipManager.addRelationship(infos);
        if (iResult < 0) {
            //关系保存失败
            return false;
        }

        String strFilePath = SaveCdaFile(xmlInfo, strVersionCode, strCdaId);
        //将文件上传到服务器中
        ObjectNode msg = fastDFSUtil.upload(strFilePath, "");

        String strFileGroup = msg.get(FastDFSUtil.GroupField).asText();//setFilePath
        String strSchemePath = msg.get(FastDFSUtil.RemoteFileField).asText();//setFileName

        File file = new File(strFilePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }

        boolean bRes = SaveXmlFilePath(strCdaId, strVersionCode, strFileGroup, strSchemePath);
        if (bRes) {
            //关系保存成功
            return true;
        } else {
            //关系保存失败
            return false;
        }
    }

    @RequestMapping("DeleteRelationship")
    @ResponseBody
    public Object DeleteRelationship(String ids, String strVersionCode) {
        List<String> relationIds = Arrays.asList(ids.split(","));

        int iResult = cdaDatasetRelationshipManager.deleteRelationshipById(strVersionCode, relationIds);
        if (iResult >= 0) {
            return true;
        } else {
            return false;
            //关系删除失败
        }
    }

    /*
    * 判断文件是否存在*/
    @RequestMapping("/FileExists")
    @ResponseBody
    public String FileExists(String strCdaId, String strVersionCode) {
        //1：已存在文件
        if (cdaDocumentManager.isFileExists(strCdaId, strVersionCode)) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * 生成CDA文件
     *
     * @param strCdaId
     * @param strVersionCode
     * @return
     */
    @RequestMapping("/createCDASchemaFile")
    @ResponseBody
    public boolean createCDASchemaFile(String strCdaId, String strVersionCode) throws Exception {
        int iResult = cdaDocumentManager.createCDASchemaFile(strCdaId, strVersionCode);
        if (iResult >= 0) {
            return true;
        } else {
            return true;
            //CDA文档创建失败
        }
    }

//    @RequestMapping("/TestFileSend")
//    @ResponseBody
//    public void TestFileSend(String strVersion)
//    {
//        try {
//            XStdDispatchManager sendManager = ServiceFactory.getService(Services.StdDispatchManager);
//
//            sendManager.SendStandard(strVersion);
//        }
//        catch (Exception ex)
//        {
//            int t=0;
//        }
//    }

    /*
    * 根据CDA ID 获取数据集信息
    * @param strVersionCode 版本号
    * @param strCdaId CDAID
    * @return Result
    * */
    @RequestMapping("/getDatasetByCdaId")
    @ResponseBody
    public Object getDatasetByCdaId(String strVersionCode, String strCdaId) {
        CdaDatasetRelationship[] relations = cdaDatasetRelationshipManager.getRelationshipByCdaId(strCdaId, strVersionCode);
        List<DataSet> datasetList = new ArrayList<>();
        for (CdaDatasetRelationship info : relations) {
            datasetList.add(info.getDataset());
        }

        List<DataSetForInterface> dataSetModels = new ArrayList<>();

        for (DataSet dataSet : datasetList) {

            DataSetForInterface info = new DataSetForInterface();
            info.setId(String.valueOf(dataSet.getId()));
            info.setCode(dataSet.getCode());
            info.setName(dataSet.getName());

            dataSetModels.add(info);
        }

        if (dataSetModels == null) {
            return false;
        } else {

            return  getResult(dataSetModels, 1, 1, 1);
        }
    }

    @RequestMapping("/validatorCda")
    @ResponseBody
    public boolean validatorCda(String code, String versionCode) {
        return cdaDocumentManager.isDocumentExist(versionCode, code,null);
    }

    /**
     * 将String 保存为XML文件
     *
     * @param fileInfo 文件信息
     * @return 返回 文件路径
     */
    public String SaveCdaFile(String fileInfo, String versionCode, String cdaId) throws IOException {
        fileInfo = fileInfo.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        String strPath = System.getProperty("java.io.tmpdir");
        String splitMark = System.getProperty("file.separator");
        strPath += splitMark+"StandardFiles";
        //文件路径
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "createfile" + splitMark + cdaId + ".xml";

        File file = new File(strXMLFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fileInfo);
        bw.flush();
        bw.close();
        fw.close();

        return strXMLFilePath;
    }

    public boolean SaveXmlFilePath(String cdaId, String versionCode, String fileGroup, String filePath) {
        List<String> listIds = new ArrayList<>();
        listIds.add(cdaId);
        CDADocument[] xcdaDocuments = cdaDocumentManager.getDocumentList(versionCode, listIds);
        if (xcdaDocuments.length <= 0) {
            //未找到CDA
            return false;
        }

        xcdaDocuments[0].setFileGroup(fileGroup);
        xcdaDocuments[0].setSchema(filePath);
        xcdaDocuments[0].setVersionCode(versionCode);
        return cdaDocumentManager.saveDocument(xcdaDocuments[0]);
    }

    /**
     * 获取cda文档的XML文件信息。
     * <p>
     * 从服务器的临时文件路径中读取配置文件，并以XML形式返回。
     *
     * @param cdaId
     * @param versionCode
     * @return XML信息
     * @version 1.0.1 将临时目录转移至fastDFS。
     */
    @RequestMapping("/getCdaXmlFileInfo")
    @ResponseBody
    public Object getCdaXmlFileInfo(String cdaId, String versionCode) throws Exception {
        String strXmlInfo = "";
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";

        String splitMark = System.getProperty("file.separator");
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "downfile" + splitMark;

        List<String> listIds = new ArrayList<>();
        listIds.add(cdaId);

        CDADocument[] xcdaDocuments = cdaDocumentManager.getDocumentList(versionCode, listIds);
        String strFileGroup = "";
        String strSchemePath = "";
        if (xcdaDocuments.length > 0) {
            strFileGroup = xcdaDocuments[0].getFileGroup();
            strSchemePath = xcdaDocuments[0].getSchema();
        } else {
            return "";
        }

        File files = new File(strXMLFilePath);
        if (!files.exists()) {
            files.mkdirs();
        }

        String strLocalFileName = strXMLFilePath + "\\" + strSchemePath.replaceAll("/", "_");
        File localFile = new File(strLocalFileName);
        if (localFile.exists() && localFile.isFile()) {
            localFile.delete();
        }
        if (!strFileGroup.equals("") && !strSchemePath.equals("")) {
            strLocalFileName = fastDFSUtil.download(strFileGroup, strSchemePath, strXMLFilePath);

            File file = new File(strLocalFileName);
            FileReader fr = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fr);
            strXmlInfo = bReader.readLine();
        } else {
            strXmlInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root></root>";
        }
        return true;
    }

//    @RequestMapping("/getOrgType")
//    @ResponseBody
//    public Object getOrgType() {
//        String url = "/rest/v1.0/conDict/orgType";
//        Map<String, Object> params = new HashMap<>();
//        params.put("type","Govement");
//        String _res = HttpClientUtil.doGet(host + url, params, username, password);
//        return _res;
//    }
}
