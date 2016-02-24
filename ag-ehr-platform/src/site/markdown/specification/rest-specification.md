REST API调用规范
====================

> 作者：温富建，2016.02.18

概述
---------------------

本文档为平台REST API调用规范，旨在为第三方应用提供访统一的问平台调用与交互方式，通过这些接口可以访问患者的健康数据，医疗资源及医疗机构等数据。

所有API都是通过向平台的REST服务器发送HTTP请求来实现，REST服务器支持gzip，可有效降低网络开销，建议第三方应用加入gzip支持。
 
术语解释
---------------------

- Client Id: 在平台注册App时，由平台提供的唯一App标识。
- Client Secret：在平台注册App时为其分配的应用密码。
- 用户ID：在平台注册用户时，由平台提供的唯一用户标识。
- 用户密码：与用户ID相匹配的用户密码，由用户保管。
- 机构代码：在平台注册机构时，此机构在国家注册的机构代码。
- 机构公钥：在平台注册时，平台为机构提供的公钥。

版本
---------------------

健康档案平台API均被版本化，当前版本为v1.0，版本号通过URL指定，例如： 

	rest/v1.0/user?age=20
	
请务必在请求中包含版本号以便请求能够得到正确响应。

模式
---------------------

所有API请求均通过HTTPS协议。API服务器地址：

