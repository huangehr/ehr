授权
====================

> 作者：温富建，2016.02.18

概述
---------------------

平台使用OAuth2作为协议。应用需要提供注册时平台为开发者分配的App Key，App Secret及用户名，获取一个临时Token，每次请求时均需要
携带此Token，具体规则参见[REST API规范](../../convention/rest-specification.html)。

Web页面授权流程
---------------------

非Web页面授权流程
---------------------

URL重定向
---------------------

权限作用域
---------------------

常见鉴权错误
---------------------

常见token访问错误
---------------------

用户权限检查与审核
---------------------

API列表
---------------------

### 获取权限列表

	GET /authorizations

### 获取应用权限

	GET /authorizations/:id

### 授权

	POST /authorizations
	
### 为应用授权

	PUT /authorizations/clients/:client_id
	
### 为应用及指纹授权

	PUT /authorizations/clients/:client_id/:fingerprint
	
### 更新授权信息

	PATCH /authorizations/:id
	
### 删除授权信息

	DELETE /authorizations/:id
	
### 检查权限
	
	GET /applications/:client_id/tokens/:access_token
	
### 重置权限

	POST /applications/:client_id/tokens/:access_token
	
### 删除应用授权信息

	DELETE /applications/:client_id/tokens/:access_token
