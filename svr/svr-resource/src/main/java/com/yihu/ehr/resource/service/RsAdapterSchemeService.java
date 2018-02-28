package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAdapterDictionaryDao;
import com.yihu.ehr.resource.dao.RsAdapterMetadataDao;
import com.yihu.ehr.resource.dao.RsAdapterSchemeDao;
import com.yihu.ehr.resource.feign.StandClient;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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
public class RsAdapterSchemeService extends BaseJpaService<RsAdapterScheme, RsAdapterSchemeDao> {

    @Autowired
    private RsAdapterSchemeDao schemeDao;

    @Autowired
    private RsAdapterMetadataDao metadataDao;

    @Autowired
    private RsAdapterDictionaryDao adapterDictionaryDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    @Autowired
    private StandClient standClient;

    /**
     * 保存适配方案
     *
     * @param adapterscheme RsAdapterscheme 适配方案
     * @return RsAdapterscheme 适配方案
     */
    public RsAdapterScheme saveAdapterScheme(RsAdapterScheme adapterscheme) throws Exception {
        String type = adapterscheme.getType();
        if ("1".equals(type)) {
            //平台标准
            if (schemeDao.findOne(adapterscheme.getId()) == null) {
                batchAdapterMetadata(adapterscheme);
                batchAdapterDictionary(adapterscheme);
            }
        } else if ("2".equals(type)) {
            //第三方标准
            if (schemeDao.findOne(adapterscheme.getId()) == null) {
                batchAdapterOrgMetaData(adapterscheme);
                batchAdapterOrgDictionary(adapterscheme);
            }
        }
        return schemeDao.save(adapterscheme);
    }


    //第三方标准字典适配
    public void batchAdapterOrgDictionary(RsAdapterScheme adapterscheme) throws Exception {
        String sql = "INSERT INTO rs_adapter_dictionary ( " +
                " scheme_id, " +
                " src_dict_code, " +
                " src_dict_entry_code, " +
                " src_dict_entry_name " +
                ") SELECT " +
                " '"+adapterscheme.getId()+"', " +
                " osd. CODE, " +
                " osde. CODE, " +
                " osde. NAME " +
                "VALUE " +
                " " +
                "FROM " +
                " org_std_dictentry osde " +
                "LEFT JOIN  " +
                " org_std_dict osd " +
                "ON osde.org_dict = osd.sequence  and osde.organization =osd.organization " +
                "WHERE osde.organization = '"+adapterscheme.getAdapterVersion()+"'";
        jdbcTemplate.execute(sql);
    }


    //第三方标准数据元适配
    public void batchAdapterOrgMetaData(RsAdapterScheme adapterscheme) throws Exception {
        String sql = "INSERT INTO rs_adapter_metadata (" +
                " scheme_id, " +
                " src_dataset_code, " +
                " src_metadata_code, " +
                " src_metadata_name " +
                ") SELECT " +
                " '"+adapterscheme.getId()+"', " +
                " osd. CODE, " +
                " osm. CODE, " +
                " osm. NAME " +
                "FROM " +
                " org_std_metadata osm " +
                " LEFT JOIN " +
                " org_std_dataset osd " +
                "ON " +
                " osm.org_dataset = osd.sequence  and osm.organization =osd.organization " +
                "WHERE osm.organization = '"+adapterscheme.getAdapterVersion()+"'";
        jdbcTemplate.execute(sql);
    }


    //平台标准字典适配
    public void batchAdapterDictionary(RsAdapterScheme adapterscheme) throws Exception {
        List<Map<String, Object>> ehrAdapterDict = standClient.getEhrAdapterDict(adapterscheme.getAdapterVersion());
        if(ehrAdapterDict!=null && ehrAdapterDict.size()>0){
            List<RsAdapterDictionary> rsAdapterDitcs = new ArrayList<>();
            String schemeId = adapterscheme.getId();
            for (Map<String,Object> map :ehrAdapterDict){
                RsAdapterDictionary rsAdapterDictionary = new RsAdapterDictionary();
                rsAdapterDictionary.setSchemeId(schemeId);
                rsAdapterDictionary.setSrcDictCode(map.get("dictCode")==null ? "": map.get("dictCode").toString());
                rsAdapterDictionary.setSrcDictEntryCode(map.get("dictEntryCode")==null ? "": map.get("dictEntryCode").toString());
                rsAdapterDictionary.setSrcDictEntryName(map.get("dictEntryName")==null ? "": map.get("dictEntryName").toString());
                rsAdapterDitcs.add(rsAdapterDictionary);
            }
            adapterDictionaryDao.save(rsAdapterDitcs);
        }
    }

    //平台标准数据元适配
    public void batchAdapterMetadata(RsAdapterScheme adapterscheme) throws Exception {
        List<Map<String, Object>> ehrAdapterMetadata = standClient.getEhrAdapterMetadata(adapterscheme.getAdapterVersion());
        if(ehrAdapterMetadata!=null && ehrAdapterMetadata.size()>0){
            List<RsAdapterMetadata> rsAdapterMetadatas = new ArrayList<>();
            String schemeId = adapterscheme.getId();
            for (Map<String,Object> map :ehrAdapterMetadata){
                RsAdapterMetadata rsAdapterMetadata = new RsAdapterMetadata();
                rsAdapterMetadata.setSchemeId(schemeId);
                rsAdapterMetadata.setSrcDatasetCode(map.get("dataSetCode")==null ? "": map.get("dataSetCode").toString());
                rsAdapterMetadata.setSrcMetadataCode(map.get("metadaCode")==null ? "": map.get("metadaCode").toString());
                rsAdapterMetadata.setSrcMetadataName(map.get("metadaName")==null ? "": map.get("metadaName").toString());
                rsAdapterMetadatas.add(rsAdapterMetadata);
            }
            metadataDao.save(rsAdapterMetadatas);
        }
    }



    /**
     * 删除适配方案
     *
     * @param ids String 适配方案ID
     */
    @Transactional
    public void deleteAdapterScheme(String ids) {
        String[] idsArray = ids.split(",");

        for (String id_ : idsArray) {
            metadataDao.deleteBySchemeId(id_);
            schemeDao.delete(id_);
        }
    }

    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page  int 分页
     * @param size  int 分页大小
     * @return Page<RsAdapterscheme>
     */
    public Page<RsAdapterScheme> getAdapterScheme(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return schemeDao.findAll(pageable);
    }


    /**
     * 获取适配方案
     *
     * @param id String Id
     * @return RsAdapterscheme
     */
    public RsAdapterScheme getAdapterSchemeById(String id) {
        return schemeDao.findOne(id);
    }

    public void deleteById(String id) {
        schemeDao.delete(id);
        metadataDao.deleteBySchemeId(id);
        adapterDictionaryDao.deleteBySchemeId(id);
    }
}
