namespace java com.yihu.ehr.data.domain

include "TOrder.thrift"

/**
* 排序
**/
struct TSort {
	1:list<TOrder.TOrder> orders
}