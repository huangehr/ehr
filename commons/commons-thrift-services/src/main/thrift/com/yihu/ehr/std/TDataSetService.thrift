/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TDataSet.thrift"

service TDataSetService {
	TDataSet.TDataSet getDataSet(1:string id)
}