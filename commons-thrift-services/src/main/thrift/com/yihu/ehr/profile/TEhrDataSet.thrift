namespace java com.yihu.ehr.profile

include "TEhrRecord.thrift"

struct TEhrDataSet {
	1: list<TEhrRecord.TEhrRecord> records;
}