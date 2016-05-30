REST API 规范
====================

引言
---------------------

REST API只是一个规范，而不是一个标准，因此EHR微服务平台的REST API规范参考了部分论坛及社区的意见，并结合REST最佳实践，形成本规范。

本规范的目标是从实践的角度出发。作为设计的基础，遵守以下几个原则：

	1. 当标准合理的时候遵守标准。
	2. API应该对程序员友好，并且在浏览器地址栏容易输入。
	3. API应该简单，直观，容易使用的同时优雅。
	4. API应该具有足够的灵活性来支持上层ui。
	5. API设计权衡上述几个原则。

需要强调的是：API的就是程序员的UI，和其他UI一样，你必须仔细考虑它的用户体验！

使用名词而不是动词
---------------------

为了易于理解，以用户为例说明API，其中Json Model为资源的JSON描述串：
<table>
    <tr>
        <td>资源</td>
        <td>Get/Read</td>
        <td>Post/Create</td>
        <td>Put/Update</td>
        <td>Delete/Delete</td>
    </tr>
	<tr>
		<td>Users</td>
		<td>获取用户列表，返回值：Json Model（带ID）</td>
		<td>创建一个用户，入参：Json Model（不带ID），返回值：Json Model（带ID）</td>
		<td>批量更新用户信息</td>
		<td>指删除用户信息</td>
	</tr>
	<tr>
		<td>Users/2</td>
		<td>获取ID为2的用户信息，返回值：Json Model（带ID）</td>
		<td>以ID为2的资源作为模板，创建一个新的用户并返回该新用户信息，返回值：Json Model（带ID）</td>
		<td>更新用户信息，入参：Json Model，ID在URL中指定</td>
		<td>删除用户信息，入参：资源ID，在URL中指定，返回值根据实际需要决定，默认不返回</td>
	</tr>
</table>

不要使用动词：

	/getAllCars
	/createNewCar
	/deleteAllRedCars

当然，在某些特定的场合，确实无法使用名词，如资源搜索，这时请使用简单明了的动词：

	/users/search?filter="name?林"
	
Get方法和查询参数不应该改变资源状态
---------------------

使用Put,Post和Delete方法替代Get方法来改变资源状态，不要使用Get来使状态改变：

	GET /users/711?activate or
	GET /users/711/activate
	
使用名词的复数形式
---------------------

不要混合使用单数和复数形式，而应该为所有资源一直保持使用复数形式：

	/cars instead of /car
	/users instead of /user
	/products instead of /product
	/settings instead of /setting

为关系使用子资源
---------------------

假如资源连接到其它资源，则使用子资源形式：

	GET /cars/711/drivers/ Returns a list of drivers for car 711
	GET /cars/711/drivers/4 Returns driver #4 for car 711
	
使用HTTP头决定序列化格式
---------------------

在客户端和服务端都需要知道使用什么格式来进行通信，这个格式应该在HTTP头中指定:

	* Content-Type：定义请求的格式；
	* Accept ：定义允许的响应格式的列表

使用HATEOAS
---------------------

Hypermedia as the Engine of Application State（HATEOAS）是一个指导原则，它规定超文本链接应该被用于在API中创建更好的资源导航：

	{
	  "id": 711,
	  "manufacturer": "bmw",
	  "model": "X5",
	  "seats": 5,
	  "drivers": [
	   {
		"id": "23",
		"name": "Stefan Jauker",
		"links": [
		 {
		 "rel": "self",
		 "href": "/api/v1/drivers/23"
		}
	   ]
	  }
	 ]
	}
	
只提供JSON作为返回格式
---------------------

比较一下XML和json了。XML即冗长，难以阅读，又不适合各种编程语言解析。当然XML有扩展性的优势，但是如果你只是将它来对内部资源串行化，那么他的扩展优势也发挥不出来。

当然后期也可能需要支持XML。如果是这样的话你还有另外一个问题：你的http请求中的media类型是应该和accept 头同步还是和url？为了方便（browser explorability）,
应该是在url中(用户只要自己拼url就好了)。如果这样的话最好的方法是使用.xml或者.json的后缀。

命名方式
---------------------

URL中使用蛇形命令（下划线和小写），字段中，即JSON使用驼峰命名。如果使用json那么最好的应该是遵守JAVASCRIPT的命名方法，也就是说骆驼命名法。
如果你正在使用多种语言写一个库，那么最好按照那些语言所推荐的，Java，C#使用骆驼，Python，Ruby使用Snake。

