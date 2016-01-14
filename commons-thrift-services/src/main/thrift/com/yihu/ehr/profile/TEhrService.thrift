namespace java com.yihu.ehr.profile

include "TEhrProfile.thrift"
include "TEhrDataSet.thrift"

service TEhrService{
	list<TEhrProfile.TEhrProfile> getProfiles(String demographicId, i64 since, i64 to, bool loadStdDataSet, bool loadOriginDataSet);
	/**
	* 加载一份健康档案。需要指定档案ID及要是否加载标准化或原始档案数据。
	**/
	TEhrProfile.TEhrProfile getProfiles(1:string profileId, 2:bool loadStdData, 3:bool loadOriData);

	TEhrDataSet.TEhrDataSet getDataSet(1:string dataSetCode);
}