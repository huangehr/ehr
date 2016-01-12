namespace java com.yihu.ehr.lang

/**
* 应用服务接口，用于取得Thrift服务器所有实现的服务名称列表。
**/
service ServiceProvider{
	string 	getImplementationName (1:string serviceName);

	bool supportsService (1:string serviceName);

	list<string> getSupportedServiceNames ();
}