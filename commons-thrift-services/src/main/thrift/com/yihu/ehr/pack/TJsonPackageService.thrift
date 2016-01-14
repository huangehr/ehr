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
	* 获取from到to时间内接收到的档案列表。以天为单位。若from与to是同一天，则只显示这一天内的数据。
	*
	* @param since
	* @param to
	* @return
	*/
	list<TJsonPackage.TJsonPackage> getArchiveList(1:string since, 2:string to)

	/**
	* 获取档案列表（用于分页）
	* @param args
	* @return
	*/
	list<TJsonPackage.TJsonPackage> searchArchives(1:map<string, string> conditions)

	/**
	* 取得一份JSON档案对象包.
	*
	* @param id
	* @return
	*/
	TJsonPackage.TJsonPackage getTJsonPackage.TJsonPackage(1:string id)

	/**
	* 获取from到to时间内接收到的档案数。以天为单位。若from与to是同一天，则只显示这一天内的数据。
	*
	* @param since
	* @param to
	* @return
	*/
	i32 getArchiveCount(1:string since, 2:string to)

	/**
	* 锁定一份要解析的JSON档案, 解析作业会将此档案解析到数据库中.
	*
	* @return 档案的存储路径.
	*/
	TJsonPackage.TJsonPackage acquireArchive();

	/**
	* 将档案标记为已入库.
	*
	* @param jsonArchiveId
	*/
	void reportArchiveFinished(1:string jsonArchiveId, 2:string message)

	/**
	* 标记档案为解析失败，同时记录失败原因。
	*
	* @param jsonArchiveId
	* @param message
	*/
	void reportArchiveFailed(string jsonArchiveId, string message)
}