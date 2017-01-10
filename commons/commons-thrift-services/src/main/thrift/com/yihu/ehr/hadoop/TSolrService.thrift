/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.hadoop

/**
* Solr服务。
**/
service TSolrService {
	string query(1:string tableName, 2:string query);
}