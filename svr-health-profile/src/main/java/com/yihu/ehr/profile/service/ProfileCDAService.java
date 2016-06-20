package com.yihu.ehr.profile.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSet;
import com.yihu.ehr.profile.config.CdaDocumentTypeOptions;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CDA相关接口服务
 * @author hzp 2016-06-16
 */
@Service
public class ProfileCDAService {
    @Autowired
    XResourceClient resource;

    //特殊字典信息服务
    @Autowired
    CD10Service dictService;

    //模板服务
    @Autowired
    XTemplateRepository templateRepository;

    @Autowired
    TemplateService templateService;

    //CDA服务
    @Autowired
    XCDADocumentClient cdaService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    XTransformClient transform;

    @Autowired
    CdaDocumentTypeOptions cdaDocumentTypeOptions;

    @Autowired
    HBaseDao hBaseDao;


    String appId = "svr-health-profile";


    /**
     * 获取某个CDA文档数据
     * @return
     */
    public Map<String, Object> getCDAData(String profileId, String cdaDocumentId) throws Exception
    {
        Map<String, Object> re = new HashMap<>();
        //主表记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"rowkey:" + profileId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            String cdaVersion = obj.get("cda_version").toString();
            String orgCode = obj.get("org_code").toString();

            //获取CDA关联数据集
            Map<String,List<Map<String, Object>>> datasetList = new HashMap<>();
            List<MCdaDataSet> CDADataset = cdaService.getCDADataSetByCDAId(cdaVersion, cdaDocumentId);
            if (CDADataset != null && CDADataset.size() > 0) {
                for (MCdaDataSet dataset : CDADataset) {
                    String datasetCode = dataset.getDataSetCode();
                    String multiRecord = dataset.getMultiRecord();

                    //单条数据
                    if(multiRecord.equals("0")){
                        List<Map<String, Object>> dataList = new ArrayList<>();
                        MStdTransformDto stdTransformDto = new MStdTransformDto();
                        stdTransformDto.setSource(objectMapper.writeValueAsString(obj));
                        stdTransformDto.setVersion(cdaVersion);
                        stdTransformDto.setDataset(datasetCode);
                        Map<String, Object> map = transform.stdMasterTransform(objectMapper.writeValueAsString(stdTransformDto));
                        dataList.add(map);
                        datasetList.put(datasetCode, dataList);
                    }
                    else{
                        //获取Hbase细表数据
                        String q = "{\"table\":\""+datasetCode+"\",\"q\":\"profile_id:" + profileId + "\"}";
                        Envelop data = resource.getEhrCenterSub(q, null, null);

                        if (data.getDetailModelList() != null && data.getDetailModelList().size() > 0) {
                            List<Map<String, Object>> table = data.getDetailModelList();

                            //根据cdaVersion转译
                            MStdTransformDto stdTransformDto = new MStdTransformDto();
                            stdTransformDto.setSource(objectMapper.writeValueAsString(table));
                            stdTransformDto.setVersion(cdaVersion);
                            List<Map<String, Object>> dataList = transform.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));
                            datasetList.put(datasetCode, dataList);
                        }
                    }
                }
            }

            //获取cda document数据
            MCDADocument cda = cdaService.getCDADocuments(cdaVersion,cdaDocumentId);
            re.put("cda_version",cdaVersion);
            re.put("cda_document_id",cdaDocumentId);
            re.put("cda_document_name",cda.getName());
            re.put("data_sets",datasetList);


            //是否非结构化档案
            if(obj.containsKey("profile_type") && obj.get("profile_type").toString().equals("2"))
            {
                Envelop rawFiles = resource.getRawFiles(profileId,null,null);
                if(rawFiles.getDetailModelList()!=null && rawFiles.getDetailModelList().size()>0)
                {
                    re.put("files",rawFiles.getDetailModelList());
                }
            }
        } else {
            throw new Exception("未查到相关记录！");
        }

        return re;
    }


    /**
     * 根据ProfileId查询CDA分类
     */
    public List<Map<String, Object>> getCDAClass(String profileId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();

        //根据profileId或者eventNo获取主记录
        String q = "{\"q\":\"rowkey:" + profileId + "\"}";

        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, q, null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            profileId = obj.get("rowkey").toString();
            String cdaVersion = obj.get("cda_version").toString();
            String orgCode = obj.get("org_code").toString();

            //根据机构获取定制模板
            List<Template> list = templateRepository.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
            //遍历模板
            if (list != null && list.size() > 0) {

                //获取档案相关数据
                List<String> datasetContains = new ArrayList<>();
                Envelop data = resource.getEhrCenterSub("{\"q\":\"profile_id:"+profileId+"\"}", null, null);
                if(data.getDetailModelList()!=null && data.getDetailModelList().size()>0)
                {
                    for(int i=0;i<data.getDetailModelList().size();i++)
                    {
                        Map<String,Object> map = (Map<String,Object>)data.getDetailModelList().get(i);
                        String dsCode = String.valueOf(map.get("rowkey")).split("\\$")[1];
                        if(!datasetContains.contains(dsCode))
                        {
                            datasetContains.add(dsCode);
                        }
                    }
                }

                for (Template template : list) {
                    String cdaDocumentId = template.getCdaDocumentId();
                    //获取CDA关联数据集
                    List<MCdaDataSet> datasetList = cdaService.getCDADataSetByCDAId(cdaVersion, cdaDocumentId);
                    if (datasetList != null && datasetList.size() > 0) {
                        for (MCdaDataSet dataset : datasetList) {
                            String dsCode = dataset.getDataSetCode();
                            if(datasetContains.contains(dsCode))
                            {
                                Map<String, Object> item = new HashMap<>();
                                item.put("profile_id", profileId);
                                item.put("template_id", String.valueOf(template.getId()));
                                item.put("cda_document_id", template.getCdaDocumentId());
                                item.put("template_name", template.getTitle());
                                item.put("pc_template", template.getPcTplURL());
                                item.put("mobile_template", template.getMobileTplURL());
                                re.add(item);
                                break;
                            }
                        }
                    }
                }
            }

            return re;
        } else {
            throw new Exception("未查到相关记录！");
        }
    }

    /**
     * 通过事件号获取CDADocumentId
     */
    public String getCDADocumentId(String eventNo, String cdaCode) throws Exception {
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"event_no:" + eventNo + "\"}", null, null);

        //是否有数据
        if (result.getDetailModelList() != null && result.getTotalCount() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            String orgCode = obj.get("org_code").toString();
            String cdaVersion = obj.get("cda_version").toString();
            //获取模板ID
            Template template = templateRepository.findByOrganizationCodeAndCdaVersionAndCdaCode(orgCode, cdaVersion, cdaCode);
            if (template != null) {
                return String.valueOf(template.getCdaDocumentId());
            }
        }

        throw new Exception("未找到相关模板！");
    }

    /**
     * 获取完整CDA档案
     */
    public Map<String,Object> getCDADocument(String profileId) throws Exception {
        Map<String,Object> re = new HashMap<>();
        re.put("profile_id",profileId);
        //主表记录
        Envelop profile = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"rowkey:" + profileId + "\"}", null, null);
        if (profile.getDetailModelList() != null && profile.getDetailModelList().size() > 0) {
            Map<String, Object> profileMap = (Map<String, Object>) profile.getDetailModelList().get(0);
            String version = profileMap.get("cda_version").toString();
            String orgCode = profileMap.get("org_code").toString();
            String eventType = profileMap.get("event_type").toString();

            //遍历所有CDA Document
            Map<Template, MCDADocument> CDAList = templateService.getOrganizationTemplates(orgCode,version,cdaDocumentTypeOptions.getCdaDocumentTypeId(eventType));
            List<Map<String, Object>> CDADataList = new ArrayList<>();
            for (MCDADocument cda : CDAList.values()) {
                String cdaDocumentId = cda.getId();
                Map<String, Object> CDAData = getCDAData(profileId,cdaDocumentId);
                CDADataList.add(CDAData);
            }
            re.put("cda_documents",CDADataList);
        }
        else{
            throw new Exception("未找到该CDA档案！（profile_id："+profileId+"）");
        }

        return re;
    }



}
