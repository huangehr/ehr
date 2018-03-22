package com.yihu.ehr.profile.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.resource.MCdaTransformDto;
import com.yihu.ehr.profile.dao.ArchiveTemplateDao;
import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.feign.RedisServiceClient;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.feign.XStdRedisServiceClient;
import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.hos.model.standard.MCDADocument;
import com.yihu.hos.model.standard.MCdaDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CDA相关接口服务
 * @author hzp 2016-06-16
 */
@Service
public class ProfileCDAService {

    @Value("${spring.application.id}")
    private String appId;

    @Autowired
    private ResourceClient resource;
    @Autowired
    private RedisServiceClient redisServiceClient;
    @Autowired
    private XStdRedisServiceClient stdRedisServiceClient;
    @Autowired
    private ArchiveTemplateDao templateDao;
    @Autowired
    private CDADocumentClient cdaService;
    @Autowired
    private ObjectMapper objectMapper;

    //event_type对应cda_code
    Map<String,String[]> eventTypeCDA = new HashMap() {
        {
            put("3", new String[]{"HSDC01.04","HSDC01.05"}); //处方
            put("4", new String[]{"HSDC02.03","HSDC02.04"}); //医嘱
            put("5", new String[]{"HSDC02.09","HSDC02.10"}); //检查检验
        }
    };

