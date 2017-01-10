/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.patient.family

include "TFamily.thrift"

service TFamilyService {
	TFamily.TFamily getFamily(1:string id)
}