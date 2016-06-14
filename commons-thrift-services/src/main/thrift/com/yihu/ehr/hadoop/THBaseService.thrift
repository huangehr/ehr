/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.hadoop

include "THBaseTable.thrift"

/**
* HBase 管理服务
**/
service THBaseService{
	/**
	* 获取所有HBase中的表及其信息。
	**/
	list<THBaseTable.THBaseTable> getTables(1:bool withMetaData);

	/**
	* 获取指定表
	**/
	THBaseTable.THBaseTable getTable(1:string tableName);

	/**
	* 按数据集创建表。需要传入版本ID。
	**/
	bool createTableByDataSet(1:string versionId);

	/**
	* 删除指定表数据,all 会删除所有表的数据。
	**/
	bool trucateTable(1:string tableName);
}