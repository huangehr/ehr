namespace java com.yihu.ehr.browser

include "../profile/TEhrProfile.thrift"

service TPersonalProfileService {
	list<TEhrProfile.TEhrProfile> getProfiles(1:string demographicId, 2:i64 since, 3:i64 to);

	loadDocumentByDataSet(String cdaVersion, String dataSetList, bool originData);
}