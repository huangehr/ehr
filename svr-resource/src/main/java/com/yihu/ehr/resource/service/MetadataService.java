package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.RsMetadataDao;
import com.yihu.ehr.resource.model.RsMetadata;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据源服务
 *
 * Created by lyr on 2016/5/16.
 */
@Service
@Transactional
public class MetadataService extends BaseJpaService<RsMetadata,ResourceMetadataDao> {

    @Autowired
    private RsMetadataDao metadataDao;

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
    public List<RsMetadata> addMetaBatch(List<RsMetadata> metaLs)
    {
        StringBuilder sql = new StringBuilder("INSERT INTO rs_metadata(id, domain, name, std_code, dict_code, column_type, null_able, description, valid) VALUES ") ;
        RsMetadata rsMetadata ;
        SQLQuery query;
        int total = 0;
        for(int i=0; i<metaLs.size(); i++){
            rsMetadata = metaLs.get(i);
            sql.append("('"+ rsMetadata .getId() +"'");
            sql.append(",'"+ rsMetadata .getDomain() +"'");
            sql.append(",'"+ rsMetadata .getName() +"'");
            sql.append(",'"+ rsMetadata .getStdCode() +"'");
            sql.append(",'"+ rsMetadata .getDictCode() +"'");
            sql.append(",'"+ rsMetadata .getColumnType() +"'");
            sql.append(",'"+ rsMetadata .getNullAble() +"'");
            sql.append(",'"+ rsMetadata .getDescription() +"'");
            sql.append(",'1')");

            if(i%100==0 || i+1 == metaLs.size()){
                query = currentSession().createSQLQuery(sql.toString());
                total += query.executeUpdate();
                sql = new StringBuilder("INSERT INTO rs_metadata(id, domain, name, std_code, dict_code, column_type, null_able, description, valid) VALUES ") ;
            }else
                sql.append(",");

        }
        return metaLs;
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
    public List idExist(String ids)
    {
        String sql = "SELECT id FROM rs_metadata WHERE id in(:ids)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("ids", ids.split(","));
        return sqlQuery.list();
    }
}
