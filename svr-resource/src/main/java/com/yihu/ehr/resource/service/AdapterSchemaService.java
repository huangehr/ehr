package com.yihu.ehr.resource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.AdapterDictionaryQueryDao;
import com.yihu.ehr.resource.dao.AdapterMetadataQueryDao;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemaDao;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 适配方案服务
 * <p/>
 * Created by lyr on 2016/5/17.
 */
@Service
@Transactional
public class AdapterSchemaService extends BaseJpaService<RsAdapterSchema, AdapterSchemaDao> {

    @Autowired
    private AdapterSchemaDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AdapterMetadataQueryDao adapterMetadataQueryDao;

    @Autowired
    private AdapterDictionaryQueryDao adapterDictionaryQueryDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    /**
     * 保存适配方案
     *
     * @param adapterSchema RsAdapterSchema 适配方案
     * @return RsAdapterSchema 适配方案
     */
    public RsAdapterSchema saveAdapterSchema(RsAdapterSchema adapterSchema) throws Exception {
        String type = adapterSchema.getType();
        if ("1".equals(type)) {
            //平台标准
            if (schemaDao.findOne(adapterSchema.getId()) == null) {
                batchAdapterMetadata(adapterSchema);
                batchAdapterDictEntries(adapterSchema);
            }
        } else if ("2".equals(type)) {
            //第三方标准
            if (schemaDao.findOne(adapterSchema.getId()) == null) {
                batchAdapterOrgMetaData(adapterSchema);
                batchAdapterOrgDictEntries(adapterSchema);
            }
        }
        //return schemaDao.save(adapterSchema);
        return null;
    }


    //获取标准适配字典列表
    private List<RsAdapterDictionary> adapterDictionaryList(List<RsAdapterDictionary> adapterDictionaryList, String adapterSchemaId, String srcDictCode, String srcDictEntryCode, String srcDictEntryName) {
        RsAdapterDictionary rsAdapterDictionary = new RsAdapterDictionary();
        rsAdapterDictionary.setSchemeId(adapterSchemaId);
        rsAdapterDictionary.setSrcDictCode(srcDictCode);
        rsAdapterDictionary.setSrcDictEntryCode(srcDictEntryCode);
        rsAdapterDictionary.setSrcDictEntryName(srcDictEntryName);
        adapterDictionaryList.add(rsAdapterDictionary);
        return adapterDictionaryList;
    }

    //获取标准适配数据元列表
    private List<RsAdapterMetadata> adapterMetadataList(List<RsAdapterMetadata> adapterMetadataList, String adapterSchemaId, String srcDataSetCode, String srcMetadataCode, String srcMetadataName) {
        RsAdapterMetadata adapterMetadata = new RsAdapterMetadata();
        adapterMetadata.setSchemaId(adapterSchemaId);
        adapterMetadata.setSrcDatasetCode(srcDataSetCode);
        adapterMetadata.setSrcMetadataCode(srcMetadataCode);
        adapterMetadata.setSrcMetadataName(srcMetadataName);
        adapterMetadataList.add(adapterMetadata);
        return adapterMetadataList;
    }

    //第三方标准字典适配
    public void batchAdapterOrgDictEntries(RsAdapterSchema adapterSchema) throws Exception {
        String orgDataSetSql = "select * from org_std_dict where organization = '" + adapterSchema.getCode() + "'";
        List<Map<String, Object>> orgDictList = jdbcTemplate.queryForList(orgDataSetSql);
        List<RsAdapterDictionary> adapterDictionaryList = new ArrayList<>();
        for (Map<String, Object> orgDictMap : orgDictList) {
            String sdeSql = "select * from org_std_dictentry where org_dict = '" + orgDictMap.get("id").toString() + "'";

            List<Map<String, Object>> orgDictEntryList = jdbcTemplate.queryForList(sdeSql);


            for (Map<String, Object> dictionariesMap : orgDictEntryList) {
                adapterDictionaryList = adapterDictionaryList(adapterDictionaryList,
                        adapterSchema.getId(),
                        orgDictMap.get("code").toString(),
                        dictionariesMap.get("code").toString(),
                        dictionariesMap.get("name").toString());
            }
        }
        String jsonData = objectMapper.writeValueAsString(adapterDictionaryList);
        RsAdapterDictionary[] adapterDictionaryArray = objectMapper.readValue(jsonData, RsAdapterDictionary[].class);
        adapterDictionaryQueryDao.batchInsertAdapterDictionaries(adapterDictionaryArray);
    }


