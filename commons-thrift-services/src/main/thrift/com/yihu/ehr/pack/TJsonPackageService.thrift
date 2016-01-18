/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.pack

include "TJsonPackage.thrift"
include "../data/domain/TPageable.thrift"

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
	i32 getPackageCount(1:i32 since, 2:i32 to)

	/**
	* 取得一份JSON档案对象包.
	*
	* @param id
	* @return
	*/
	TJsonPackage.TJsonPackage getPackage(1:string id)

	/**
	* 获取未解析的包。若未解析的包数量小于 count，则只返回实际未解析的包。
	**/
	list<TJsonPackage.TJsonPackage> getUnresolvedPackages(1:i32 count)

	/**
	* 获取档案列表（分页）
	* @param args
	* @return
	*/
	list<TJsonPackage.TJsonPackage> search(1:map<string, string> criteria, 2:TPageable.TPageable pageable)
}