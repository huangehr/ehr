package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.schema.ResourceMetadataSchema;
import com.yihu.ehr.resource.dao.RsResourceMetadataDao;
import com.yihu.ehr.resource.dao.RsMetadataDao;
import com.yihu.ehr.resource.model.RsMetadata;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据源服务
 *
 * Created by lyr on 2016/5/16.
 */
@Service
@Transactional
public class RsMetadataService extends BaseJpaService<RsMetadata, RsResourceMetadataDao> {

    @Autowired
    private RsMetadataDao metadataDao;

    @Autowired
    private ResourceMetadataSchema keySchema;


    /**
     * 删除数据元
     *
     * @param id String 数据元ID
     */
    public void deleteMetadata(String id)
    {
        String[] ids = id.split(",");

        for(String id_ : ids)
        {
            RsMetadata metadata = metadataDao.findOne(id_);
            metadata.setValid("0");
            metadataDao.save(metadata);
        }
    }

    /**
     * 创建数据元
     *
     * @param metadata RsMetadata 数据元
     * @return RsMetadata 数据元
     */
    public RsMetadata saveMetadata(RsMetadata metadata){
        return metadataDao.save(metadata);
    }

    /**
     * 批量创建数据元
     *
     * @param metadataArray RsMetadata[]
     * @return List<RsMetadata>
     */
    public List<RsMetadata> saveMetadataBatch(RsMetadata[] metadataArray)
    {
        List<RsMetadata>  metadataList = new ArrayList<RsMetadata>();

        for(RsMetadata metadata : metadataArray)
        {
            metadataList.add(metadataDao.save(metadata));
        }

        return metadataList;
    }

    /**
     * 获取数据元
     *
     * @param id String Id
     * @return RsMetadata
     */
    public RsMetadata getMetadataById(String id)
    {
        return metadataDao.findOne(id);
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsMetadata> getMetadata(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return metadataDao.findAll(pageable);
    }

    /**
     * 批量创建数据元
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addMetaBatch(List<Map<String, Object>> metaLs)
    {
        String header = "INSERT INTO rs_metadata(id, domain, name, std_code, dict_code, column_type, null_able, description, dict_id, valid) VALUES \n";
        StringBuilder sql = new StringBuilder(header) ;
        Map<String, Object> map;
        SQLQuery query;
        int total = 0;
        for(int i=1; i<=metaLs.size(); i++){
            map = metaLs.get(i-1);
            sql.append("('"+ map .get("id") +"'");
            sql.append(",'"+ map .get("domain") +"'");
            sql.append(",'"+ map .get("name") +"'");
            sql.append(",'"+ map .get("stdCode") +"'");
            sql.append(",'"+ null2Space(map .get("dictCode")) +"'");
            sql.append(",'"+ map .get("columnType") +"'");
            sql.append(",'"+ map .get("nullAble") +"'");
            sql.append(",'"+ map .get("description") +"'");
            sql.append(","+ map.get("dictId") );
            sql.append(",'1')\n");

            if(i%100==0 || i == metaLs.size()){
                query = currentSession().createSQLQuery(sql.toString());
                total += query.executeUpdate();
                sql = new StringBuilder(header) ;
            }else
                sql.append(",");
        }
        return true;
    }

    private Object null2Space(Object o){
        return o==null? "" : o;
    }
    /**
     * 查询内部编码是否已存在， 返回已存在内部编码
     */
    public List stdCodeExist(String stdCodes)
    {
        String sql = "SELECT std_code FROM rs_metadata WHERE std_code in(:stdCodes) AND valid<>0";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("stdCodes", stdCodes.split(","));
        return sqlQuery.list();
    }

    /**
     * 查询资源标准编码是否已存在， 返回已存在资源标准编码
     */
    public List idExist( String[] ids)
    {
        String sql = "SELECT id FROM rs_metadata WHERE id in(:ids)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("ids", ids);
        return sqlQuery.list();
    }

    /**
     * 数据元缓存
     */
    public void metadataCache()
    {
        Iterable<RsMetadata> metadatas = metadataDao.findMetadataExistDictCode();

        for(RsMetadata meta : metadatas)
        {
            if(StringUtils.isEmpty(meta.getDictCode().trim()))
            {
                continue;
            }

            keySchema.set(meta.getId(),meta.getDictCode());
        }
    }

    public int getMaxIdNumber() {

        String sql = "SELECT MAX(CONVERT(case when ID is not null  then substring(ID,5) else '0' end ,SIGNED)) from rs_metadata";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        List list = sqlQuery.list();
        if(list != null && list.size() > 0){
            return Integer.valueOf(list.get(0).toString());
        }else{
            return 0;
        }
    }
}
