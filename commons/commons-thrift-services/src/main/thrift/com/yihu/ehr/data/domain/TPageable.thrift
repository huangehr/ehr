namespace java com.yihu.ehr.data.domain

include "TSort.thrift"

struct TPageable {
	1:i32 size
	2:i32 number
	3:TSort.TSort sort
}