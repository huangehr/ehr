package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.profile.service.template.ArchiveTemplateService;
import com.yihu.ehr.util.rest.Envelop;
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
     *
     * 因为数据格式的问题，请注意次接口的注意事项
     * 1.没有子项目的模板用 multi = false，详细数据存放在在data字段，如门诊摘要、住院摘要之类的
     * 2.有子项目的模板用 multi = true，详细数据用列表的形式存放在records字段，如检查报告、检验报告，并用mark字段提供关联的主键信息
     * @param profileId
     * @param templateName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getCDAClass (String profileId, String templateName) throws Exception {
        String _profileId = profileId;
        if (profileId.split("_")[0].length() == 1) {
            profileId = profileId.substring(profileId.indexOf("_") + 1);
        }
        //根据profileId或者eventNo获取主记录
        Envelop envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1, null);
        List<Map<String, Object>> dataList = envelop.getDetailModelList();
        List<Map<String, Object>> result = new ArrayList<>();
        if (dataList.size() > 0) {
            Map<String, Object> event = dataList.get(0);
            if (event.get(ResourceCells.PROFILE_TYPE).equals("0") || event.get(ResourceCells.PROFILE_TYPE).equals("1")) { //profileType = 0 为之前数据存储错误的问题，已更改过来
                //根据机构获取定制模板
                List<ArchiveTemplate> list;
                if (StringUtils.isEmpty(templateName)) {
                    //pc接口
                    list = archiveTemplateService.search("type=universal," + ArchiveTemplate.Type.values()[new Integer(event.get(ResourceCells.EVENT_TYPE).toString())]
                            + ";cdaVersion=" + event.get(ResourceCells.CDA_VERSION));
                } else {
                    //mobile接口
                    list = archiveTemplateService.search("type=universal," + ArchiveTemplate.Type.values()[new Integer(event.get(ResourceCells.EVENT_TYPE).toString())]
                            + ";cdaVersion=" + event.get(ResourceCells.CDA_VERSION)
                            + ";title?" + templateName);
                }
                //遍历模板
                List<Map<String, Object>> prescription = new ArrayList<>();
                for (ArchiveTemplate item : list) {
                    Map<String, Object> temp = new HashMap<>();
                    if (item.getTitle().contains("检查报告")) {
                        String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_79$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        List<Map<String, Object>> data = new ArrayList<>();
                        subList.forEach(item2 -> {
                            Map<String, Object> dataMap = new HashMap<>();
                            String item2Name = (String) item2.get("EHR_002883"); //检查名称
                            dataMap.put(ResourceCells.PROFILE_ID, item2.get(ResourceCells.ROWKEY));
                            dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
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
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        List<Map<String, Object>> data = new ArrayList<>();
                        subList.forEach(item2 -> {
                            Map<String, Object> dataMap = new HashMap<>();
                            String item2Name = (String) item2.get("EHR_000352"); //检验项目
                            dataMap.put(ResourceCells.PROFILE_ID, item2.get(ResourceCells.ROWKEY));
                            dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
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
                    } else if (item.getTitle().contains("手术记录")) {
                        String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_06$*\"}";
                        Envelop subEnvelop = resource.getSubData(subQ, 1, 1000, null);
                        List<Map<String, Object>> subList = subEnvelop.getDetailModelList();
                        List<Map<String, Object>> data = new ArrayList<>();
                        subList.forEach(item2 -> {
                            Map<String, Object> dataMap = new HashMap<>();
                            String item2Name = (String) item2.get("EHR_000418"); //手术名称
                            dataMap.put(ResourceCells.PROFILE_ID, item2.get(ResourceCells.ROWKEY));
                            dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
                            dataMap.put("cda_document_id", item.getCdaDocumentId());
                            dataMap.put("cda_code", item.getCdaCode());
                            dataMap.put("pc_template", item.getPcUrl());
                            dataMap.put("mobile_template", item.getMobileUrl());
                            dataMap.put("template_id", item.getId());
                            dataMap.put("name", item2Name);
                            dataMap.put("mark", item2.get("EHR_000423")); //手术申请单号
                            data.add(dataMap);
                        });
                        temp.put("template_name", item.getTitle());
                        temp.put("multi", true);
                        temp.put("data", new HashMap<>());
                        temp.put("records", data);
                    } else if (item.getTitle().contains("处方")) {
                        //中西药特殊处理
                        if (item.getTitle().contains("中药")) {
                            String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_83$*\"}";
                            Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                            if (subEnvelop.getDetailModelList() != null && subEnvelop.getDetailModelList().size() > 0) {
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put(ResourceCells.PROFILE_ID, profileId);
                                dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
                                dataMap.put("cda_document_id", item.getCdaDocumentId());
                                dataMap.put("cda_code", item.getCdaCode());
                                dataMap.put("pc_template", item.getPcUrl());
                                dataMap.put("mobile_template", item.getMobileUrl());
                                dataMap.put("template_id", item.getId());
                                dataMap.put("name", "中药处方");
                                dataMap.put("mark", "01");
                                prescription.add(dataMap);
                            }
                        } else if (item.getTitle().contains("西药")) {
                            String subQ = "{\"q\":\"rowkey:" + profileId + "$HDSD00_84$*\"}";
                            Envelop subEnvelop = resource.getSubData(subQ, 1, 1, null);
                            if (subEnvelop.getDetailModelList() != null && subEnvelop.getDetailModelList().size() > 0) {
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put(ResourceCells.PROFILE_ID, profileId);
                                dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
                                dataMap.put("cda_document_id", item.getCdaDocumentId());
                                dataMap.put("cda_code", item.getCdaCode());
                                dataMap.put("pc_template", item.getPcUrl());
                                dataMap.put("mobile_template", item.getMobileUrl());
                                dataMap.put("template_id", item.getId());
                                dataMap.put("name", "西药处方");
                                dataMap.put("mark", "02");
                                prescription.add(dataMap);
                            }
                        }
                        continue;
                    } else {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put(ResourceCells.PROFILE_ID, profileId);
                        dataMap.put(ResourceCells.PROFILE_TYPE, event.get(ResourceCells.PROFILE_TYPE));
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
                }
                if (prescription.size() > 0) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("template_name", "门诊处方");
                    temp.put("multi", true);
                    temp.put("data", new HashMap<>());
                    temp.put("records", prescription);
                    result.add(temp);
                }
            }
        }
        if (_profileId.split("_")[0].length() == 1) {
            envelop = resource.healthFile("_id=" + _profileId, null, 1, 1);
            dataList = envelop.getDetailModelList();
            if (dataList.size() > 0) {
                Map<String, Object> obj = dataList.get(0);
                List<Map<String, String>> fileList = obj.get("file_list") != null ? (List) obj.get("file_list") : new ArrayList<>();
                Map<String, List<Map<String, Object>>> classify = new HashMap<>();
                fileList.forEach(item -> {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(ResourceCells.PROFILE_ID, obj.get(ResourceCells.ROWKEY));
                    dataMap.put(ResourceCells.PROFILE_TYPE, obj.get(ResourceCells.PROFILE_TYPE));
                    dataMap.put("cda_code", item.get("cda_document_id"));
                    dataMap.put("name", item.get("emr_name"));
                    dataMap.put("mark", "files");
                    dataMap.put("mime", item.get("mime"));
                    dataMap.put("url", item.get("url"));
                    String cdaDocumentName = item.get("cda_document_name") == null ? "CDA文档(" + item.get("cda_document_id") + ")" : item.get("cda_document_name");
                    if (classify.containsKey(cdaDocumentName)) {
                        classify.get(cdaDocumentName).add(dataMap);
                    } else {
                        List<Map<String, Object>> data = new ArrayList<>();
                        data.add(dataMap);
                        classify.put(cdaDocumentName, data);
                    }
                });
                classify.forEach((key, val) -> {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("template_name", key);
                    temp.put("multi", true);
                    temp.put("data", new HashMap<>());
                    temp.put("records", val);
                    result.add(temp);
                });
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
        //标准包档案的主键以机构编码开头
        Map<String, Object> result = new HashMap<>();
        if (profileId.split("_")[0].length() > 1) {
            final boolean is_multi = profileId.contains("$") ? true : false;
            Envelop envelop;
            if (is_multi) {
                envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId.split("\\$")[0] + "\"}", 1, 1, null);
            } else {
                envelop = resource.getMasterData("{\"q\":\"rowkey:" + profileId + "\"}", 1, 1, null);
            }
            List<Map<String, Object>> eventList = envelop.getDetailModelList();
            if (eventList != null && eventList.size() > 0) {
                Map<String, Object> event = eventList.get(0);
                String cdaVersion = String.valueOf(event.get(ResourceCells.CDA_VERSION));
                Map<String, Object> dataMap = new HashMap<>();
                Map<String, List<MCdaDataSet>> cdaDataSetMap = cdaService.getCDADataSetByCDAIdList(cdaVersion, cdaDocumentIdList);
                if (transform) {
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
                            String[] combination = profileId.split("\\$");
                            String subQ = "{\"q\":\"profile_id:" + combination[0] + "\"}";
                            Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                            List<Map<String, Object>> subList = subData.getDetailModelList();
                            subList.forEach(item -> {
                                item.put(ResourceCells.ORG_AREA, event.get(ResourceCells.ORG_AREA));
                                item.put(ResourceCells.ORG_NAME, event.get(ResourceCells.ORG_NAME));
                                item.put(ResourceCells.EVENT_DATE, event.get(ResourceCells.EVENT_DATE));
                                String dataSetCode = String.valueOf(item.get(ResourceCells.ROWKEY)).split("\\$")[1];
                                if (combination[1].equals(dataSetCode)) {
                                    if (profileId.equals(item.get(ResourceCells.ROWKEY))) {
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
                                if (combination[1].equals("HDSD00_77") && dataSetCode.equals("HDSD00_76") && !StringUtils.isEmpty(mark)) {
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
                                    if (mark.equals(item.get("EHR_006339"))) { //报告单号
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
                                item.put(ResourceCells.ORG_AREA, event.get(ResourceCells.ORG_AREA));
                                item.put(ResourceCells.ORG_NAME, event.get(ResourceCells.ORG_NAME));
                                item.put(ResourceCells.EVENT_DATE, event.get(ResourceCells.EVENT_DATE));
                                String dataSetCode = String.valueOf(item.get(ResourceCells.ROWKEY)).split("\\$")[1];
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
                } else {
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
                            String[] combination = profileId.split("\\$");
                            String subQ = "{\"q\":\"profile_id:" + combination[0] + "\"}";
                            Envelop subData = resource.getSubData(subQ, 1, 2000, null);
                            List<Map<String, Object>> subList = subData.getDetailModelList();
                            subList.forEach(item -> {
                                item.put(ResourceCells.ORG_AREA, event.get(ResourceCells.ORG_AREA));
                                item.put(ResourceCells.ORG_NAME, event.get(ResourceCells.ORG_NAME));
                                item.put(ResourceCells.EVENT_DATE, event.get(ResourceCells.EVENT_DATE));
                                String dataSetCode = String.valueOf(item.get(ResourceCells.ROWKEY)).split("\\$")[1];
                                if (combination[1].equals(dataSetCode)) {
                                    if (profileId.equals(item.get(ResourceCells.ROWKEY))) {
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
                                    return;
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
                                item.put(ResourceCells.ORG_AREA, event.get(ResourceCells.ORG_AREA));
                                item.put(ResourceCells.ORG_NAME, event.get(ResourceCells.ORG_NAME));
                                item.put(ResourceCells.EVENT_DATE, event.get(ResourceCells.EVENT_DATE));
                                String dataSetCode = String.valueOf(item.get(ResourceCells.ROWKEY)).split("\\$")[1];
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
                return result;
            }
        } else {
            Envelop envelop = resource.healthFile("_id=" + profileId, null, 1, 1);
            List<Map<String, Object>> dataList = envelop.getDetailModelList();
            if (dataList.size() > 0) {
                Map<String, Object> event = dataList.get(0);
                result.put(cdaDocumentIdList[0], event);
                return result;
            }
        }
        return result;
    }

}
