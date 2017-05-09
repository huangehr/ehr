package com.yihu.ehr.service.resource.stage2.index;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.FileFamily;
import com.yihu.ehr.service.resource.stage1.CdaDocument;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.solr.SolrAdmin;
import com.yihu.ehr.util.FileTableUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @created 2017.05.08
 */
@Service
public class IndexService {
    @Autowired
    SolrAdmin solrAdmin;

    public void save(ResourceBucket bucket) throws Exception {

        List<String> dimensionList = new ArrayList<>();

        Map<String,Object> map = new HashedMap();

        //主记录数据
        Map<String,String> masterMap = bucket.getMasterRecord().getDataGroup();

        //遍历所有维度
        for(String dimension : dimensionList)
        {
            String col = "";//列名
            //判断是否主表字段
            if(true)
            {
                //if(masterMap.containsKey(col))
                //FileTableUtil.getBasicFamilyCellMap()
            }

        }

        solrAdmin.create(ResourceCore.IndexTable,map);
        /*if (resBucket.getProfileType() == ProfileType.File){
            TableBundle bundle = new TableBundle();

            Map<String, CdaDocument> cdaDocuments = resBucket.getCdaDocuments();

            for(String rowkey : cdaDocuments.keySet())
            {
                bundle.addRows(rowkey);
            }

            hbaseDao.delete(ResourceCore.FileTable,bundle);

            for (String rowkey : cdaDocuments.keySet()){
                CdaDocument cdaDocument = cdaDocuments.get(rowkey);
                bundle.addValues(rowkey, FileFamily.Basic, FileTableUtil.getBasicFamilyCellMap(resBucket));
                bundle.addValues(rowkey, FileFamily.Data, FileTableUtil.getFileFamilyCellMap(cdaDocument));
            }

            hbaseDao.save(ResourceCore.FileTable, bundle)
        }*/
    }


}
