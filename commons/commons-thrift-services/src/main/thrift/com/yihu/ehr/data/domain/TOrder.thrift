namespace java com.yihu.ehr.data.domain

/**
* 排序方向
**/
enum TDirection{
	ASC = 1,
	DESC = 2
}

/**
* 排序方向及排序属性包装. 为TSort提供排序源。
**/
struct TOrder {
	1:required TDirection direction
	2:required string property
	3:required bool ignoreCase
}