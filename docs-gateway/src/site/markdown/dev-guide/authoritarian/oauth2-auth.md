OAuth2授权
====================

> 作者：温富建，2016.02.18

概述
---------------------

平台使用OAuth2作为协议。OAuth2协议可以使应用具有访问用户私有数据的权限而不使用用户密码，用户也可以随时取消应用访问其数据的权限。

所有的开发者都需要在应用开发中心中注册成为开发者，并提交应用。成功之后平台会为应用分配一个App Key与Secret，请不要泄漏Secret信息。
用户可以创建一个仅供个人使用的Token，也可以实现Web页面授权流程以允许其他用户能够对你的应用进行授权。

平台OAuth2仅支持[授权许可代码模式](https://tools.ietf.org/html/rfc6749#section-4.1)。开发者需要实现下文中描述的“Web页面授权流程”，
并取得Token（平台不支持[隐式授权模式](https://tools.ietf.org/html/rfc6749#section-4.2)）。

Web页面授权流程
---------------------

本流程描述来自第三方Web站点的OAuth2流程。

**1 你的应用将用户引导到健康档案平台**

	GET https://ehr.yihu.com/rest/v1/login/oauth/authorize
	
**参数**
<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>app_key</td>
		<td>string</td>
		<td>必选。开发者在平台上提交的应用Key</td>
	</tr>
	<tr>
		<td>redirect_uri</td>
		<td>string</td>
		<td>授权成功之后，将用户重定向到哪里。参见下文“重定向URL”。</td>
	</tr>
	<tr>
		<td>scope</td>
		<td>string</td>
		<td>
			逗号分隔的作用域列表。若不提供此参数，默认作用域为空且不含有可用Token。
			若用户已经为此应用授权，授权页面不会显示已授权作用域，而是使用之前授权的作用域
			生成新的Token。
		</td>
	</tr>
	<tr>
		<td>state</td>
		<td>string</td>
		<td>
			随机串，平台将原样回去。用于防XSS攻击。
		</td>
	</tr>
</table>

**2 平台将用户重定向回你的应用**

在用户成功对应用授权之后，健康档案平台会用户重定向到你的应用，并在返回参数中添加一个验证码:code及上一步调用时使用的state参数。
验证码有效期为10分钟且仅能使用一次。如果state参数不匹配，应用应该取消本次授权过程。

使用验证码获取Token：

	POST https://ehr.yihu.com/rest/v1/login/oauth/access_token
	
**参数**
<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>app_key</td>
		<td>string</td>
		<td>必选。开发者在平台上提交的应用Key。</td>
	</tr>
	<tr>
		<td>secret</td>
		<td>string</td>
		<td>必选。应用Secret。</td>
	</tr>
	<tr>
		<td>code</td>
		<td>string</td>
		<td>
			必选。第一步返回的验证码。
		</td>
	</tr>
	<tr>
		<td>redirect_uri</td>
		<td>string</td>
		<td>
			用户授权成功后，平台用于重定向的URL。
		</td>
	</tr>
	<tr>
		<td>state</td>
		<td>string</td>
		<td>
			上一步发送过来的随机串。
		</td>
	</tr>
</table>

**返回值**

默认返回值格式如下：

	access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&scope=user%2Cprofile&token_type=bearer
	
根据请求头的格式，其返回值格式也可能如下：

	Accept: application/json
	{
		"access_token":"e72e16c7e42f292c6912e7710c838347ae178b4a",
		 "scope":"user,profile",
		 "token_type":"bearer"
	}
	
	Accept: application/xml
	<OAuth>
	  <token_type>bearer</token_type>
	  <scope>user,profile</scope>
	  <access_token>e72e16c7e42f292c6912e7710c838347ae178b4a</access_token>
	</OAuth>
	
**对比“请求作用域”与“授权作用域”**

scope属性包含此Token由用户所授权的有效作用域。正常情况下，作用域的范围与你请求的作用域是一致的。但是，用户可能会重新修改此Token的作用域，
特别是你的应用能访问的资源范围可能会被缩小。这种情况请注意调整你的应用。

相比你的原始请求作用域，若用户缩小你的应用能够访问的资源范围，请记得处理由这种情况引发的错误。例如，应用可以警告或通知用户做些必要的操作，
重新执行上面的授权操作以获取权限，但请注意用户随时可以拒绝。

**规范化作用域**

当一次请求多个作用域时，会将多个规范化的作用域列表与Token一起保存，删除那些由其他请求产生的作用域。

**3 使用Token调用API**

应用使用授权过的Token可以代表用户调用API：

	GET https://ehr.yihu.com/rest/v1/user?access_token=...
	
可以将Token作为URL查询参数的一部分传递，如上所示，但更简洁的调用是将其作为HTTP请求头的一部分：

	Authorization: token OAUTH-TOKEN
	
调用示例：
	
	curl -H "Authorization: token OAUTH-TOKEN" https://ehr.yihu.com/v1/user

非Web页面授权流程
---------------------

重定向URL
---------------------

在本流程中，redirect_uri参数是可选的，如果空为平台会将用户重定向到应用OAuth参数中配置的回调URL。如果不为空，
此重定向URL的主机与端口必须与上述回调URL一致，即重定向URL是回调URL的一个子目录。

	CALLBACK: http://example.com/path
    
    有效: http://example.com/path
    有效: http://example.com/path/subdir/other
    无效: http://example.com/bar
    无效: http://example.com/
    无效: http://example.com:8080/path
    无效: http://oauth.example.com:8080/path
    无效: http://example.org

作用域
---------------------

作用域(scope.html)用于限定Token能够访问的资源范围，没有附加其他与授权用户本身有关的权限。

对于Web页面授权流程，应用在请求用户授权时会在授权页面表单上显示要授权的资源作用域。

检查响应头可知道你的作用域范围及API需要什么样的作用域作为条件

	curl -H "Authorization: token OAUTH-TOKEN" https://ehr.yihu.com/rest/v1/users/technoweenie -I
	
    HTTP/1.1 200 OK
    X-OAuth-Scopes: repo, user
    X-Accepted-OAuth-Scopes: user
    
- X-OAuth-Scopes：此Token被授权的作用域列表
- X-Accepted-OAuth-Scopes：API检查的作用域列表

<table>
	<tr>
		<td>名称</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>(no scope)</td>
		<td>对公共数据仅有只读权限（包括公开的用户档案信息，机构信息等）</td>
	</tr>
	<tr>
		<td>user</td>
		<td>具有患者数据的读/写权限，包括：user:email</td>
	</tr>
</table>

常见授权错误
---------------------

在向用户请求授权的过程中，可能会出现一些错误。你可能在初始化请求阶段看到收到以下错误：

**应用挂起**

如果授权的应用被挂起（如被举报，API使用不符合规范等），平台将会重定向到应用的回调URL，并附加以下参数：

	http://your-application.com/callback?error=application_suspended
	  &error_description=Your+application+has+been+suspended.+Contact+support@yihu.com.
	  &error_uri=https://ehr.yihu.com/rest/v1/oauth/%23application-suspended
	  &state=xyz
	  
如果收到上述信息，请联系技术支持以解决此问题。

**重定向URL不匹配**

如果*redirect_uri*参数与应用注册时提供的回调URL不匹配，平台将重定向到应用注册配置的回调URL，并附加错误信息：

	http://your-application.com/callback?error=redirect_uri_mismatch
	  &error_description=The+redirect_uri+MUST+match+the+registered+callback+URL+for+this+application.
	  &error_uri=https://ehr.yihu.com/rest/v1/oauth/%23redirect-uri-mismatch
	  &state=xyz
	  
使用匹配的URL即可修复此错误。

**拒绝访问**

如果用户拒绝你的应用授权请求，平台将重定向到应用注册配置的回调URL，并附加错误信息：

	http://your-application.com/callback?error=access_denied
      &error_description=The+user+has+denied+your+application+access.
      &error_uri=https://ehr.yihu.com/rest/v1/oauth/%23access-denied
      &state=xyz
      
这种情况你的应用是无法继续下一步操作的，如果用户只是简单地关闭了窗口，那你连这个错误也收不到。

常见Token访问错误
---------------------

在授权的第二阶段，使用临时代码获取Token时，可能会发下以下错误。这些错误格式根据你请求时所传递的HTTP头参数不同而不同。

**无效应用凭据**

若平台收到的App Key与Secret无效，平台将返回以下错误：

	{
      "error": "incorrect_client_credentials",
      "error_description": "The app_key and/or client_secret passed are incorrect.",
      "error_uri": "https://ehr.yihu.com/rest/v1/oauth/#incorrect-client-credentials"
    }
    
若发生这种错误，请检查你的应用并提供有效的凭据即可。

**重定向URL不匹配**

如果你的提供的*redirect_ui*与应用注册的回调URL不匹配，你将会收到以下错误信息：

	{
      "error": "redirect_uri_mismatch",
      "error_description": "The redirect_uri MUST match the registered callback URL for this application.",
      "error_uri": "https://ehr.yihu.com/rest/v1/oauth/#redirect-uri-mismatch"
    }
    
若发生这种情况，请根据应用注册时提供的URL，重新请求授权。

**无效验证码**

	{
      "add_scopes": [
        "repo"
      ],
      "note": "admin script"
    }
    
如果验证码错误或过期，平台将返回以下错误：

	{
      "error": "bad_verification_code",
      "error_description": "The code passed is incorrect or expired.",
      "error_uri": "https://ehr.yihu.com/rest/v1/oauth/#bad-verification-code"
    }
    
若发生这种情况，请重新请求授权，并获取新的验证码。

用户授权检查与审核
---------------------

用户可以在平台“应用授权设置中心”检查与取消应用授权。你的应用可以通过以下链接集成此审核页面（未完成）：

	https://ehr.yihu.com/settings/connections/applications/:app_key
	
API列表
---------------------

你可以使以下API管理OAuth应用，但这些API只能通过[用户名/密码授权](basic-auth.html)模式调用，而不是Token。

若用户启用两阶段授权（未实现），你可能需要多做一些工作才能调用API。

### 获取权限列表

	GET /authorizations

### 获取应用权限

	GET /authorizations/:id

### 创建授权

	POST /authorizations
	
### 为应用授权

	PUT /authorizations/clients/:app_key
	
### 为应用及指纹授权

	PUT /authorizations/clients/:app_key/:fingerprint
	
### 更新授权信息

	PATCH /authorizations/:id
	
### 删除授权信息

	DELETE /authorizations/:id
	
### 检查权限
	
	GET /applications/:app_key/tokens/:access_token
	
### 更新Token

你的应用访问的时候，如果服务端提示“Token Expired”，则需要更新

	POST /applications/:app_key/tokens/:access_token
	
### 删除应用授权信息

	DELETE /applications/:app_key/tokens/:access_token