- [https://ehr.yihu.com/rest](https://ehr.yihu.com/rest)：数据服务地址
- [https://da.yihu.com/rest](https://da.yihu.com/rest)：档案包接收/采集地址

数据发送与接收均以JSON作为载体。例如：数组参数array。普通字符串直接使用URL。

	curl -i https://ehr.yihu.com/rest/v1.0/users
    
    HTTP/1.1 200 OK
    Server: nginx
    Date: Fri, 12 Oct 2012 23:33:14 GMT
    Content-Type: application/json; charset=utf-8
    Connection: keep-alive
    Status: 200 OK
    ETag: "a00049ba79152d03380c34652f2cb612"
    Content-Length: 5
    Cache-Control: max-age=0, private, must-revalidate
    
JSON结构中字段为空的时候，使用null表示，而不是将其丢弃。

所有时间戳数据均以ISO 8601格式返回：

	YYYY-MM-DDTHH:MM:SSZ
	
### 资源摘要

当请求资源列表时，返回值是特定资源属性的一个子集，而非所有属性，即“资源摘要”（某些资源的属性会占用过多的系统资源），
鉴于性能原因应用默认不返回这些属性。若要获取这些属性，通过资源的详细信息来获取。

例如，当请求用户列表的时候，你将得到用户列表，其中只有部分属性有返回值

	GET /users
	
### 资源详情

当请求单个资源时，平台将会返回这个资源的所有属性，即“资源详情”（注意，用于不同的用户权限，其返回值可能有所不同）。

例如，当请求某个用户的详细信息时，将会返回这个用户的所有数据：

	GET /users/1024

参数
---------------------

很多API都含有可选参数。例如，对于GET请求，非路径变量参数可以通过HTTP查询参数指定：

	curl -i "https://ehr.yihu.com/v1/users/1024?age=20"
	
在这个示例中，用户的年龄参数通过*:age*查询字符串来指定。

对于POST, PUT与DELETE请求，不在URL中指定的参数必须封装成JSON，并指定Content-Type为'application/json'：

	curl -i -u username -d '{"age":["20"]}' https://ehr.yihu.com/users

全局入口
---------------------

通过向根路径发送GET请求，将会得到平台所有支持的API列表：

	curl https://ehr.yihu.com/rest

可以从返回的JSON得到你所需要的API列表

客户端错误
---------------------

客户端请求时，服务端可能会返回以下三种类型的API错误，具体的错误信息在响应体中：

- 若请求参数是一个无效的JSON，服务端将返回400错误：


	HTTP/1.1 400 Bad Request
    Content-Length: 35
    
    {"message":"Problems parsing JSON"}
    
- 若客户端发送的是一个错误的JSON值，服务端将返回400错误：


	HTTP/1.1 400 Bad Request
    Content-Length: 40
    
    {"message":"Body should be a JSON object"}
    
- 若客户端发送的数据包含无效的字段，服务端将返回422错误：

	
	HTTP/1.1 422 Unprocessable Entity
    Content-Length: 149
    
    {
      "message": "Validation Failed",
      "errors": [
        {
          "resource": "Issue",
          "field": "title",
          "code": "missing_field"
        }
      ]
    }
    
所有的错误对象都含有相应的资源与属性字段，用以告诉客户端可能的错误原因。另外，错误对象中还有一个错误代码，用于告诉哪个字段发生错误。
错误代码列表如下：

<table>
	<tr>
		<td>错误名称</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>missing</td>
		<td>资源不存在</td>
	</tr>
	<tr>
		<td>missing_field</td>
		<td>请求的资源不含请求的字段</td>
	</tr>
	<tr>
		<td>invalid</td>
		<td>字段的格式无效。请参考指定的资源文档以获取详细信息</td>
	</tr>
	<tr>
		<td>already_exists</td>
		<td>此资源的字段值与另一个资源一样。这种情况大多发生在资源的主键重复，例如：对象ID</td>
	</tr>
</table>

以上是通用的错误对象，资源也可能返回一些自定义错误消息，自定义错误消息包含一个message字段描述该错误，且大多数错误消息都
含有一个documentation_url字段指向可能对解决问题有帮助的页面。

HTTP重定向
---------------------

平台API在适当的时候会使用HTTP重定义功能。客户端应该假设所有的请求都有可能发生重定向。客户端接收到HTTP重定向时，
这不是一个错误并且客户端需要跟随重定向链接。重定向链接包含在URI资源的返回头*Location*字段中。

<table>
	<tr>
		<td>HTTP状态码</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>301</td>
		<td>永久重定向。所请求的资源已被永久转移到*Location*指定的位置，客户端应该使用该URI重新请求该资源。</td>
	</tr>
	<tr>
		<td>302，307</td>
		<td>临时重定向。所请求的资源暂时转移到*Location*指定的位置，客户端应该使用该URI重新请求该资源。</td>
	</tr>
</table>

HTTP动词
---------------------

平台使用HTTP动词代表一个操作，目前支持以下动词

<table>
	<tr>
		<td>动词</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>GET</td>
		<td>获取资源</td>
	</tr>
	<tr>
		<td>POST</td>
		<td>创建资源</td>
	</tr>
	<tr>
		<td>PUT</td>
		<td>更新一个或多个资源</td>
	</tr>
	<tr>
		<td>DELETE</td>
		<td>删除资源</td>
	</tr>
</table>

授权
---------------------

平台API有以下两种鉴权模式。大多数情况下，若不请求中不包含token会返回 *403 Forbidden*，但有些API出于保护资源的目的可能会返回*404 Not Found*错误。

- OAuth2 Token（包含在请求头中）

 
	curl -H "Authorization: token OAUTH-TOKEN" https://ehr.yihu.com/rest
	
- OAuth2 Token（作为请求参数的一部分）


	curl https://ehr.yihu.com/rest/resource?access_token=OAUTH-TOKEN
	
更多关于OAuth2的信息，请参考(OAuth2规范)[https://developer.github.com/v3/oauth/]。OAuth2 Token获取请查询授权接口。

- OAuth2 Id/Secret获取


	curl 'https://ehr.yihu.com/rest/users/whatever?client_id=xxxx&client_secret=yyyy'
	
该请求返回的Token仅能用于服务端-服务端的场景，请不要部署两个一样的应用，然后分别向服务端请求Token，会互相覆盖。
请保管好应用Secret，不要泄露。

- 登录失败处理

使用非法凭据来请求资源将会返回*401 Unauthorized*错误：

	curl https://ehr.yihu.com/rest/resource?access_token=OAUTH-TOKEN
    HTTP/1.1 401 Unauthorized
    
    {
      "message": "Bad credentials",
      "documentation_url": "https://ehr.yihu.com/rest"
    }

如果在短时间内，服务端接收到多个使用非法凭据的请求，后续该开发者的所有API请求在一段时间内都将被拒绝（包括使用合法凭据的请求），返回的错误为*403 Forbidden*

	curl -i https://ehr.yihu.com/rest/resource?access_token=INVALID-OAUTH-TOKEN
    HTTP/1.1 403 Forbidden
    
    {
      "message": "Maximum number of login attempts exceeded. Please try again later.",
      "documentation_url": "https://ehr.yihu.com/rest"
    }
    
超媒体
---------------------

所有的资源都含有一个或多个\*_url的属性，以链接到其他资源。这样做可以帮助客户端减少自己构建其关联资源的构建代码。强烈建议客户端使用这些URL。
这样做对以后开发者升级API会有帮助。所有的URL都符合 [RFC 6570](https://github.com/hannesg/uri_template)URI 模板。

	>> tmpl = URITemplate.new('/notifications{?since,all,participating}')
    >> tmpl.expand
    => "/notifications"
    
    >> tmpl.expand :all => 1
    => "/notifications?all=1"
    
    >> tmpl.expand :all => 1, :participating => 1
    => "/notifications?all=1&participating=1"

分页
---------------------

当请求的资源过多时，会默认以15条/页的数据返回。可以指定新的页码*page*与页大小*size*以返回更多的数据。

	curl 'https://aehr.yihu.com/rest/users?page=2&per_page=100'
	
注意，分布起始值为1，若请求中不提供page参数，默认返回第一页的数据。

### Link Header

分页信息包含在响应头的Link字段中。建议使用此字段的值作为资源导航，而不是自己构建URL。某些情况下，分页信息会使用SHA1并且不提供页码。

	Link: <https://ehr.yihu.com/rest/user/repos?page=3&per_page=100>; rel="next",
      <https://ehr.yihu.com/rest/repos?page=50&per_page=100>; rel="last"
      
Link字段包含一个或多个超媒体链接关系。*rel*可能值如下：

<table>
	<tr>
		<td>名称</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>next</td>
		<td>下一页结果</td>
	</tr>
	<tr>
		<td>last</td>
		<td>最后一页结果</td>
	</tr>
	<tr>
		<td>first</td>
		<td>第一页结果</td>
	</tr>
	<tr>
		<td>previous</td>
		<td>前一页结果</td>
	</tr>
</table>

请求频率与限制
---------------------

对认证过的请求，客户端每个小时的请求频率限制为5000次，对于未认证过的请求，客户端的请求频率限制为60次/小时。未认证的请求与IP绑定而不是与用户名绑定。

此情况对于搜索类型的API不适用，其频率限制有自身的规则。

通过检查HTTP响应头可以得到当前的请求频率限制：

	curl -i https://ehr.yihu.com/rest/users/whatever
    HTTP/1.1 200 OK
    Date: Mon, 01 Jul 2013 17:27:06 GMT
    Status: 200 OK
    X-RateLimit-Limit: 60
    X-RateLimit-Remaining: 56
    X-RateLimit-Reset: 1372700873
    
从请求头中你可以得知你所需要的信息：

<table>
	<tr>
		<td>字段名称</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>X-RateLimit-Limit</td>
		<td>每小时最大请求次数</td>
	</tr>
	<tr>
		<td>X-RateLimit-Remaining</td>
		<td>当前时间内剩余请求次数</td>
	</tr>
	<tr>
		<td>X-RateLimit-Reset</td>
		<td>下一次请求次数重置时间，UTC epoch秒数</td>
	</tr>
</table>

若想要将X-RateLimit-Reset时间转换为可读的，可以使用以下代码转换：

	new Date(1372700873 * 1000)
    // => Mon Jul 01 2013 13:47:53 GMT-0400 (EDT)

一旦请求次数用完，你将得到以下错误消息：

	HTTP/1.1 403 Forbidden
    Date: Tue, 20 Aug 2013 14:50:41 GMT
    Status: 403 Forbidden
    X-RateLimit-Limit: 60
    X-RateLimit-Remaining: 0
    X-RateLimit-Reset: 1377013266
    
    {
       "message": "API rate limit exceeded for xxx.xxx.xxx.xxx. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
       "documentation_url": "https://ehr.yihu.com/rest/v1/#rate-limiting"
    }
    
- 增加未授权的应用请求次数

若想增加未授权的应用请求次数，你需要在查询字段串中传递客户端的Client Id与Secret：

	curl -i 'https://ehr.yihu.com/rest/v1/users/whatever?client_id=xxxx&client_secret=yyyy'
    HTTP/1.1 200 OK
    Date: Mon, 01 Jul 2013 17:27:06 GMT
    Status: 200 OK
    X-RateLimit-Limit: 5000
    X-RateLimit-Remaining: 4966
    X-RateLimit-Reset: 1372700873
    
这个方法应该仅用于服务端-服务端的请求，请务必不要泄漏App Secret。

使用User Agent
---------------------

所有的API都需要在请求头中包含User-Agent。没有User-Agent的请求将被拒绝。请在此字段中使用您的用户名或应用名称。
这样做可以在出现问题的时候方便联系你们。

例如：
	
	User-Agent: Health-Profile-Browser
	
如果提供的User-Agent是无效值，你将收到以下错误信息：

	curl -iH 'User-Agent: ' https://ehr.yihu.com/rest/v1/meta
	
    HTTP/1.0 403 Forbidden
    Connection: close
    Content-Type: text/html
    
    Request forbidden by administrative rules.
    Please make sure your request has a User-Agent header.
    Check https://ehr.yihu.com/rest for other possible causes.
    
条件式请求
---------------------
	
未实现。

跨域资源共享（Cross-Origin Resource Sharing，CORS）
---------------------

未实现。

JSON-P回调
---------------------

你可以在GET请求参数中添加?callback参数以将请求结果包装成JSON函数，这种方式经常用在向第三方Web页面中嵌入健康档案平台的内容（跨域）。
平台将会把回调结果中的HTTP头作为API返回值的一部分：

	curl https://ehr.yihu.com/rest/v1?callback=foo
    
    foo({
      "meta": {
        "status": 200,
        "X-RateLimit-Limit": "5000",
        "X-RateLimit-Remaining": "4966",
        "X-RateLimit-Reset": "1372700873",
        "Link": [ // pagination headers and other links
          ["https://ehr.yihu.com/rest/v1?page=2", {"rel": "next"}]
        ]
      },
      "data": {
        // 回调的实际响应值
      }
    })
    
你可以编写JavaScript代码处理这种回调结果。以下是一个简单的示例：

	<html>
    <head>
    <script type="text/javascript">
    function foo(response) {
      var meta = response.meta;
      var data = response.data;
      console.log(meta);
      console.log(data);
    }
    
    var script = document.createElement('script');
    script.src = 'https://ehr.yihu.com?callback=foo';
    
    document.getElementsByTagName('head')[0].appendChild(script);
    </script>
    </head>
    
    <body>
      <p>Open up your browser's console.</p>
    </body>
    </html>
    
所有的响应头内容均与原HTTP响应头一致，除了LInk字段。此处的Link字段已经提前为你解析好，并包装为*[url, options]*数组。

例如，正常的HTTP响应头中Link结构如下：

	Link: <url1>; rel="next", <url2>; rel="foo"; bar="baz"
	
JSON-P中的Link结构如下：

	{
      "Link": [
        [
          "url1",
          {
            "rel": "next"
          }
        ],
        [
          "url2",
          {
            "rel": "foo",
            "bar": "baz"
          }
        ]
      ]
    }
    
时区
---------------------

部分API需要提供时间戳参数，部分API会返回含时区的时间戳。我们根据以下规则（优先级从大到小）决定API的时区信息：

**显示使用ISO 8601格式的时间戳，即包含时区信息的时间戳**

对于允许显示指定时间戳的API，我们将使用指定的时区。这种时间戳看起像这样：*2014-02-27T15:05:06+01:00*。

**在HTTP请求头中使用Time-Zone参数**

在HTTP请求头中附加Time-Zone参数，其值使用[时区数据库中的名称](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)。

	curl -H "Time-Zone: Asia/Shanghai" -X POST https://ehr.yihu.com/linguist/contents/new_file.md

**使用上一次的已知时区信息**

如果找不到Time-Zone参数，并且你生成了一个授权请求，我们将使用上次授权用户所在的时区。每次你浏览健康档案的时候都会更新这个值。

**UTC**

如果上述几种情况中均不包含任何时区信息，我们将使用UTC。