    /**
     * 根据ProfileId查询CDA分类
     */
    public List<Map<String, Object>> getCDAClass(String profileId) {
        List<Map<String, Object>> result = new ArrayList<>();
        //根据profileId或者eventNo获取主记录
        String query = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop envelop = resource.getMasterData(query, 1, 1, null);
        List<Map<String, Object>> dataList = envelop.getDetailModelList();
        if (dataList.size() > 0) {
            Map<String, Object> obj = dataList.get(0);
            String profileType = obj.get("profile_type").toString(); //0结构化  1非结构化
            String cdaVersion = obj.get("cda_version").toString();
            if (profileType.equals("0")) {
                String orgCode = obj.get("org_code").toString();
                //根据机构获取定制模板
                List<ArchiveTemplate> list = templateDao.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
                //遍历模板
                if (list.size() > 0) {
                    //获取档案相关数据集编码列表
                    List<String> dataSetContains = new ArrayList<>();
                    Envelop data = resource.getSubData("{\"q\":\"profile_id:" + profileId + "\"}", 1, 500, null);
                    List<Map<String, Object>> subData = data.getDetailModelList();
                    if (subData.size() > 0) {
                        subData.forEach(item -> {
                            String dataSetCode = String.valueOf(item.get("rowkey")).split("\\$")[1];
                            if (!dataSetContains.contains(dataSetCode)) {
                                dataSetContains.add(dataSetCode);
                            }
                        });
                    }
                    for (ArchiveTemplate template : list) {
                        String cdaDocumentId = template.getCdaDocumentId();
                        String cdaCode = template.getCdaCode();
                        //获取CDA关联数据集
                        List<MCdaDataSet> dataSetList = cdaService.getCDADataSetByCDAId(cdaVersion, cdaDocumentId);
                        if (dataSetList != null && dataSetList.size() > 0) {
                            for (MCdaDataSet mCdaDataSet : dataSetList) {
                                String mDataSetCode = mCdaDataSet.getDataSetCode();
                                if (dataSetContains.contains(mDataSetCode)) {
                                    Map<String, Object> item = new HashMap<>();
                                    item.put("profile_id", profileId);
                                    item.put("profile_type", profileType);
                                    item.put("template_id", template.getId());
                                    item.put("cda_document_id", template.getCdaDocumentId());
                                    item.put("cda_code", cdaCode);
                                    item.put("template_name", template.getTitle());
                                    item.put("pc_template", template.getPcTplURL());
                                    item.put("mobile_template", template.getMobileTplURL());
                                    result.add(item);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {  //非结构化取数据rawfile
                Envelop data = resource.getRawFiles(profileId, null, null, null);
                if (data.getDetailModelList() != null && data.getDetailModelList().size() > 0) {
                    for (int i = 0; i< data.getDetailModelList().size(); i++) {
                        Map<String,Object> map = (Map<String, Object>) data.getDetailModelList().get(i);
                        String cdaDocumentId = map.get("cda_document_id").toString();
                        //获取cda内容
                        MCDADocument cdaDocument = cdaService.getCDADocuments(cdaVersion,cdaDocumentId);
                        Map<String, Object> item = new HashMap<>();
                        item.put("profile_id", profileId);
                        item.put("profile_type", profileType);
                        item.put("cda_document_id", cdaDocumentId);
                        item.put("cda_code", cdaDocument.getCode());
                        item.put("template_name", cdaDocument.getName());
                        item.put("file_list", map.get("file_list"));
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取某个CDA文档数据
     * @param profileId
     * @param cdaDocumentId
     * @return
     */
    public Map<String, Object> getCDAData(String profileId, String cdaDocumentId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //主表记录
        Envelop envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1,null);
        if (envelop.getDetailModelList().size() > 0) {
            Map<String, Object> event = (Map<String, Object>) envelop.getDetailModelList().get(0);
            result = (Map<String, Object>)getCDAPartData(event,true, cdaDocumentId).get(cdaDocumentId);
        }
        return result;
    }



    /**
     * 通过事件号获取CDADocumentId
     */
    public Map<String, Object> getCDADocumentId(String orgCode,String eventNo, String cdaCode) throws Exception {
        Map<String, Object> re = new HashMap<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\"org_code:"+orgCode+"+AND+event_no:" + eventNo + "\"}", null, null);

        //是否有数据
        if (result.getDetailModelList() != null && result.getTotalCount() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            String cdaVersion = obj.get("cda_version").toString();
            //获取模板ID
            ArchiveTemplate template = templateDao.findByOrganizationCodeAndCdaVersionAndCdaCode(orgCode, cdaVersion, cdaCode);
            if (template != null) {
                re.put("template_id",template.getId());
                re.put("profile_id",obj.get("rowkey"));
                re.put("cda_document_id",template.getCdaDocumentId());
            }
        }
        return re;
    }

    /**
     * 获取完整CDA档案
     */
    public Map<String,Object> getCDADocument(String profileId) throws Exception {
        //权限控制******
        Map<String,Object> re = new HashMap<>();
        re.put("profile_id",profileId);
        //主表记录
        Envelop profile = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1,null);
        if (profile.getDetailModelList() != null && profile.getDetailModelList().size() > 0) {
            Map<String, Object> profileMap = (Map<String, Object>) profile.getDetailModelList().get(0);
            re.put("event_type",profileMap.get("event_type"));
            re.put("patient_id",profileMap.get("patient_id"));
            re.put("org_code",profileMap.get("org_code"));
            re.put("event_no",profileMap.get("event_no"));

            //获取有数据的cda class
            List<Map<String, Object>> cdaClassList = getCDAClass(profileId);
            List<Map<String, Object>> CDADataList = new ArrayList<>();
            List<String>cdaDocumentIdList=new ArrayList<>();
            for (Map<String, Object> cda : cdaClassList) {
                cdaDocumentIdList.add(cda.get("cda_document_id").toString());
            }
            Map<String, Object> CDAData = getCDAPartData(profileMap, false,(String[])cdaDocumentIdList.toArray(new String[cdaDocumentIdList.size()]));
            for(String key :CDAData.keySet()){
                CDADataList.add((Map<String, Object>)CDAData.get(key));
            }
            re.put("cda_documents",CDADataList);

        }
        else{
            throw new Exception("未找到该CDA档案！（profile_id："+profileId+"）");
        }

        return re;
    }

    /**
     * 获取CDA文档数据片段
     */
    private Map<String, Object> getCDAPartData(Map<String, Object> event, boolean isPart, String...cdaDocumentIdList) throws Exception {
        String cdaVersion = event.get("cda_version").toString();
        List<String> masterDataSetCodeList = new ArrayList<>();
        List<String> multiDataSetCodeList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        //获取CDA关联数据集
        Map<String, List<String>> masterDataSetMap = new HashMap<>();
        Map<String, List<String>> multiDataSetMap = new HashMap<>();
        Map<String, List<MCdaDataSet>> CDADataSetMap = cdaService.getCDADataSetByCDAIdList(cdaVersion, cdaDocumentIdList);
        for (String cdaId : CDADataSetMap.keySet()) {
            List<MCdaDataSet> CDADataSet = CDADataSetMap.get(cdaId);
            if (CDADataSet != null && CDADataSet.size() > 0) {
                for (MCdaDataSet dataSet : CDADataSet) {
                    String dataSetCode = dataSet.getDataSetCode();
                    String multiRecord = dataSet.getMultiRecord();
                    if (multiRecord.equals("0")) {
                        //主表数据
                        masterDataSetCodeList.add(dataSetCode);
                    } else {
                        //细表数据
                        multiDataSetCodeList.add(dataSetCode);
                    }
                }
                masterDataSetMap.put(cdaId, masterDataSetCodeList);
                multiDataSetMap.put(cdaId, multiDataSetCodeList);
            }
        }
        //获取所有数据集数据
        MCdaTransformDto cdaTransformDto = new MCdaTransformDto();
        cdaTransformDto.setMasterJson(event);
        cdaTransformDto.setMasterDatasetCodeList(masterDataSetMap);
        cdaTransformDto.setMultiDatasetCodeList(multiDataSetMap);
        Map<String, Object> dataMap = resource.getCDAData(objectMapper.writeValueAsString(cdaTransformDto));
        for (String cdaId : dataMap.keySet()) {
            Map<String, Object> dataSetMap = (Map<String, Object>)dataMap.get(cdaId);
            for (String dataSetCode : dataSetMap.keySet()) {
                List<Map<String, Object>> dataSetList = (List<Map<String, Object>>)dataSetMap.get(dataSetCode);
                for (Map<String, Object> metadataMap : dataSetList) {
                    //诊断代码
                    if (metadataMap.containsKey("JDSD00_79_021")) {
                        String icd10Code = (String) metadataMap.get("JDSD00_79_021");
                        String icd10Name = redisServiceClient.getIcd10Name(icd10Code);
                        if (!StringUtils.isEmpty(icd10Name)) {
                            metadataMap.put("JDSD00_79_021", icd10Name);
                        }
                    }
                    //字典项转为字典值
                    /*for (String key3 : metadataMap.keySet()) {
                        String dictId = stdRedisServiceClient.getMetaDataDict(cdaVersion, dataSetCode, key3);
                        if (!StringUtils.isEmpty(dictId) && !dictId.equals("0")) {
                            String dictValue = stdRedisServiceClient.getDictEntryValue(cdaVersion, dictId, (String) metadataMap.get(key3));
                            if (!StringUtils.isEmpty(dictValue)) {
                                metadataMap.put(key3, dictValue);
                            }
                        }
                    }*/
                    metadataMap.put("org_area", event.get("org_area"));
                    metadataMap.put("org_name", event.get("org_name"));
                    metadataMap.put("event_date", event.get("event_date"));
                }
            }
        }
        //获取cda document数据
        Map<String, MCDADocument> cdaMap = cdaService.getCDADocumentsList(cdaVersion, cdaDocumentIdList);
        for (String cdaId : cdaMap.keySet()) {
            Map<String, Object> re = new HashMap<>();
            re.put("cda_version", cdaVersion);
            re.put("cda_document_id", cdaId);
            re.put("cda_document_name", cdaMap.get(cdaId).getName());
            re.put("data_sets", dataMap.get(cdaId));
            re.put("event_type", event.get("event_type"));
            re.put("patient_id", event.get("patient_id"));
            re.put("org_code", event.get("org_code"));
            re.put("org_name", event.get("org_name"));
            re.put("event_no", event.get("event_no"));
            re.put("event_date", event.get("event_date"));
            result.put(cdaId, re);
        }
        //非结构化数据
        /*Map<String, Envelop> rawFilesMap = resource.getRawFilesList(profileId, cdaDocumentIdList);
        for (String key : rawFilesMap.keySet()) {
            Envelop rawFiles = rawFilesMap.get(key);
            if (rawFiles.getDetailModelList() != null && rawFiles.getDetailModelList().size() > 0) {
                Map<String, Object> rawFile = (Map<String, Object>) rawFiles.getDetailModelList().get(0);
                String fileString = rawFile.get("file_list").toString();
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Map.class);
                List<Map<String, Object>> fileList = objectMapper.readValue(fileString, javaType);
                ((Map<String, Object>) result.get(key)).put("file_list", fileList);
            }
        }*/
        return result;
    }

}
