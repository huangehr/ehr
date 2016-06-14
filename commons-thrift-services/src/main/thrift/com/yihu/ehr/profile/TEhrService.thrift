/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.profile

include "TEhrProfile.thrift"
include "TEhrDataSet.thrift"

service TEhrService{
	/**
	* 加载一份健康档案。需要指定档案ID及要是否加载标准化或原始档案数据。
	**/
	TEhrProfile.TEhrProfile getProfile(1:string profileId, 2:bool loadStdData, 3:bool loadOriData)

	/**
	* 获取数据集
	**/
	TEhrDataSet.TEhrDataSet getDataSet(1:string dataSetCode);

	/**
	* 获取指定患者一段时间内的健康档案
	**/
	list<TEhrProfile.TEhrProfile> getProfiles(1:string demographicId, 2:i64 since, 3:i64 to, 4:bool loadStdDataSet, 5:bool loadOriDataSet)

	/**
	* 搜索患者的健康档案
	**/
	list<TEhrProfile.TEhrProfile> searchProfiles(1:map<string, string> criteria);
}