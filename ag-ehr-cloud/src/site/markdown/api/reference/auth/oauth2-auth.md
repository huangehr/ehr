OAuth2授权
====================

> 作者：Sand，2016.02.18

概述
---------------------

平台使用OAuth2作为协议。OAuth2协议可以使应用具有访问用户私有数据的权限而不使用用户密码，用户也可以随时取消应用访问其数据的权限。

所有的开发者都需要在应用开发中心中注册成为开发者，并提交应用。成功之后平台会为应用分配一个Client Id与Secret，请不要泄漏Secret信息。
用户可以创建一个仅供个人使用的Token，也可以实现Web页面授权流程以允许其他用户能够对你的应用进行授权。

平台OAuth2仅支持[授权许可代码模式](https://tools.ietf.org/html/rfc6749#section-4.1)。开发者需要实现下文中描述的“Web页面授权流”，
并取得Token（平台不支持[隐式授权模式](https://tools.ietf.org/html/rfc6749#section-4.2)）。

Web页面授权流
---------------------

本流程描述来自第三方Web站点的OAuth2流程。

**1 你的应用将用户引导到健康档案平台**

	GET https://ehr.yihu.com/oauth/authorize
	
**参数**
<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>client_id</td>
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
			加号“+”分隔的作用域列表。若不提供此参数，默认作用域为空则生成一个不可用的Token。
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

**返回值**

在用户成功对应用授权之后，健康档案平台会用户重定向到你的应用，并在URL参数中添加一个验证码:code及上一步调用时使用的state参数：

	https://you-application.com?code=LjF5Jm&state=asdf12345hgiljl

**2 平台将用户重定向回你的应用**

验证码有效期为10分钟且仅能使用一次。如果state参数不匹配，应用应该取消本次授权过程。

使用验证码获取Token：

	POST https://ehr.yihu.com/oauth/accesstoken
	
**参数**
<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>client_id</td>
		<td>string</td>
		<td>必选。开发者在平台上提交的应用Key。</td>
	</tr>
	<tr>
		<td>client_secret</td>
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
	<tr>
		<td>grant_type</td>
		<td>authorization_code</td>
		<td>
			如果是授权码模式固定传authorization_code
		</td>
	</tr>
	
</table>

**返回值**
	
根据请求头的格式，其返回值格式可能如下：

	Accept: application/json
	{
        "access_token": "67ce6ef4-97cf-4d5c-a224-61651740280b",
        "token_type": "bearer",
        "refresh_token": "23477c94-4fee-4eaa-86de-12415c3daf0f",
        "expires_in": 43199,
        "scope": "read"
    }
	
**对比“请求作用域”与“授权作用域”**

scope属性包含此Token由用户所授权的有效作用域。正常情况下，作用域的范围与你请求的作用域是一致的。但是，用户可能会重新修改此Token的作用域，
特别是你的应用能访问的资源范围可能会被缩小。这种情况请注意调整你的应用。

相比你的原始请求作用域，若用户缩小你的应用能够访问的资源范围，请记得处理由这种情况引发的错误。例如，应用可以警告或通知用户做些必要的操作，
重新执行上面的授权操作以获取权限，但请注意用户随时可以拒绝。

**规范化作用域**

当一次请求多个作用域时，会将多个规范化的作用域列表与Token一起保存，删除那些由其他请求产生的作用域。

**3 使用Token调用API**

应用使用授权过的Token可以代表用户调用API：

	GET https://ehr.yihu.com/api/v1.0/user?access_token=...
	
可以将Token作为URL查询参数的一部分传递，如上所示，但更简洁的调用是将其作为HTTP请求头的一部分：

	Authorization: token OAUTH-TOKEN
	
调用示例：
	
	curl -H "Authorization: token OAUTH-TOKEN" https://ehr.yihu.com/v1.0/user

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

作用域于限定Token能够访问的资源范围，没有附加其他与授权用户本身有关的权限。用户可根据需要修改作用域，以限制应用的资源访问范围。

对于Web页面授权流程，应用在请求用户授权时会在授权页面表单上显示要授权的资源作用域。下表为平台当前开放的作用域：

<table>
   <tr>
      <td>名称</td>
      <td>描述</td>
   </tr>
   <tr>
      <td>(no scope)</td>
      <td>应用仅可以访问公开的信息，如：用户信息，机构信息。</td>
   </tr>
   <tr>
      <td>read,write:user</td>
      <td>授予用户信息的读/写权限，user:demographic_id与user:health_profile为只读。</td>
   </tr>
   <tr>
      <td>read:user:demographic_id</td>
      <td>授予用户身份证号的读取权限。</td>
   </tr>
   <tr>
      <td>read:user:health_profile</td>
      <td>授予用户健康档案的读取权限。</td>
   </tr>
   <tr>
      <td>read,write:organization</td>
      <td>授予机构信息的读/写权限，organization:standard与organization:adaption为只读。</td>
   </tr>
   <tr>
      <td>read:organization:standard</td>
      <td>授予机构数据标准的读取权限。</td>
   </tr>
   <tr>
      <td>read:organization:adaption</td>
      <td>授予机构适配数据的读取权限。</td>
   </tr>
</table>

检查响应头可知道你的作用域范围及API需要什么样的作用域作为条件

	curl -H "Authorization: token OAUTH-TOKEN" https://ehr.yihu.com/api/v1.0/users/technoweenie -I
	
    HTTP/1.1 200 OK
    X-OAuth-Scopes: repo, user
    X-Accepted-OAuth-Scopes: user
    
- X-OAuth-Scopes：此Token被授权的作用域列表
- X-Accepted-OAuth-Scopes：API检查的作用域列表

常见授权错误
---------------------

在向用户请求授权的过程中，可能会出现一些错误。你可能在初始化请求阶段看到收到以下错误：

**应用挂起**

如果授权的应用被挂起（如被举报，API使用不符合规范等），平台将会重定向到应用的回调URL，并附加以下参数：

	http://your-application.com/callback?error=application_suspended
	  &error_description=Your+application+has+been+suspended.+Contact+support@yihu.com.
	  &error_uri=https://ehr.yihu.com/api/v1.0/oauth/%23application-suspended
	  &state=xyz
	  
如果收到上述信息，请联系技术支持以解决此问题。

**重定向URL不匹配**

如果*redirect_uri*参数与应用注册时提供的回调URL不匹配，平台将重定向到应用注册配置的回调URL，并附加错误信息：

	http://your-application.com/callback?error=redirect_uri_mismatch
	  &error_description=重定向URI必须与应用注册回调URI一致
	  &state=xyz
	  
使用匹配的URL即可修复此错误。

**拒绝访问**

如果用户拒绝你的应用授权请求，平台将重定向到应用注册配置的回调URL，并附加错误信息：

	http://your-application.com/callback?error=access_denied
      &error_description=用户拒绝授权
      &state=xyz
      
这种情况你的应用是无法继续下一步操作的，如果用户只是简单地关闭了窗口，那你连这个错误也收不到。

常见Token访问错误
---------------------

在授权的第二阶段，使用临时代码获取Token时，可能会发下以下错误。这些错误格式根据你请求时所传递的HTTP头参数不同而不同。

**无效应用凭据**

若平台收到的Client Id与Secret无效，平台将返回以下错误：

	{
      "error": "incorrect_client_credentials",
      "error_description": "The client_id and/or client_secret passed are incorrect.",
      "error_uri": "https://ehr.yihu.com/api/v1.0/oauth/#incorrect-client-credentials"
    }
    
若发生这种错误，请检查你的应用并提供有效的凭据即可。

**重定向URL不匹配**

如果你的提供的*redirect_ui*与应用注册的回调URL不匹配，你将会收到以下错误信息：

	{
      "error": "redirect_uri_mismatch",
      "error_description": "The redirect_uri MUST match the registered callback URL for this application.",
      "error_uri": "https://ehr.yihu.com/api/v1.0/oauth/#redirect-uri-mismatch"
    }
    
若发生这种情况，请根据应用注册时提供的URL，重新请求授权。

**无效验证码**
    
如果验证码错误或过期，平台将返回以下错误：

	{
      "error": "bad_verification_code",
      "error_description": "授权码Lk8gzM无效或已过期",
      "error_uri": "https://ehr.yihu.com/docs/oauth/#bad-verification-code"
    }
    
若发生这种情况，请重新发请授权流获取新的验证码。

用户授权检查与审核
---------------------

用户可以在平台“应用授权设置中心”检查与取消应用授权。你的应用可以通过以下链接集成此审核页面（未完成）：

	https://ehr.yihu.com/settings/connections/applications/:client_id
	
API列表
---------------------

你可以使以下API管理OAuth应用，但这些API只能通过[用户名/密码授权](basic-auth.html)模式调用，而不是Token。

若用户启用两阶段授权（未实现），你可能需要多做一些工作才能调用API。

### 获取你的授权列表

	GET /authorizations
	
**返回值**

	Status: 200 OK    			
	Link: &lt;https://ehr.yihu.com/api/v1.0/resource?page=2&gt; rel="next",
		  &lt;https://ehr.yihu.com/api/v1.0/resource?page=5&gt; rel="last"
	X-RateLimit-Limit: 5000
	X-RateLimit-Remaining: 4999
	
	[
	  {
		"id": 1,
		"url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
		"scopes": [
		  "user"
		],
		"token": "",
		"hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
		"app": {
		  "url": "http://my-app.com",
		  "name": "my app",
		  "client_id": "abcde12345fghij67890"
		},
		"note": "optional note",
		"note_url": "http://optional/note/url",
		"updated_at": "2011-09-06T20:39:23Z",
		"created_at": "2011-09-06T17:26:27Z"
	  }
	]

### 获取授权Token

	GET /authorizations/:id
	
**返回值**
	
	Status: 200 OK
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z"
    }	

### 批量创建授权
	
若你的应用需要一次创建多个Token，那使用Web页面流程将变得非常冗长。相反，通过简易授权你可以为你旗下的多个应用一次性创建多个Token。


	POST /authorizations
	
**参数**
	
<table>
   <tr>
      <td>名称 </td>
      <td>类型</td>
      <td>描述</td>
   </tr>
   <tr>
      <td>scopes </td>
      <td>array</td>
      <td>Token的作用域.</td>
   </tr>
   <tr>
      <td>note</td>
      <td>string</td>
      <td>必选。对Token做备注以免你忘了这是要干嘛的。</td>
   </tr>
   <tr>
      <td>note_url</td>
      <td>string </td>
      <td>使用URL提醒你这是哪个应用的Token</td>
   </tr>
   <tr>
      <td>client_id</td>
      <td>string </td>
      <td>用于创建Token的Client ID，用分号分开。为空时，为你的所有应用创建Token，否则只为指定应用的创建Token</td>
   </tr>
   <tr>
      <td>client_secret </td>
      <td>string </td>
      <td>用于创建Token的Client secret。与client_id相对应。</td>
   </tr>
</table>

参数示例：

	{
      "scopes": [
        "public_repo"
      ],
      "note": "admin script"
    }
    
**返回值**
    
    Status: 201 Created
    Location: https://ehr.yihu.com/api/v1.0/authorizations/1
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "abcdefgh12345678",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z"
    }
	
