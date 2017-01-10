/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.resolve

include "../pack/TPackage.thrift"

service TResolveService {
	void startResovlveScheduler(1:string cronExpression)

	void pauseResolveScheduler(bool waitForCompleted)
}