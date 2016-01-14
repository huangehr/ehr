namespace java com.yihu.ehr.profile

include "TEhrDataSet.thrift"

struct TEhrProfile {
	1: string id;
	2: string orgCode;
	3: string orgName;
	4: i64 eventDate;
	5: string summary;
	6: list<TEhrDataSet.TEhrDataSet> dataSets;
}
