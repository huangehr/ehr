/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.std

include "TDictItem.thrift"

struct TDict {
	1:required string id
	2:required string code
	3:required string name

	9:optional list<TDictItem.TDictItem> items
}