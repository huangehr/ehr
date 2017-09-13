package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAdapterMetadataDao;
import com.yihu.ehr.resource.dao.RsAdapterSchemeDao;
import com.yihu.ehr.resource.dao.RsAdapterDictionaryDao;
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
public class RsAdapterSchemeService extends BaseJpaService<RsAdapterScheme, RsAdapterSchemeDao> {

    @Autowired
    private RsAdapterSchemeDao schemaDao;

    @Autowired
    private RsAdapterMetadataDao metadataDao;

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
                " org_std_dictentry osde " +
                "LEFT JOIN  " +
                " org_std_dict osd " +
                "ON osde.org_dict = osd.sequence  and osde.organization =osd.organization " +
                "WHERE osde.organization = '"+adapterSchema.getAdapterVersion()+"'";
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
                " org_std_metadata osm " +
                " LEFT JOIN " +
                " org_std_dataset osd " +
                "ON " +
                " osm.org_dataset = osd.sequence  and osm.organization =osd.organization " +
                "WHERE osm.organization = '"+adapterSchema.getAdapterVersion()+"'";
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
                " std_dictionary_entry_"+adapterSchema.getAdapterVersion()+" sde " +
                "LEFT JOIN" +
                " std_dictionary_"+adapterSchema.getAdapterVersion()+" sd " +
                "ON " +
                " sde.dict_id = sd.id where sd.code is not null ";
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
                " std_meta_data_"+adapterSchema.getAdapterVersion()+" smd  " +
                "LEFT JOIN" +
                " std_data_set_"+adapterSchema.getAdapterVersion()+" sds  " +
                "ON  " +
                " smd.dataset_id = sds.id";
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
