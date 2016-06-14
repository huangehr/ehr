/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.browser

include "../profile/TEhrProfile.thrift"
include "../profile/TEhrDataSet.thrift"

service TPersonalProfileService {
	list<TEhrProfile.TEhrProfile> getProfiles(1:string demographicId, 2:i64 since, 3:i64 to)

	TEhrProfile.TEhrProfile loadDocumentByDataSet(1:string cdaVersion, 2:list<TEhrDataSet.TEhrDataSet> dataSets, 3:bool originData);
}