### 创建单个应用授权

此方法为尚未创建授权的应用创建授权。Client ID直接包含在URL中。若授权已存在，则返回此授权信息，否则创建一个新的并返回。

	PUT /authorizations/clients/:client_id
	
**参数**

<table>
   <tr>
	 <td>名称 </td>
	 <td>类型</td>
	 <td>描述</td>
   </tr>
   <tr>
      <td>client_secret </td>
      <td>string </td>
      <td>必选。与URL中client ID相对应的Secret。</td>
   </tr>
   <tr>
      <td>scopes </td>
      <td>array </td>
      <td>要申请的授权作用域。</td>
   </tr>
   <tr>
	 <td>note</td>
	 <td>string</td>
	 <td>必选。对Token做备注以免你忘了这是要干嘛的。</td>
   </tr>
   <tr>
   	 <td>note_url </td>
   	 <td>string </td>
   	 <td>使用URL提醒你这是哪个应用的Token</td>
   </tr>
	<tr>
   	 <td>fingerprint</td>
   	 <td>string </td>
   	 <td>区别同一个应用，同一个用户创建的Token</td>
   </tr>
</table>

参数示例：

	{
      "client_secret": "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
      "scopes": [
        "public_repo"
      ],
      "note": "admin script",
      "fingerprint": ""
    }
    
