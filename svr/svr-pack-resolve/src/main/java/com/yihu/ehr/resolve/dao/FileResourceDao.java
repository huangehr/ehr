package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.FileFamily;
import com.yihu.ehr.resolve.model.stage1.CdaDocument;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.util.FileTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author Sand
 * @created 2016.05.09 19:13
 */
@Repository
public class FileResourceDao {
    @Autowired
    private HBaseDao hbaseDao;

    public void save(ResourceBucket resBucket) throws Exception {
        if (resBucket.getProfileType() == ProfileType.File){
            TableBundle bundle = new TableBundle();
            Map<String, CdaDocument> cdaDocuments = resBucket.getCdaDocuments();
            for(String rowkey : cdaDocuments.keySet()) {
                bundle.addRows(rowkey);
            }
            hbaseDao.delete(ResourceCore.FileTable, bundle);
            for (String rowkey : cdaDocuments.keySet()){
                CdaDocument cdaDocument = cdaDocuments.get(rowkey);
                bundle.addValues(rowkey, FileFamily.Basic, FileTableUtil.getBasicFamilyCellMap(resBucket));
                bundle.addValues(rowkey, FileFamily.Data, FileTableUtil.getFileFamilyCellMap(cdaDocument));
            }
            hbaseDao.save(ResourceCore.FileTable, bundle);
        }
    }

    /**
    public Map<String, CdaDocument> findAll(String[] rowkeys) throws IOException, ParseException {
        Map<String, CdaDocument> cdaDocuments = new HashMap<>();
        TableBundle bundle = new TableBundle();
        bundle.addRows(rowkeys);
        Object results[] = hbaseDao.get(FileTableUtil.Table, bundle);
        for (Object object : results){
            ResultUtil result = new ResultUtil(object);
            CdaDocument cdaDocument = new CdaDocument();
            cdaDocument.setId(result.getCellValue(FileFamily.Resource, FileFamily.FileColumns.CdaDocumentId, ""));
            cdaDocument.setName(result.getCellValue(FileFamily.Resource, FileFamily.FileColumns.CdaDocumentName, ""));
            String list = result.getCellValue(FileFamily.Resource, FileFamily.FileColumns.FileList, "");
            ArrayNode root = (ArrayNode) ((ObjectMapper) SpringContext.getService(ObjectMapper.class)).readTree(list);
            for (int i = 0; i < root.size(); ++i){
                ObjectNode objectNode = (ObjectNode) root.get(i);
                OriginFile originFile = new OriginFile();
                originFile.setMime(objectNode.get("mime").asText());
                originFile.setExpireDate(DateTimeUtil.utcDateTimeParse(objectNode.get("expire_date").asText()));
                originFile.setOriginUrl(objectNode.get("origin_url").asText());
                String files = objectNode.get("files").asText();
                for (String file : files.split(";")){
                    String tokens[] = file.split(":");
                    originFile.addStorageUrl(tokens[0], tokens[1]);
                }
                cdaDocument.getOriginFiles().add(originFile);
            }
            cdaDocuments.put(result.getRowKey(), cdaDocument);
        }
        return cdaDocuments;
    }*/
}
