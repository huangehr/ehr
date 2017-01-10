/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.adaption

include "TAdaptationScheme.thrift"

/**
* 标准适配分发服务。
**/
service TAdaptionDispatchService{
	/**
	* 获取适配方案映射信息
	**/
	TAdaptationScheme.TAdaptationScheme getScheme(1:string userId, 2:string verionIde, 3:string orgCode);

	/**
	* 获取采集标准及适配方案信息
	**/
	list<TAdaptationScheme.TAdaptationScheme> getSchemes(1:string userId, 2:string verionIde, 3:string orgCode);

	/**
	* 根据机构编码获取最新映射版本号
	**/
	TAdaptationScheme.TAdaptationScheme getOrgLastestAdaption(1:string orgCode);
}