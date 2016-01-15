/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TMetaData.thrift"

service TMetaDataService {
	TMetaData.TMetaData getMetaData(1:string id)
}