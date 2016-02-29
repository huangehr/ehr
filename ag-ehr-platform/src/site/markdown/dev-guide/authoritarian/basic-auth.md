Basic授权(用户名/密码)
====================

> 作者：温富建，2016.02.22

概述
---------------------

平台提供了用户名/密码授权（下文称为“Basic授权”）模式，虽然我们支持这种方式，但强烈建议你的应用使用OAuth授权，除非API只能使用Basic授权。
例如：OAuth相关API及健康之路内部应用需要使用Basic授权认证身份。

Basic授权
---------------------

平台根据[RFC2617](http://www.ietf.org/rfc/rfc2617.txt)规范提供了Basic授权支持，但有些不同。此规范中要求对于未授权的请求返回*401 Unauthorized*错误，
但由于健康数据的特殊性及数据隐私保密原则，平台有可能会返回*404 Not Found*错误。请注意这个区别以免某些HTTP库处理此返回值时出现错误。
错误处理方式可能在HTTP响应头的*Authorization*中提及。

**使用用户名/密码**

使用简单的正确用户/密码组合即可调用平台的API。通过以下命令行即可实现对用户的授权，只需要将username替换为你的用户名

	curl -u username https://ehr.yihu.com/api/v1/user
	
**使用OAuth Token**

另外也可以使用个人Token或OAuth Token替代用户名/密码

	curl -u username:token https://ehr.yihu.com/api/v1/user
	
这种方式对于不能使用OAuth机制，但想利用Token的优点的情况非常方便。

两阶段授权（未实现）
---------------------

若用户启用了两阶段授权功能，则Basic授权还需要多做一步，即短信或邮箱验证。