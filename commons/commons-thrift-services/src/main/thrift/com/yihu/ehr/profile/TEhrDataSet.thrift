/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.profile

include "TEhrRecord.thrift"

struct TEhrDataSet {
	1: list<TEhrRecord.TEhrRecord> records;
}