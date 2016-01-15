/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.resource

/**
* 平台资源管理服务，以Redis为中心的数据资源管理。文本数据资源已经作为配置服务的一部分管理。
**/
service TResourceService {
	/**
	* 更新机构数据。
	**/
	bool updateOrgData();

	/**
	* 更新标准化数据资源
	**/
	bool updateStdData();
}