    //第三方标准数据元适配
    public void batchAdapterOrgMetaData(RsAdapterSchema adapterSchema) throws Exception {
        String orgDataSetSql = "select * from org_std_dataset where organization = '" + adapterSchema.getCode() + "'";
        List<Map<String, Object>> orgDataSetList = jdbcTemplate.queryForList(orgDataSetSql);
        List<RsAdapterMetadata> adapterMetadataList = new ArrayList<>();
        for (Map<String, Object> orgDataSetMap : orgDataSetList) {
            String sdeSql = "select * from org_std_meta_data where org_dataset = '" + orgDataSetMap.get("id").toString() + "'";

            List<Map<String, Object>> orgDataSet = jdbcTemplate.queryForList(sdeSql);


            for (Map<String, Object> metaDataMap : orgDataSet) {
                adapterMetadataList = adapterMetadataList(adapterMetadataList,
                        adapterSchema.getId(),
                        orgDataSetMap.get("code").toString(),
                        metaDataMap.get("code").toString(),
                        metaDataMap.get("name").toString());
            }
        }
        String jsonData = objectMapper.writeValueAsString(adapterMetadataList);
        RsAdapterMetadata[] adapterMetadataArray = objectMapper.readValue(jsonData, RsAdapterMetadata[].class);
        adapterMetadataQueryDao.batchAdapterMetadata(adapterMetadataArray);

    }


    //平台标准字典适配
    public void batchAdapterDictEntries(RsAdapterSchema adapterSchema) throws Exception {
        String sdSql = "select * from std_dictionary_" + adapterSchema.getAdapterVersion();
        List<Map<String, Object>> dictionaryList = jdbcTemplate.queryForList(sdSql);
        List<RsAdapterDictionary> adapterDictionaryList = new ArrayList<>();
        for (Map<String, Object> dictionaryMap : dictionaryList) {
            String dictionaryEntrySql = "select * from std_dictionary_entry_" + adapterSchema.getAdapterVersion()
                    + " where dict_id = '" + dictionaryMap.get("id").toString() + "'";
            List<Map<String, Object>> dictionaryEntriesMap = jdbcTemplate.queryForList(dictionaryEntrySql);

            for (Map<String, Object> dictionaryEntryMap : dictionaryEntriesMap) {
                adapterDictionaryList(adapterDictionaryList,
                        adapterSchema.getId(),
                        dictionaryMap.get("code").toString(),
                        dictionaryEntryMap.get("code").toString(),
                        dictionaryEntryMap.get("value").toString());
            }
        }
        String jsonData = objectMapper.writeValueAsString(adapterDictionaryList);
        RsAdapterDictionary[] adapterDictionaryArray = objectMapper.readValue(jsonData, RsAdapterDictionary[].class);
        adapterDictionaryQueryDao.batchInsertAdapterDictionaries(adapterDictionaryArray);
    }

    //平台标准数据元适配
    public void batchAdapterMetadata(RsAdapterSchema adapterSchema) throws Exception {
        String sdsSql = "select * from std_data_set_" + adapterSchema.getAdapterVersion();
        List<Map<String, Object>> dataSetList = jdbcTemplate.queryForList(sdsSql);
        List<RsAdapterMetadata> adapterMetadataList = new ArrayList<>();
        for (Map<String, Object> dataSetMap : dataSetList) {
            String metadataSql = "select * from std_meta_data_" + adapterSchema.getAdapterVersion()
                    + " where dataset_id = '" + dataSetMap.get("id").toString() + "'";
            List<Map<String, Object>> map = jdbcTemplate.queryForList(metadataSql);

            for (Map<String, Object> metaDataMap : map) {
                adapterMetadataList = adapterMetadataList(adapterMetadataList,
                        adapterSchema.getId(),
                        dataSetMap.get("code").toString(),
                        metaDataMap.get("column_name").toString(),
                        metaDataMap.get("column_name").toString());
            }
        }
        String jsonData = objectMapper.writeValueAsString(adapterMetadataList);
        RsAdapterMetadata[] adapterMetadataArray = objectMapper.readValue(jsonData, RsAdapterMetadata[].class);
        adapterMetadataQueryDao.batchAdapterMetadata(adapterMetadataArray);

    }


    /**
     * 删除适配方案
     *
     * @param ids String 适配方案ID
     */
    @Transactional
    public void deleteAdapterSchema(String ids) {
        String[] idsArray = ids.split(",");

        for (String id_ : idsArray) {
            metadataDao.deleteBySchemaId(id_);
            schemaDao.delete(id_);
        }
    }

    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page  int 分页
     * @param size  int 分页大小
     * @return Page<RsAdapterSchema>
     */
    public Page<RsAdapterSchema> getAdapterSchema(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return schemaDao.findAll(pageable);
    }


    /**
     * 获取适配方案
     *
     * @param id String Id
     * @return RsAdapterSchema
     */
    public RsAdapterSchema getAdapterSchemaById(String id) {
        return schemaDao.findOne(id);
    }
}
