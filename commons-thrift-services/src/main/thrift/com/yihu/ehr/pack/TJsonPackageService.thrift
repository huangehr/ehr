namespace java com.yihu.ehr.pack

include "TJsonPackage.thrift"

service TJsonPackageService {
	/**
	* 接收病人档案，先存储成功，再在数据库中保存一条待入库记录。
	*
	* @param data 病人档案数据
	* @param pwd  zip密码，需要用私钥匙解密才有效。
	* @return 档案存储成功, 返回档案ID.
	*/
	bool receive(1:binary data, 2:string pwd)

	/**
	* 解析一份档案并入库。
	**/
	bool parsePackage(1:string id)

	/**
	* 获取from到to时间内接收到的档案列表。以天为单位。若from与to是同一天，则只显示这一天内的数据。
	*
	* @param since
	* @param to
	* @return
	*/
	list<TJsonPackage.TJsonPackage> getPackages(1:i32 since, 2:i32 to)

	/**
	* 获取档案列表（用于分页）
	* @param args
	* @return
	*/
	list<TJsonPackage.TJsonPackage> searchPackage(1:map<string, string> conditions)

	/**
	* 取得一份JSON档案对象包.
	*
	* @param id
	* @return
	*/
	TJsonPackage.TJsonPackage getPackage(1:string id)

	/**
	* 获取from到to时间内接收到的档案数。以天为单位。若from与to是同一天，则只显示这一天内的数据。
	*
	* @param since
	* @param to
	* @return
	*/
	i32 packageCount(1:i32 since, 2:i32 to);

	/**
	* 锁定一份要解析的JSON档案, 解析作业会将此档案解析到数据库中.
	*
	* @return 档案的存储路径.
	*/
	TJsonPackage.TJsonPackage acquirPackage(1:string id);
}