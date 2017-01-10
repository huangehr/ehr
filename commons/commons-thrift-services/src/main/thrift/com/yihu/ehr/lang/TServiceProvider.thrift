/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.lang

/**
* 应用服务接口，用于取得Thrift服务器所有实现的服务名称列表。
**/
service TServiceProvider{
	string 	getImplementationName (1:string serviceName);

	bool supportsService (1:string serviceName);

	list<string> getSupportedServiceNames ();
}