**返回值**

授权不存在时：

	Status: 201 Created
    Location: https://ehr.yihu.com/api/v1.0/authorizations/1
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "abcdefgh12345678",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "fingerprint": ""
    }
    
授权存在时：

	Status: 200 OK
    Location: https://ehr.yihu.com/api/v1.0/authorizations/1
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "fingerprint": ""
    }
    
### 创建单个用户授权

此方法为尚未创建授权的应用创建授权。用户名直接包含在URL中。若授权已存在，则返回此授权信息，否则创建一个新的并返回。

	PUT /authorizations/users/:user_name
	
**参数**

<table>
   <tr>
	 <td>名称 </td>
	 <td>类型</td>
	 <td>描述</td>
   </tr>
   <tr>
      <td>password</td>
      <td>string </td>
      <td>必选。与URL中user_name相对应的密码。</td>
   </tr>
   <tr>
      <td>scopes </td>
      <td>array </td>
      <td>要申请的授权作用域。</td>
   </tr>
   <tr>
	 <td>note</td>
	 <td>string</td>
	 <td>对Token做备注以免你忘了这是要干嘛的。</td>
   </tr>
</table>

参数示例：

	{
      "client_secret": "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
      "scopes": [
        "public_repo"
      ],
      "note": "admin script",
      "fingerprint": ""
    }
    
