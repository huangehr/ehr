package com.yihu.quota.scheduler;

import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SchedulerUtil
 *
 * @author 张进军
 * @date 2018/7/17 15:31
 */
class SchedulerUtil {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerUtil.class);

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
        if (groupList != null && groupList.size() > 0) {
            // 查询空值时的筛选条件
            fq += " AND -" + distinctFieldName + ":*";
            for (Group group : groupList) {
                SolrDocument firstDoc = group.getResult().get(0);
                if (StringUtils.isEmpty(group.getGroupValue())) {
                    // 记录中去重字段是空值时，每条空值新单独记录到ES
                    long count = solrUtil.count(ResourceCore.SubTable, q, fq);
                    SolrDocumentList nullDocList = solrUtil.query(ResourceCore.SubTable, q, fq, null, 0, count, showFields);
                    for (int i = 0, size = nullDocList.size(); i < size; i++) {
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
