package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.ResourceFamily;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.details.CdaDocument;
import com.yihu.ehr.resolve.util.FileTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;


/**
 * @author Sand
 * @created 2016.05.09 19:13
 */
@Repository
public class FileResourceDao {

    @Autowired
    private HBaseDao hbaseDao;

    public void save (OriginalPackage originalPackage) throws Exception {
        if (originalPackage.getProfileType() == ProfileType.File){
            FilePackage filePackage = (FilePackage) originalPackage;
            TableBundle bundle = new TableBundle();
            Map<String, CdaDocument> cdaDocuments = filePackage.getCdaDocuments();
            for (String rowkey : cdaDocuments.keySet()) {
                bundle.addRows(rowkey);
            }
            hbaseDao.delete(ResourceCore.RawFiles, bundle);
            for (String rowkey : cdaDocuments.keySet()){
                CdaDocument cdaDocument = cdaDocuments.get(rowkey);
                bundle.addValues(rowkey, ResourceFamily.Basic, FileTableUtil.getBasicFamilyCellMap(originalPackage));
                bundle.addValues(rowkey, ResourceFamily.Data, FileTableUtil.getFileFamilyCellMap(cdaDocument));
            }
            hbaseDao.save(ResourceCore.RawFiles, bundle);
        } else if (originalPackage.getProfileType() == ProfileType.Link){
            LinkPackage linkPackage = (LinkPackage) originalPackage;
            TableBundle bundle = new TableBundle();
            String rowKey = originalPackage.getId();
            bundle.addRows(rowKey);
            hbaseDao.delete(ResourceCore.RawFiles, bundle);
            bundle.addValues(rowKey, ResourceFamily.Basic, FileTableUtil.getBasicFamilyCellMap(originalPackage));
            bundle.addValues(rowKey, ResourceFamily.Data, FileTableUtil.getFileFamilyCellMap(linkPackage));
            hbaseDao.save(ResourceCore.RawFiles, bundle);
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
