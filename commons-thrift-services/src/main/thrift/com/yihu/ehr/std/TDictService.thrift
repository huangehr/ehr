/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TDict.thrift"
include "TDictItem.thrift"

service TDictService {
	TDict.TDict getDict(1:string id)
}