package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.profile.service.template.ArchiveTemplateService;
import com.yihu.ehr.profile.util.BasicConstant;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.profile.model.MCDADocument;
import com.yihu.hos.model.standard.MCdaDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * CDA相关接口服务
 * @author hzp 2016-06-16
 */
@Service
public class ProfileCDAService extends ProfileBasicService {

    @Autowired
    private ArchiveTemplateService archiveTemplateService;
    @Autowired
    private ResourcesTransformService resourcesTransformService;

    /**
     * 根据ProfileId查询CDA分类
     */
    public List<Map<String, Object>> getCDAClass (String profileId, String templateName) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        //根据profileId或者eventNo获取主记录
        String query = "{\"q\":\"rowkey:" + profileId + "\"}";
        Envelop envelop = resource.getMasterData(query, 1, 1, null);
        List<Map<String, Object>> dataList = envelop.getDetailModelList();
        if (dataList.size() > 0) {
            Map<String, Object> obj = dataList.get(0);
            String profileType = obj.get(BasicConstant.profileType).toString();
            String cdaVersion = obj.get(BasicConstant.cdaVersion).toString();
            String eventType = obj.get(BasicConstant.eventType).toString();
            if (profileType.equals("0") || profileType.equals("1")) {
                //根据机构获取定制模板
                List<ArchiveTemplate> list;
                if (StringUtils.isEmpty(templateName)) {
                    //pc接口
                    list = archiveTemplateService.search("type=universal," + ArchiveTemplate.Type.values()[new Integer(eventType)] + ";cdaVersion=" + cdaVersion);
                } else {
                    //mobile接口
                    list = archiveTemplateService.search("type=universal," + ArchiveTemplate.Type.values()[new Integer(eventType)] + ";cdaVersion=" + cdaVersion + ";title?" + templateName);
                }
                //遍历模板
                List<Map<String, Object>> prescription = new ArrayList<>();
                list.forEach(item -> {
                    Map<String, Object> temp = new HashMap<>();
                    if (item.getTitle().contains("检查报告")) {
                        String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_79$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        List<Map<String, Object>> data = new ArrayList<>();
                        subList.forEach(item2 -> {
                            Map<String, Object> dataMap = new HashMap<>();
                            String item2Name = (String) item2.get("EHR_002883"); //检查名称
                            dataMap.put(BasicConstant.profileId, item2.get(BasicConstant.rowkey));
                            dataMap.put(BasicConstant.profileType, profileType);
                            dataMap.put("cda_document_id", item.getCdaDocumentId());
                            dataMap.put("cda_code", item.getCdaCode());
                            dataMap.put("pc_template", item.getPcUrl());
                            dataMap.put("mobile_template", item.getMobileUrl());
                            dataMap.put("template_id", item.getId());
                            dataMap.put("name", item2Name);
                            dataMap.put("mark", item2.get("EHR_000316")); //检查报告单号
                            data.add(dataMap);
                        });
                        temp.put("template_name", item.getTitle());
                        temp.put("multi", true);
                        temp.put("data", new HashMap<>());
                        temp.put("records", data);
                    } else if (item.getTitle().contains("检验报告")) {
                        String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_77$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        List<Map<String, Object>> data = new ArrayList<>();
                        subList.forEach(item2 -> {
                            Map<String, Object> dataMap = new HashMap<>();
                            String item2Name = (String) item2.get("EHR_000352"); //检验项目
                            dataMap.put(BasicConstant.profileId, item2.get(BasicConstant.rowkey));
                            dataMap.put(BasicConstant.profileType, profileType);
                            dataMap.put("cda_document_id", item.getCdaDocumentId());
                            dataMap.put("cda_code", item.getCdaCode());
                            dataMap.put("pc_template", item.getPcUrl());
                            dataMap.put("mobile_template", item.getMobileUrl());
                            dataMap.put("template_id", item.getId());
                            dataMap.put("name", item2Name);
                            dataMap.put("mark", item2.get("EHR_000363")); //检验报告单号
                            data.add(dataMap);
                        });
                        temp.put("template_name", item.getTitle());
                        temp.put("multi", true);
                        temp.put("data", new HashMap<>());
                        temp.put("records", data);
                    } else if (item.getTitle().contains("处方")) {
                        if (item.getTitle().contains("中药")) {
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put(BasicConstant.profileId, profileId);
                            dataMap.put(BasicConstant.profileType, profileType);
                            dataMap.put("cda_document_id", item.getCdaDocumentId());
                            dataMap.put("cda_code", item.getCdaCode());
                            dataMap.put("pc_template", item.getPcUrl());
                            dataMap.put("mobile_template", item.getMobileUrl());
                            dataMap.put("template_id", item.getId());
                            dataMap.put("name", "中药处方");
                            dataMap.put("mark", "01");
                            prescription.add(dataMap);
                            return;
                        } else if (item.getTitle().contains("西药")) {
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put(BasicConstant.profileId, profileId);
                            dataMap.put(BasicConstant.profileType, profileType);
                            dataMap.put("cda_document_id", item.getCdaDocumentId());
                            dataMap.put("cda_code", item.getCdaCode());
                            dataMap.put("pc_template", item.getPcUrl());
                            dataMap.put("mobile_template", item.getMobileUrl());
                            dataMap.put("template_id", item.getId());
                            dataMap.put("name", "西药处方");
                            dataMap.put("mark", "02");
                            prescription.add(dataMap);
                            return;
                        }
                    } else {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put(BasicConstant.profileId, profileId);
                        dataMap.put(BasicConstant.profileType, profileType);
                        dataMap.put("cda_document_id", item.getCdaDocumentId());
                        dataMap.put("cda_code", item.getCdaCode());
                        dataMap.put("pc_template", item.getPcUrl());
                        dataMap.put("mobile_template", item.getMobileUrl());
                        dataMap.put("template_id", item.getId());
                        dataMap.put("name", item.getTitle());
                        dataMap.put("mark", "single");
                        temp.put("template_name", item.getTitle());
                        temp.put("multi", false);
                        temp.put("data", dataMap);
                        temp.put("records", new ArrayList<>());
                    }
                    result.add(temp);
                });
                Map<String, Object> temp = new HashMap<>();
                temp.put("template_name", "门诊处方");
                temp.put("multi", true);
                temp.put("data", new HashMap<>());
                temp.put("records", prescription);
                result.add(temp);
            } else {  //非结构化取数据rawfile
                Envelop data = resource.getRawFiles(profileId, null, null, null);
                if (data.getDetailModelList() != null) {
                    for (int i = 0; i< data.getDetailModelList().size(); i++) {
                        Map<String,Object> map = (Map<String, Object>) data.getDetailModelList().get(i);
                        String cdaDocumentId = map.get("cda_document_id").toString();
                        //获取cda内容
                        MCDADocument cdaDocument = cdaService.getCDADocuments(cdaVersion,cdaDocumentId);
                        Map<String, Object> item = new HashMap<>();
                        item.put(BasicConstant.profileId, profileId);
                        item.put(BasicConstant.profileType, profileType);
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
    public Map<String, Object> getCDAData(String profileId, boolean transform, String mark, String cdaDocumentId) throws Exception {
        return (Map<String, Object>)getCDAPartData(profileId, transform, mark, cdaDocumentId).get(cdaDocumentId);
    }

    /**
     * 获取CDA文档数据片段
     * @param profileId
     * @param transform
     * @param cdaDocumentIdList
     * @return
     * @throws Exception
     */
    private Map<String, Object> getCDAPartData(String profileId, boolean transform, String mark, String...cdaDocumentIdList) throws Exception {
        final boolean is_multi = profileId.contains("$") ? true : false;
        Envelop envelop;
        if (is_multi) {
            envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId.split("\\$")[0] + "\"}", 1, 1,null);
        } else {
            envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1,null);
        }
        List<Map<String, Object>> eventList = envelop.getDetailModelList();
        if (eventList != null && eventList.size() > 0) {
            Map<String, Object> event = eventList.get(0);
            String cdaVersion = String.valueOf(event.get(BasicConstant.cdaVersion));
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> dataMap = new HashMap<>();
            Map<String, List<MCdaDataSet>> cdaDataSetMap = cdaService.getCDADataSetByCDAIdList(cdaVersion, cdaDocumentIdList);
            if (!transform) {
                cdaDataSetMap.keySet().forEach(cdaDocId -> {
                    List<MCdaDataSet> cdaDataSets = cdaDataSetMap.get(cdaDocId);
                    Map<String, Object> tempMap = new HashMap<>();
                    List<String> multipleDataset = new ArrayList<>();
                    if (cdaDataSets != null) {
                        cdaDataSets.forEach(cdaDataSet -> {
                            String dataSetCode = cdaDataSet.getDataSetCode();
                            if (cdaDataSet.getMultiRecord().equals("0")) {
                                //主表数据
                                if (!tempMap.containsKey(dataSetCode)) {
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
                    if (is_multi) {
                        String [] combination = profileId.split("\\$");
                        String subQ = "{\"q\":\"profile_id:" + combination[0] + "\"}";
                        Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                        List<Map<String, Object>> subList = subData.getDetailModelList();
                        subList.forEach(item -> {
                            item.put(BasicConstant.orgArea, event.get(BasicConstant.orgArea));
                            item.put(BasicConstant.orgName, event.get(BasicConstant.orgName));
                            item.put(BasicConstant.eventDate, event.get(BasicConstant.eventDate));
                            String dataSetCode = String.valueOf(item.get(BasicConstant.rowkey)).split("\\$")[1];
                            if (combination[1].equals(dataSetCode)) {
                                if (profileId.equals(item.get(BasicConstant.rowkey))) {
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(item);
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(item);
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
                            if (combination[1].equals("HDSD00_79") && dataSetCode.equals("HDSD00_78") && !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006291"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(item);
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(item);
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
                            if (combination[1].equals("HDSD00_77") && dataSetCode.equals("HDSD00_76") && !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006294"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(item);
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(item);
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                            }
                            if (combination[1].equals("HDSD00_77") && dataSetCode.equals("HDSD00_75") && !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006339"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(item);
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(item);
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
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
                        dataMap.put(cdaDocId, tempMap);
                    } else {
                        String subQ = "{\"q\":\"profile_id:" + profileId + "\"}";
                        Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                        List<Map<String, Object>> subList = subData.getDetailModelList();
                        subList.forEach(item -> {
                            item.put(BasicConstant.orgArea, event.get(BasicConstant.orgArea));
                            item.put(BasicConstant.orgName, event.get(BasicConstant.orgName));
                            item.put(BasicConstant.eventDate, event.get(BasicConstant.eventDate));
                            String dataSetCode = String.valueOf(item.get(BasicConstant.rowkey)).split("\\$")[1];
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
                        dataMap.put(cdaDocId, tempMap);
                    }
                });
            } else {
                cdaDataSetMap.keySet().forEach(cdaDocId -> {
                    List<MCdaDataSet> cdaDataSets = cdaDataSetMap.get(cdaDocId);
                    Map<String, Object> tempMap = new HashMap<>();
                    List<String> multipleDataset = new ArrayList<>();
                    if (cdaDataSets != null && cdaDataSets.size() > 0) {
                        cdaDataSets.forEach(item -> {
                            String dataSetCode = item.getDataSetCode();
                            if (item.getMultiRecord().equals("0")) {
                                //主表数据
                                if (!tempMap.containsKey(dataSetCode)) {
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
                    if (is_multi) {
                        String [] combination = profileId.split("\\$");
                        String subQ = "{\"q\":\"profile_id:" + combination[0] + "\"}";
                        Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                        List<Map<String, Object>> subList = subData.getDetailModelList();
                        subList.forEach(item -> {
                            item.put(BasicConstant.orgArea, event.get(BasicConstant.orgArea));
                            item.put(BasicConstant.orgName, event.get(BasicConstant.orgName));
                            item.put(BasicConstant.eventDate, event.get(BasicConstant.eventDate));
                            String dataSetCode = String.valueOf(item.get(BasicConstant.rowkey)).split("\\$")[1];
                            if (combination[1].equals(dataSetCode)) {
                                if (profileId.equals(item.get(BasicConstant.rowkey))) {
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
                            if (combination[1].equals("HDSD00_79") && dataSetCode.equals("HDSD00_78") && !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006291"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
                            if (combination[1].equals("HDSD00_77") && dataSetCode.equals("HDSD00_76") &&  !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006294"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
                            if (combination[1].equals("HDSD00_77") && dataSetCode.equals("HDSD00_75") && !StringUtils.isEmpty(mark)) {
                                if (mark.equals(item.get("EHR_006501"))) { //报告单号
                                    if (tempMap.containsKey(dataSetCode)) {
                                        List<Map<String, Object>> tempList = (List<Map<String, Object>>) tempMap.get(dataSetCode);
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                    } else {
                                        List<Map<String, Object>> tempList = new ArrayList<>();
                                        tempList.add(resourcesTransformService.stdTransform(item, dataSetCode, cdaVersion));
                                        tempMap.put(dataSetCode, tempList);
                                    }
                                }
                                return;
                            }
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
                        dataMap.put(cdaDocId, tempMap);
                    } else {
                        String subQ = "{\"q\":\"profile_id:" + profileId + "\"}";
                        Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                        List<Map<String, Object>> subList = subData.getDetailModelList();
                        subList.forEach(item -> {
                            item.put(BasicConstant.orgArea, event.get(BasicConstant.orgArea));
                            item.put(BasicConstant.orgName, event.get(BasicConstant.orgName));
                            item.put(BasicConstant.eventDate, event.get(BasicConstant.eventDate));
                            String dataSetCode = String.valueOf(item.get(BasicConstant.rowkey)).split("\\$")[1];
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
                        dataMap.put(cdaDocId, tempMap);
                    }
                });
            }
            //获取cda document数据
            List<ArchiveTemplate> archiveTemplates = archiveTemplateService.findByCdaDocumentId(Arrays.asList(cdaDocumentIdList));
            archiveTemplates.forEach(item -> {
                Map<String, Object> temp = new HashMap<>();
                temp.put("cda_version", cdaVersion);
                temp.put("cda_document_id", item.getCdaDocumentId());
                temp.put("cda_document_name", item.getCdaDocumentName());
                temp.put("data_sets", dataMap.get(item.getCdaDocumentId()));
                temp.put("event_type", event.get("event_type"));
                temp.put("patient_id", event.get("patient_id"));
                temp.put("org_code", event.get("org_code"));
                temp.put("org_name", event.get("org_name"));
                temp.put("event_no", event.get("event_no"));
                temp.put("event_date", event.get("event_date"));
                result.put(item.getCdaDocumentId(), temp);
            });
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
        return new HashMap<>();
    }

}
