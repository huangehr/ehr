package com.yihu.ehr.profile.persist.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.util.FileTableUtil;
import com.yihu.ehr.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.09 19:13
 */
@Service
public class FileRepository {
    @Autowired
    HBaseDao hbaseDao;

    public void save(StdProfile profile) throws IOException {
        if (profile.getProfileType() == ProfileType.File){
            TableBundle bundle = new TableBundle();
            FileProfile fileProfile = (FileProfile)profile;

            Map<String, CdaDocument> cdaDocuments = fileProfile.getDocuments();
            for (String rowkey : cdaDocuments.keySet()){
                CdaDocument cdaDocument = cdaDocuments.get(rowkey);
                bundle.addValues(rowkey, FileFamily.Basic, FileTableUtil.getBasicFamilyCellMap(fileProfile));
                bundle.addValues(rowkey, FileFamily.Files, FileTableUtil.getFileFamilyCellMap(cdaDocument));
            }

            hbaseDao.saveOrUpdate(FileTableUtil.Table, bundle);
        }
    }

    public Map<String, CdaDocument> findAll(String[] rowkeys) throws IOException, ParseException {
        Map<String, CdaDocument> cdaDocuments = new HashMap<>();
        TableBundle bundle = new TableBundle();
        bundle.addRows(rowkeys);

        Object results[] = hbaseDao.get(FileTableUtil.Table, bundle);
        for (Object object : results){
            ResultUtil result = new ResultUtil(object);
            CdaDocument cdaDocument = new CdaDocument();

            cdaDocument.setId(result.getCellValue(FileFamily.Files, FileFamily.FileColumns.CdaDocumentId, ""));
            cdaDocument.setName(result.getCellValue(FileFamily.Files, FileFamily.FileColumns.CdaDocumentName, ""));

            String list = result.getCellValue(FileFamily.Files, FileFamily.FileColumns.FileList, "");
            ArrayNode root = (ArrayNode) ((ObjectMapper) SpringContext.getService("objectMapper")).readTree(list);
            for (int i = 0; i < root.size(); ++i){
                ObjectNode objectNode = (ObjectNode) root.get(i);
                OriginFile originFile = new OriginFile();

                originFile.setMime(objectNode.get("mime").asText());
                originFile.setExpireDate(DateTimeUtils.utcDateTimeParse(objectNode.get("expire_date").asText()));
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
    }
}