**返回值**

授权不存在时：

	Status: 201 Created
    Location: https://ehr.yihu.com/api/v1.0/authorizations/1
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "abcdefgh12345678",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "fingerprint": ""
    }
    
授权存在时：

	Status: 200 OK
    Location: https://ehr.yihu.com/api/v1.0/authorizations/1
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "fingerprint": ""
    }
	
### 更新授权信息

	PUT /authorizations/:id
	
**参数**

<table>
   <tr>
	 <td>名称 </td>
	 <td>类型</td>
	 <td>描述</td>
   </tr>
   <tr>
      <td>scopes </td>
      <td>array </td>
      <td>新的授权作用域。</td>
   </tr>
   <tr>
      <td>add_scopes </td>
      <td>array </td>
      <td>新增的授权作用域。</td>
   </tr>
   <tr>
      <td>remove_scopes </td>
      <td>array </td>
      <td>要移除的授权作用域。</td>
   </tr>
   <tr>
	 <td>note</td>
	 <td>string</td>
	 <td>必选。对Token做备注以免你忘了这是要干嘛的。</td>
   </tr>
   <tr>
   	 <td>note_url </td>
   	 <td>string </td>
   	 <td>使用URL提醒你这是哪个应用的Token</td>
   </tr>
	<tr>
		<td>fingerprint </td>
		<td>string </td>
		<td>区别同一应用在不同用户下创建的Token</td>
	</tr>
</table>

参数示例（三个scope参数是互斥的，若同时存在，只取第一个）：

	{
      "add_scopes": [
        "repo"
      ],
      "note": "admin script"
    }
    
**返回值**

	Status: 200 OK
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "fingerprint": "jklmnop12345678"
    }
	
### 删除应用的所有授权

	DELETE /authorizations/:id
	
**返回值**

	Status: 204 No Content
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
	
### 检查权限

你的应用可以使用此API检查授权Token的有效性而不必登录。使用简易授权模式，即提供应用的Client Id与Secret作为参数即可。
如果Token无效，将返回*404 Not Found*：
	
	GET /applications/:client_id/tokens/:access_token
	
