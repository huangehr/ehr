package com.yihu.quota.scheduler;

import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;

/**
 * SchedulerUtil
 *
 * @author 张进军
 * @date 2018/7/17 15:31
 */
class SchedulerUtil {

    /**
     * 从去重分组结果，收集细表的 rowkey
     *
     * @param rowkeyList        rowkey 容器
     * @param groupList         去重分组结果
     * @param q                 查询条件
     * @param fq                筛选条件
     * @param distinctFieldName 去重字段名
     * @param showFields        查询返回字段名数组
     * @throws Exception
     */
    static void collectRowkeyFromDistinctGroup(List<String> rowkeyList, SolrUtil solrUtil, List<Group> groupList,
                                               String q, String fq, String distinctFieldName, String[] showFields) throws Exception {
        if (groupList != null && rowkeyList.size() > 0) {
            // 查询空值时的筛选条件
            fq += " AND -" + distinctFieldName + ":*";
            for (Group group : groupList) {
                int groupChildCount = group.getResult().size();
                SolrDocument firstDoc = group.getResult().get(0);
                Object fieldValueObj = firstDoc.getFieldValue(distinctFieldName);
                if (fieldValueObj == null) {
                    // 记录中去重字段是空值时，每条空值新单独记录到ES
                    SolrDocumentList nullDocList = solrUtil.query(ResourceCore.SubTable, q, fq, null, 0, -1, showFields);
                    for (int i = 0; i < groupChildCount; i++) {
                        SolrDocument doc = nullDocList.get(i);
                        rowkeyList.add(doc.getFieldValue("rowkey").toString());
                    }
                } else {
                    rowkeyList.add(firstDoc.getFieldValue("rowkey").toString());
                }
            }
        }
    }

}
