/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.app

include "TApp.thrift"

service TAppService {
	TApp.TApp getApp(1:string id)
}