**返回值**

	Status: 200 OK
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "abcdefgh12345678",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "user": {
        "login": "octocat",
        "id": 1,
        "avatar_url": "https://ehr.yihu.com/images/error/octocat_happy.gif",
        "gravatar_id": "",
        "url": "https://ehr.yihu.com/api/v1.0/users/octocat",
        "html_url": "https://ehr.yihu.com/octocat",
        "followers_url": "https://ehr.yihu.com/api/v1.0/users/octocat/followers",
        "following_url": "https://ehr.yihu.com/api/v1.0/users/octocat/following{/other_user}",
        "gists_url": "https://ehr.yihu.com/api/v1.0/users/octocat/gists{/gist_id}",
        "starred_url": "https://ehr.yihu.com/api/v1.0/users/octocat/starred{/owner}{/repo}",
        "subscriptions_url": "https://ehr.yihu.com/api/v1.0/users/octocat/subscriptions",
        "organizations_url": "https://ehr.yihu.com/api/v1.0/users/octocat/orgs",
        "repos_url": "https://ehr.yihu.com/api/v1.0/users/octocat/repos",
        "events_url": "https://ehr.yihu.com/api/v1.0/users/octocat/events{/privacy}",
        "received_events_url": "https://ehr.yihu.com/api/v1.0/users/octocat/received_events",
        "type": "User",
        "site_admin": false
      }
    }
	
### 重置Token

你的应用可以在无需人工干预的情况下使用此API重置一个有效的OAuth Token。应用必须立即使用这个API的返回值，因为重置是实时生效的。
请使用简易授权，并提供应用Client ID与Secret调用此API。若Token本身已经无效，则返回*404 Not Found*。

	POST /applications/:client_id/tokens/:access_token
	
**返回值**

	Status: 200 OK
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
    
    {
      "id": 1,
      "url": "https://ehr.yihu.com/api/v1.0/authorizations/1",
      "scopes": [
        "public_repo"
      ],
      "token": "abcdefgh12345678",
      "token_last_eight": "12345678",
      "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
      "app": {
        "url": "http://my-github-app.com",
        "name": "my github app",
        "client_id": "abcde12345fghij67890"
      },
      "note": "optional note",
      "note_url": "http://optional/note/url",
      "updated_at": "2011-09-06T20:39:23Z",
      "created_at": "2011-09-06T17:26:27Z",
      "user": {
        "login": "octocat",
        "id": 1,
        "avatar_url": "https://ehr.yihu.com/images/error/octocat_happy.gif",
        "gravatar_id": "",
        "url": "https://ehr.yihu.com/api/v1.0/users/octocat",
        "html_url": "https://ehr.yihu.com/octocat",
        "followers_url": "https://ehr.yihu.com/api/v1.0/users/octocat/followers",
        "following_url": "https://ehr.yihu.com/api/v1.0/users/octocat/following{/other_user}",
        "gists_url": "https://ehr.yihu.com/api/v1.0/users/octocat/gists{/gist_id}",
        "starred_url": "https://ehr.yihu.com/api/v1.0/users/octocat/starred{/owner}{/repo}",
        "subscriptions_url": "https://ehr.yihu.com/api/v1.0/users/octocat/subscriptions",
        "organizations_url": "https://ehr.yihu.com/api/v1.0/users/octocat/orgs",
        "repos_url": "https://ehr.yihu.com/api/v1.0/users/octocat/repos",
        "events_url": "https://ehr.yihu.com/api/v1.0/users/octocat/events{/privacy}",
        "received_events_url": "https://ehr.yihu.com/api/v1.0/users/octocat/received_events",
        "type": "User",
        "site_admin": false
      }
    }
	
	
### 删除单个应用授权

你的应用可以删除单个Token。请使用简易授权，并提供应用Client ID与Secret调用此API。

	DELETE /applications/:client_id/tokens/:access_token

**返回值**

	Status: 204 No Content
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4999
