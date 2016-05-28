package com.yihu.ehr.resource.service;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemaDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterSchema;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 适配方案服务
 *
 * Created by lyr on 2016/5/17.
 */
@Service
@Transactional
public class AdapterSchemaService extends BaseJpaService<RsAdapterSchema,AdapterSchemaDao> {

    @Autowired
    private AdapterSchemaDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

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
    public RsAdapterSchema saveAdapterSchema(RsAdapterSchema adapterSchema)
    {
        if(schemaDao.findOne(adapterSchema.getId()) == null)
        {
            String dsSql = "select * from std_data_set_" + adapterSchema.getAdapterVersion();
            List<Map<String,Object>> dsList = jdbcTemplate.queryForList(dsSql);

            for(Map<String,Object> ds : dsList)
            {
                String metadataSql = "select * from std_meta_data_" + adapterSchema.getAdapterVersion()
                        + " where dataset_id = '" + ds.get("id").toString() + "'";

                List<Map<String,Object>> metadataList = jdbcTemplate.queryForList(metadataSql);

                for(Map<String,Object> meta : metadataList)
                {
                    RsAdapterMetadata metadata = new RsAdapterMetadata();

                    metadata.setId(new ObjectId(deployRegion, BizObject.RsAdapterMetadata).toString());
                    metadata.setSchemaId(adapterSchema.getId());
                    metadata.setSrcDatasetCode(ds.get("code").toString());
                    metadata.setSrcMetadataCode(meta.get("column_name").toString());
                    metadata.setSrcMetadataName(meta.get("name").toString());

                    metadataDao.save(metadata);
                }
            }
        }

        return schemaDao.save(adapterSchema);
    }

    /**
     * 删除适配方案
     *
     * @param ids String 适配方案ID
     */
    @Transactional
    public void deleteAdapterSchema(String ids)
    {
        String[] idsArray = ids.split(",");

        for(String id_ : idsArray)
        {
            metadataDao.deleteBySchemaId(id_);
            schemaDao.delete(id_);
        }
    }

    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page int 分页
     * @param size int 分页大小
     * @return Page<RsAdapterSchema>
     */
    public Page<RsAdapterSchema> getAdapterSchema(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return schemaDao.findAll(pageable);
    }


    /**
     * 获取适配方案
     *
     * @param id String Id
     * @return RsAdapterSchema
     */
    public RsAdapterSchema getAdapterSchemaById(String id)
    {
        return schemaDao.findOne(id);
    }
}
