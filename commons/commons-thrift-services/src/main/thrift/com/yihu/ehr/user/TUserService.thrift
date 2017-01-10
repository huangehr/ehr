/**
 * @author Sand
 * @version 1.0
 * @created 2016.01.15
 */
namespace java com.yihu.ehr.user

include "TUser.thrift"

service TUserService {
	bool createUser(1:TUser.TUser user)

	bool updateUser(1:TUser.TUser user)

	bool deleteUser(1:string userId)

	TUser.TUser getUser(1:string id)

	list<TUser.TUser> getUsers();

	list<TUser.TUser> searchUser();
}