package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.dao.intf.RsAdapterDictionaryDao;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 适配方案服务
 * <p/>
 * Created by lyr on 2016/5/17.
 */
@Service
@Transactional
public class AdapterSchemeService extends BaseJpaService<RsAdapterScheme, AdapterSchemeDao> {

    @Autowired
    private AdapterSchemeDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

    @Autowired
    private RsAdapterDictionaryDao adapterDictionaryDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${deploy.region}")
    Short deployRegion = 3502;

    /**
     * 保存适配方案
     *
     * @param adapterSchema RsAdapterSchema 适配方案
     * @return RsAdapterSchema 适配方案
     */
    public RsAdapterScheme saveAdapterSchema(RsAdapterScheme adapterSchema) throws Exception {
        String type = adapterSchema.getType();
        if ("1".equals(type)) {
            //平台标准
            if (schemaDao.findOne(adapterSchema.getId()) == null) {
                batchAdapterMetadata(adapterSchema);
                batchAdapterDictionary(adapterSchema);
            }
        } else if ("2".equals(type)) {
            //第三方标准
            if (schemaDao.findOne(adapterSchema.getId()) == null) {
                batchAdapterOrgMetaData(adapterSchema);
                batchAdapterOrgDictionary(adapterSchema);
            }
        }
        return schemaDao.save(adapterSchema);
    }


    //第三方标准字典适配
    public void batchAdapterOrgDictionary(RsAdapterScheme adapterSchema) throws Exception {
        String sql = "INSERT INTO rs_adapter_dictionary ( " +
                " scheme_id, " +
                " src_dict_code, " +
                " src_dict_entry_code, " +
                " src_dict_entry_name " +
                ") SELECT " +
                " '"+adapterSchema.getId()+"', " +
                " osd. CODE, " +
                " osde. CODE, " +
                " osde. NAME " +
                "VALUE " +
                " " +
                "FROM " +
                " org_std_dict osd, " +
                " org_std_dictentry osde " +
                "WHERE " +
                " 1 = 1 " +
                "AND osde.org_dict = osd.id " +
                "AND osd.organization = '"+adapterSchema.getAdapterVersion()+"'";
        jdbcTemplate.execute(sql);
    }


    //第三方标准数据元适配
    public void batchAdapterOrgMetaData(RsAdapterScheme adapterSchema) throws Exception {
        String sql = "INSERT INTO rs_adapter_metadata (" +
                " schema_id, " +
                " src_dataset_code, " +
                " src_metadata_code, " +
                " src_metadata_name " +
                ") SELECT " +
                " '"+adapterSchema.getId()+"', " +
                " osd. CODE, " +
                " osm. CODE, " +
                " osm. NAME " +
                "FROM " +
                " org_std_metadata osm, " +
                " org_std_dataset osd " +
                "WHERE " +
                " 1 = 1 " +
                "AND osm.org_dataset = osd.id " +
                "AND osd.organization = '"+adapterSchema.getAdapterVersion()+"'";
        jdbcTemplate.execute(sql);
    }


    //平台标准字典适配
    public void batchAdapterDictionary(RsAdapterScheme adapterSchema) throws Exception {
        String sql ="INSERT INTO rs_adapter_dictionary ( " +
                " scheme_id, " +
                " src_dict_code, " +
                " src_dict_entry_code, " +
                " src_dict_entry_name " +
                ") SELECT " +
                " '"+adapterSchema.getId()+"', " +
                " sd. CODE, " +
                " sde. CODE, " +
                " sde. " +
                "VALUE " +
                " " +
                "FROM " +
                " std_dictionary_"+adapterSchema.getAdapterVersion()+" sd, " +
                " std_dictionary_entry_"+adapterSchema.getAdapterVersion()+" sde " +
                "WHERE " +
                " 1 = 1 " +
                "AND sde.dict_id = sd.id";
        jdbcTemplate.execute(sql);
    }

    //平台标准数据元适配
    public void batchAdapterMetadata(RsAdapterScheme adapterSchema) throws Exception {
        String sql = "INSERT INTO rs_adapter_metadata ( " +
                " schema_id,  " +
                " src_dataset_code,  " +
                " src_metadata_code,  " +
                " src_metadata_name  " +
                ") SELECT  " +
                " '"+adapterSchema.getId()+"',  " +
                " sds. CODE,  " +
                " smd.column_name,  " +
                " smd. NAME  " +
                "FROM  " +
                " std_meta_data_"+adapterSchema.getAdapterVersion()+" smd,  " +
                " std_data_set_"+adapterSchema.getAdapterVersion()+" sds  " +
                "WHERE  " +
                " 1 = 1  " +
                "AND smd.dataset_id = sds.id";
        jdbcTemplate.execute(sql);
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
    public Page<RsAdapterScheme> getAdapterSchema(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return schemaDao.findAll(pageable);
    }


    /**
     * 获取适配方案
     *
     * @param id String Id
     * @return RsAdapterSchema
     */
    public RsAdapterScheme getAdapterSchemaById(String id) {
        return schemaDao.findOne(id);
    }

    public void deleteById(String id) {
        schemaDao.delete(id);
        metadataDao.deleteBySchemaId(id);
        adapterDictionaryDao.deleteBySchemaId(id);
    }
}
