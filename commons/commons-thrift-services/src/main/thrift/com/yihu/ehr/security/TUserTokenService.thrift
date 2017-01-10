/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.security

include "TUserToken.thrift"

service TUserTokenService {
	TUserToken.TUserToken getToken(1:string userId)
}