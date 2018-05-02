package com.yihu.ehr.profile.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.resource.MCdaTransformDto;
import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.feign.XStdRedisServiceClient;
import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.profile.model.MCDADocument;
import com.yihu.hos.model.standard.MCdaDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
    private XStdRedisServiceClient stdRedisServiceClient;
    @Autowired
    private ArchiveTemplateService archiveTemplateService;
    @Autowired
    private CDADocumentClient cdaService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ResourcesTransformService resourcesTransformService;

    /**
     * 根据ProfileId查询CDA分类
     */
    public List<Map<String, Object>> getCDAClass(String profileId, String templateName) throws Exception {
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
                List<ArchiveTemplate> list;
                if (StringUtils.isEmpty(templateName)) {
                    //pc接口
                    list = archiveTemplateService.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
                } else {
                    //mobile接口
                    list = archiveTemplateService.search("organizationCode=" + orgCode + ";cdaVersion=" + cdaVersion + ";title?" + templateName);
                }
                //遍历模板
                if (list.size() > 0) {
                    //获取档案相关数据集编码列表
                    Set<String> dataSetContains = new HashSet<>();
                    Envelop data = resource.getSubData("{\"q\":\"profile_id:" + profileId + "\"}", 1, 500, null);
                    List<Map<String, Object>> subData = data.getDetailModelList();
                    if (subData.size() > 0) {
                        subData.forEach(item -> {
                            String dataSetCode = String.valueOf(item.get("rowkey")).split("\\$")[1];
                            dataSetContains.add(dataSetCode);
                        });
                    }
                    for (ArchiveTemplate template : list) {
                        String cdaDocumentId = template.getCdaDocumentId();
                        String cdaCode = template.getCdaCode();
                        //获取CDA关联数据集
                        List<MCdaDataSet> dataSetList = cdaService.getCDADataSetByCDAId(cdaVersion, cdaDocumentId);
                        if (dataSetList != null) {
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
     * @param profileId 就诊时间主索引
     * @param cdaDocumentId cda文档ID
     * @param transform 是否转换
     * @return
     * @throws Exception
     */
    public Map<String, Object> getCDAData(String profileId, String cdaDocumentId, boolean transform) throws Exception {
        //主表记录
        Envelop envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1,null);
        if (envelop.getDetailModelList().size() > 0) {
            Map<String, Object> event = (Map<String, Object>) envelop.getDetailModelList().get(0);
            return (Map<String, Object>)getCDAPartData(event,true, transform, cdaDocumentId).get(cdaDocumentId);
        }
        return new HashMap<>();
    }

    /**
     * 获取CDA文档数据片段
     * @param event 主表事件数据
     * @param isPart 版本遗留字段 - 目前无用
     * @param transform 是否转换成标准
     * @param cdaDocumentIdList cda列表
     * @return
     * @throws Exception
     */
    private Map<String, Object> getCDAPartData(Map<String, Object> event, boolean isPart, boolean transform, String...cdaDocumentIdList) throws Exception {
        String cdaVersion = String.valueOf(event.get("cda_version"));
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, List<MCdaDataSet>> CDADataSetMap = cdaService.getCDADataSetByCDAIdList(cdaVersion, cdaDocumentIdList);
        if (!transform) {
            for (String cdaId : CDADataSetMap.keySet()) {
                List<MCdaDataSet> CDADataSet = CDADataSetMap.get(cdaId);
                Map<String, Object> tempMap = new HashMap<>();
                List<String> multipleDataset = new ArrayList<>();
                if (CDADataSet != null && CDADataSet.size() > 0) {
                    CDADataSet.forEach(item -> {
                        String dataSetCode = item.getDataSetCode();
                        if (item.getMultiRecord().equals("0")) {
                            //主表数据
                            if (tempMap.containsKey(dataSetCode)) {
                                List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                tempList.add(resourcesTransformService.stdMerge(event, dataSetCode, cdaVersion));
                            } else {
                                List<Map<String, Object>> tempList = new ArrayList<>();
                                tempList.add(resourcesTransformService.stdMerge(event, dataSetCode, cdaVersion));
                                tempMap.put(dataSetCode, tempList);
                            }
                        } else {
                            multipleDataset.add(dataSetCode);
                        }
                    });
                }
                //细表数据
                String profileId = String.valueOf(event.get("rowkey"));
                String subQ = "{\"q\":\"profile_id:" + profileId + "\"}";
                Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                List<Map<String, Object>> subList = subData.getDetailModelList();
                subList.forEach(item -> {
                    item.put("org_area", event.get("org_area"));
                    item.put("org_name", event.get("org_name"));
                    item.put("event_date", event.get("event_date"));
                    String dataSetCode = String.valueOf(item.get("rowkey")).split("\\$")[1];
                    if (tempMap.containsKey(dataSetCode)) {
                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                        tempList.add(item);
                    } else {
                        List<Map<String, Object>> tempList = new ArrayList<>();
                        tempList.add(item);
                        tempMap.put(dataSetCode, tempList);
                    }
                });
                multipleDataset.forEach(item -> {
                    if (!tempMap.containsKey(item)) {
                        tempMap.put(item, new ArrayList<>());
                    }
                });
                dataMap.put(cdaId, tempMap);
            }
        } else {
            for (String cdaId : CDADataSetMap.keySet()) {
                List<MCdaDataSet> CDADataSet = CDADataSetMap.get(cdaId);
                Map<String, Object> tempMap = new HashMap<>();
                List<String> multipleDataset = new ArrayList<>();
                if (CDADataSet != null && CDADataSet.size() > 0) {
                    CDADataSet.forEach(item -> {
                        String dataSetCode = item.getDataSetCode();
                        if (item.getMultiRecord().equals("0")) {
                            //主表数据
                            if (tempMap.containsKey(dataSetCode)) {
                                List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                tempList.add(resourcesTransformService.stdTransform(event, dataSetCode, cdaVersion));
                            } else {
                                List<Map<String, Object>> tempList = new ArrayList<>();
                                tempList.add(resourcesTransformService.stdTransform(event, dataSetCode, cdaVersion));
                                tempMap.put(dataSetCode, tempList);
                            }
                        } else {
                            multipleDataset.add(dataSetCode);
                        }
                    });
                }
                //细表数据
                String profileId = String.valueOf(event.get("rowkey"));
                String subQ = "{\"q\":\"profile_id:" + profileId + "\"}";
                Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                List<Map<String, Object>> subList = subData.getDetailModelList();
                subList.forEach(item -> {
                    item.put("org_area", event.get("org_area"));
                    item.put("org_name", event.get("org_name"));
                    item.put("event_date", event.get("event_date"));
                    String dataSetCode = String.valueOf(item.get("rowkey")).split("\\$")[1];
                    if (tempMap.containsKey(dataSetCode)) {
                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                    } else {
                        List<Map<String, Object>> tempList = new ArrayList<>();
                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                        tempMap.put(dataSetCode, tempList);
                    }
                });
                multipleDataset.forEach(item -> {
                    if (!tempMap.containsKey(item)) {
                        tempMap.put(item, new ArrayList<>());
                    }
                });
                dataMap.put(cdaId, tempMap);
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

    // -------------------------------------- 尚未使用的接口 --------------------------------------

    /**
     * 获取完整CDA档案
     */
    /*public Map<String,Object> getCDADocument(String profileId) throws Exception {
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
            List<Map<String, Object>> cdaClassList = getCDAClass(profileId, null);
            List<Map<String, Object>> CDADataList = new ArrayList<>();
            List<String>cdaDocumentIdList=new ArrayList<>();
            for (Map<String, Object> cda : cdaClassList) {
                cdaDocumentIdList.add(cda.get("cda_document_id").toString());
            }
            Map<String, Object> CDAData = getCDAPartData(profileMap, false, (String[])cdaDocumentIdList.toArray(new String[cdaDocumentIdList.size()]));
            for (String key :CDAData.keySet()){
                CDADataList.add((Map<String, Object>)CDAData.get(key));
            }
            re.put("cda_documents",CDADataList);

        } else{
            throw new Exception("未找到该CDA档案！（profile_id：" + profileId + "）");
        }

        return re;
    }*/

    /**
     * 通过事件号获取CDADocumentId
     */
    /*public Map<String, Object> getCDADocumentId(String orgCode, String eventNo, String cdaCode) throws Exception {
        Map<String, Object> re = new HashMap<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\"org_code:"+orgCode+"+AND+event_no:" + eventNo + "\"}", null, null);
        //是否有数据
        if (result.getDetailModelList() != null && result.getTotalCount() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            String cdaVersion = obj.get("cda_version").toString();
            //获取模板ID
            ArchiveTemplate template = archiveTemplateService.findByOrganizationCodeAndCdaVersionAndCdaCode(orgCode, cdaVersion, cdaCode);
            if (template != null) {
                re.put("template_id",template.getId());
                re.put("profile_id",obj.get("rowkey"));
                re.put("cda_document_id",template.getCdaDocumentId());
            }
        }
        return re;
    }*/


}
