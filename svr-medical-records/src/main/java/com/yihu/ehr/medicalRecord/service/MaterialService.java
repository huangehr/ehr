package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.medicalRecord.family.DocumentFamily;
import com.yihu.ehr.medicalRecord.family.TextFamily;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
@Service
@Transactional
public class MaterialService extends HBaseUtil {

    public boolean checkTextMaterial(String creatorId,String businessClass,String content,String patientId){
        FilterList filterList = new FilterList();
        Filter vf = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("ROW2_QUAL1"));
        Filter vs = new SingleColumnValueFilter(Bytes.toBytes(TextFamily.Data),Bytes.toBytes(TextFamily.DataColumns.BusinessClass),CompareFilter.CompareOp.EQUAL, new SubstringComparator("1"));
        Scan scan = new Scan();
        scan.setFilter(vs);
        ResultScanner scanner = getScanner(TextFamily.TableName,scan);
        for(Result r:scanner) {
            for (KeyValue kv : r.raw()) {
                System.out.print(new String(kv.getRow()) + " ");
                System.out.print(new String(kv.getFamily()) + ":");
                System.out.print(new String(kv.getQualifier()) + " ");
                System.out.print(kv.getTimestamp() + " ");
                System.out.println(new String(kv.getValue()));
            }
        }
        return true;
    }

    public boolean uploadTextMaterial(String creatorId,String businessClass,String content,String patientId)throws Exception{
        if(creatorId.length()>0 && content.length()>0 ) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String[] value = {content, creatorId, timestamp.toString(), patientId, businessClass};
            String rowKey = "";
            if (patientId != null && patientId.length() > 0) {
                rowKey = creatorId + "_" + patientId + "_" + timestamp.toString();
            } else {
                rowKey = creatorId + "_" + creatorId + "_" + timestamp.toString();
            }

            insertRecord(TextFamily.TableName, rowKey, TextFamily.Data, TextFamily.getColumns(TextFamily.Data), value);
            return true;
        }
        else
            return false;
    }

    public List<String> getTextMaterial(String creatorId,String businessClass,String patientId){
        if(creatorId.length()>0) {
            String rowKey = "";
            if (patientId.length() > 0) {
                rowKey = creatorId + "_" + patientId;
            }
            else {
                rowKey = creatorId + "_" + creatorId;
            }
            List<String>list=new ArrayList<>();
            Scan scan = new Scan();
            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,new BinaryPrefixComparator(rowKey.getBytes()));
            scan.setFilter(filter);
            ResultScanner scanner = getScanner(TextFamily.TableName,scan);
            for (Result res : scanner) {
                byte[]bytes=res.getValue(Bytes.toBytes(TextFamily.Data), Bytes.toBytes(TextFamily.DataColumns.Content));
                list.add(bytes.toString());
            }
            return list;
        }
        else
            return null;

    }

    public boolean uploadImgMaterial(String documentName,String creatorId,String fileType,String dataFrom,String patientId,String path)throws Exception{
        if(creatorId.length()>0 && fileType.length()>0 &&path.length()>0) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String[] value = {documentName,timestamp.toString(),creatorId, patientId,null,fileType,path,dataFrom};
            String rowKey = "";
            if (patientId != null && patientId.length() > 0) {
                rowKey = creatorId + "_" + patientId + "_" + timestamp.toString();
            } else {
                rowKey = creatorId + "_" + creatorId + "_" + timestamp.toString();
            }

            insertRecord(DocumentFamily.TableName, rowKey, DocumentFamily.Data, DocumentFamily.getColumns(TextFamily.Data), value);
            return true;
        }
        else
            return false;
    }

    public List<String> getImgMaterial(String creatorId,String patientId)throws Exception{
        if(creatorId.length()>0) {
            String rowKey = "";
            if (patientId.length() > 0) {
                rowKey = creatorId + "_" + patientId;
            }
            else {
                rowKey = creatorId + "_" + creatorId;
            }
            List<String>list=new ArrayList<>();
            Scan scan = new Scan();
            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL,new BinaryPrefixComparator(rowKey.getBytes()));
            scan.setFilter(filter);
            ResultScanner scanner = getScanner(DocumentFamily.TableName,scan);
            for (Result res : scanner) {
                byte[]bytes=res.getValue(Bytes.toBytes(DocumentFamily.Data), Bytes.toBytes(DocumentFamily.DataColumns.FileUrl));
                list.add(bytes.toString());
            }
            return list;
        }
        else
            return null;
    }

}