在Post，Put，Patch上使用JSON作为输入
---------------------

使用JSON作为所有的API输出格式，接下来考虑考虑API的输入数据格式。
很多的API使用url编码格式：就像是url查询参数的格式一样：单纯的键值对。这种方法简单有效，但是也有自己的问题：它没有数据类型的概念。
这使得程序不得不根据字符串解析出布尔和整数,而且还没有层次结构–虽然有一些关于层次结构信息的约定存在可是和本身就支持层次结构的json比较一下还是不很好用。
当然如果API本身就很简单，那么使用URL格式的输入没什么问题，但对于复杂的API你应该使用JSON。或者干脆统一使用JSON。
注意使用JSON传输的时候，要求请求头里面加入：Content-Type：applicatin/json，否则抛出415异常（unsupported media type）。

为集合提供过滤、排序、字段选择以及分页
---------------------

资源集合的GET方法不提供过滤功能，仅提供排序，字段选择及分布。功需要过滤功能，请单独为资源集合设计一个search方法。

###过滤
	
为所有字段提供查询参数，过滤参数使用filter封装，封装的语法如下：

- like：使用"?"来表示，如：name?%医，注意“医”字不必提供前后百分号及单引号
- in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
- between: 使用"="来表示并用中括号指定上下限，如：age=\[10,20\]
- =：使用"="来表示，如：status=2
- \>=：使用大于号和大于等于语法，如：createTime\>2012
- \<=：使用小于号和小于等于语法，如：createTime\<=2015
- 分组：在条件后面加上空格，并设置分组号，如：createTime\>2012 g1，具有相同组名的条件将使用or连接
- 多条件组合：使用";"来分隔，条件之间将使用AND连接


	GET /user/search?name?林 				将返回所有姓名中包含有“林”的用户
	GET /user/search?age=12,13,14 			返回年龄等于12，13，14的用户
	GET /user/search?name?林;age=12,13,14 	返回姓名包含有林且年龄等于12，13，14的用户

###排序

允许跨越多字段的正序或者倒序排列，排序参数使用sort封装，封装的语法如下：

- +name 表示按name字段升序排列
- -age 表示按age字段降序排列
- 字段之间使用逗号分隔


	GET /user/search?sort=+name,-age		返回结果将按name升序排序，再按age降序排序
	
###字段选择

在某些场景中，我们只需要在列表中查询几个有标识意义的字段，我们不需要从服务端把所有字段的值都请求出来，所以需要支持API选择查询字段的能力，这也可以提到网络传输性能和速度，
字段选择参数使用fields封装，封装语法如下：

- name,age 字段名使用英文逗号分隔


	GET /user/search?fields=name,age		返回结果将只有name，age字段
	
###分页

使用page和size来获取固定数量的资源结果，当其中一个参数没有出现时，会使用各自的默认值：

	GET /user/search?fields=+name,-age&page=1&size=15				返回第一页的15条记录

在HTTP响应中，使用自定义头X-Total-Count发回给调用段实际的资源数量。前后页的链接也在HTTP头LINK域中：

	Link: <https://blog.mwaysolutions.com/sample/api/v1/cars?offset=15&limit=5>; rel="next",
	<https://blog.mwaysolutions.com/sample/api/v1/cars?offset=50&limit=3>; rel="last",
	<https://blog.mwaysolutions.com/sample/api/v1/cars?offset=0&limit=5>; rel="first",
	<https://blog.mwaysolutions.com/sample/api/v1/cars?offset=5&limit=5>; rel="prev",
	
版本化
---------------------

API强制实行版本化，版本号在rest路径后面，资源的前面，并以v开头：

	/rest/v1.0/users
	
使用HTTP状态码处理错误
---------------------

忽略错误处理的API是很难使用的，简单的返回500和调用堆栈是非常不友好也非常无用的，我们使用403并提供异常信息，403表示不允许的，服务端正常解析和请求，
但是调用被回绝或者不被允许。下面是更新一个不存在的用户时的提示消息：

	{
	  "code": "ehr.user.invalid.id",
	  "message": "此用户ID不存在",
	  "internalMessage": "No car found in the database",
	  "code": 34,
	  "more info": "http://dev.mwaysolutions.com/blog/api/v1/errors/12345"
	}


