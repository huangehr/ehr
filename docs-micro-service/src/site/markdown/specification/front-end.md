前端代码规范
====================

> 作者：叶泽华，2015.07.17

目录
---------------------

> 一、HTML书写规范
>
> (一) 注释
>
> (二) 代码风格
>
> (三) 通用
>
> (四) head
>
> (五) 图片
>
> (六) 表单
>
> (七) 多媒体
>
> (八) 模板中的HTML
>
> (九) 其它
>
> 二、CSS书写规范
>
> (一) 注释
>
> (二) 代码风格
>
> (三) 书写规范
>
> (四) 常用的CSS命名
>
> (五) 其它
>
> (六) 最佳实践
>
> (七) 典型错误
>
> 三、JS书写规范
>
> (一) 注释
>
>
> (二) 代码风格
>
> (三) 命名规范

一、HTML书写规范
---------------------

### 注释

- 对功能代码区块添加必要的注释
- 注释的格式
- 不要在注释中在出现双横线"--"，如果需要可以使用‘\#

如：不规范的注释

	<!------项目列表部分------->

规范的注释：

	<!--######项目列表部分######-->
	
- 在功能代码块开始前，结束后各添加一行注释，可以类似如下：


	<!--######项目列表部分######-->
		// 这里是功能代码块
	<!--######项目列表部分结束######-->


3.	协助注释
非作者维护时所加入的表示修改时间、修改人等标识信息。
在区域注释或单行注释的基础上加上修改人和修改时间等信息。
区域注释：
<!--=S注释内容[修改人和修改时间]-->
	修改的内容…
<!--=E 注释内容[修改人和修改时间]-->

单行注释：
<!-- 注释内容[修改人和修改时间]-->

(二)	代码风格
---------------------

1.	缩进与换行
1)	每次缩进4个空格，使用tab键进行缩进，也不要把tab键以及空格混合起来进行缩进。
2)	建议每行不得超过 120 个字符。
3)	为每个块级元素或表格元素标签新起一行，并且对每个子元素进行缩进。
2.	命名
1)	只使用小写，包括标签名、属性名、属性值（一些可以自定义的字符串属性值除外）。如：
 
2)	页面元素的id属性值,必须唯一。
3)	在id和class属性中使用 – 作为连字符。
4)	同一页面，应避免使用相同的 name 与 id。
IE 浏览器会混淆元素的 id 和 name 属性， document.getElementById 可能获得不期望的元素。所以在对元素的 id 与 name 属性的命名需要非常小心。

	<input name="foo">
	<div id="foo"></div>
	<script>
	// IE6 将显示 INPUT
	alert(document.getElementById('foo').tagName);
	</script>
	
5)	自定义属性建议以 xxx- 为前缀，推荐使用 data-。使用前缀有助于区分自定义属性和标准定义的属性。

	<ol data-ui-type="sel"></ol>

3.	标签
1)	对于无需自闭合的标签，不允许自闭合。
常见无需自闭合标签有input、br、img、hr等。
 
2)	对 HTML5 中规定不允许省略的闭合标签，不允许省略闭合标签。
3)	标签使用必须符合标签嵌套规则。
如：div 不得置于 p 中，tbody 必须置于 table 中。
详细的标签嵌套规则参见HTML DTD中的 Elements 定义部分。
4)	根据用途来选择标签。尽量使用语义化的html标签，少用div、span无语义标签。
 
下面是常见标签语义
p - 段落							h1,h2,h3,h4,h5,h6 - 层级标题
strong,em - 强调				ins - 插入
del - 删除						abbr - 缩写
code - 代码标识					cite - 引述来源作品的标题
q - 引用							blockquote - 一段或长篇引用
ul - 无序列表					ol - 有序列表
dl,dt,dd - 定义列表
5)	在 CSS 可以实现相同需求的情况下不得使用表格进行布局。
6)	优化标签。有些标签是不需要用到的，能少就少。可以参考HTML5 specification来知道哪些标签是必须的，哪些又是多余的。
4.	属性
1)	属性值必须用双引号包围（不允许使用单引号，不允许不使用引号），布尔类型的属性，建议不添加属性值。

	<input type="text" disabled>
	<input type="checkbox" value="1" checked>

2)	自定义属性命名，参见前面“2.命名5）”。

(三)	通用
---------------------

1.	DOCTYPE
1)	HTML5的文档类型对所有的html文档都适用：<!DOCTYPE html>。另外，最好使用html,而不是xhtml。建议使用大写的 DOCTYPE。
2)	启用 IE Edge 模式。
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
3)	在 html 标签上设置正确的 lang 属性。
有助于提高页面的可访问性，如：让语音合成工具确定其所应该采用的发音，令翻译工具确定其翻译语言等。
	
2.	编码
1)	定义页面的编码时使用<meta charset="utf-8">,并且必须是<head>的第一个直接子元素。


	<html>
		<head>
		   <meta charset="UTF-8">
			......
	   </head>
		<body>
		   ......
		</body>
	</html>
	
2)	确保你的IDE使用的是UTF-8编码来保存文件的，且不要带上BOM。
3)	在样式表文件里不用去声明UTF-8编码。
3.	CSS和JavaScript引入
1)	确保页面的 结构、样式、行为三者相分离。确保文档或模板中只包含html，把用到的样式都写到样式表文件中，把脚本都写到js文件中。
2)	引入 CSS 时必须指明 rel="stylesheet"。
3)	省略<style>和<script>的type属性。
4)	建议在 head 中引入页面需要的所有 CSS 资源。
因为在页面渲染的过程中，新的CSS可能导致元素的样式重新计算和绘制，页面闪烁。
5)	建议JavaScript 应当放在页面末尾，或采用异步加载。
将 script 放在页面中间将阻断页面的渲染。出于性能方面的考虑，如非必要，请遵守此条建议。

(四)	head
---------------------

1.	title
1)	页面必须包含 title 标签声明标题。
2)	title 必须作为 head 的直接子元素，并紧随 charset 声明之后。
title 中如果包含 ascii 之外的字符，浏览器需要知道字符编码类型才能进行解码，否则可能导致乱码。
2.	favicon
1)	保证 favicon 可访问。
在未指定 favicon 时，大多数浏览器会请求 Web Server 根目录下的 favicon.ico 。为了保证favicon可访问，避免404，必须遵循以下两种方法之一：
i.	在 Web Server 根目录放置 favicon.ico 文件。
ii.	使用 link 指定 favicon。
示例：<link rel="shortcut icon" href="path/to/favicon.ico">
3.	Viewport
若页面欲对移动设备友好，需指定页面的 viewport。

(五)	图片
---------------------

1.	禁止 img 的 src 取值为空
延迟加载的图片也要增加默认的 src。src 取值为空，会导致部分浏览器重新加载一次当前页面。
2.	避免为 img 添加不必要的 title 属性
多余的 title 影响看图体验，并且增加了页面尺寸。
3.	为重要图片添加 alt 属性
可以提高图片加载失败时的用户体验。
4.	建议添加 width 和 height 属性，以避免页面抖动
5.	有下载需求的图片采用 img 标签实现，无下载需求的图片采用 CSS 背景图实现。
1)	产品 logo、用户头像、用户产生的图片等有潜在下载需求的图片，以 img 形式实现，能方便用户下载。
2)	无下载需求的图片，比如：icon、背景、代码使用的图片等，尽可能采用 css 背景图实现。

(六)表单
---------------------

1.	控件标题
1)	有文本标题的控件必须使用 label 标签将其与其标题相关联。
2)	有两种方式：
i.	将控件置于 label 内。
ii.	label 的 for 属性指向控件的 id。
<label><input type="checkbox" name="confirm" value="on"> 我已确认上述条款</label>
<label for="username">用户名：</label> <input type="textbox" name="username" id="username">
2.	按钮
1)	使用 button 元素时必须指明 type 属性值。
button 元素的默认 type 为 submit，如果被置于 form 元素中，点击后将导致表单提交。为显示区分其作用方便理解，必须给出 type 属性。
2)	尽量不要使用按钮类元素的 name 属性。
由于浏览器兼容性问题，使用按钮的 name 属性会带来许多难以发现的问题。
3)	负责主要功能的按钮在 DOM 中的顺序应靠前。
负责主要功能的按钮应相对靠前，以提高可访问性。如果在 CSS 中指定了 float: right 则可能导致视觉上主按钮在前，而 DOM 中主按钮靠后的情况。
4)	建议当使用 JavaScript 进行表单提交时，如果条件允许，应使原生提交功能正常工作。
当浏览器 JS 运行错误或关闭 JS 时，提交功能将无法工作。如果正确指定了 form 元素的 action 属性和表单控件的 name 属性时，提交仍可继续进行。
5)	建议在针对移动设备开发的页面时，根据内容类型指定输入框的 type 属性。
根据内容类型指定输入框类型，能获得能友好的输入体验。
示例：<input type="date">

(七)多媒体
---------------------

1.	当在现代浏览器中使用 audio 以及 video 标签来播放音频、视频时，应当注意格式
音频应尽可能覆盖到如下格式：
MP3、WAV、Ogg
视频应尽可能覆盖到如下格式：
MP4、WebM、Ogg
2.	在支持 HTML5 的浏览器中优先使用 audio 和 video 标签来定义音视频元素
3.	使用退化到插件的方式来对多浏览器进行支持
<audio controls>
    <source src="audio.mp3" type="audio/mpeg">
    <source src="audio.ogg" type="audio/ogg">
    <object width="100" height="50" data="audio.mp3">
        <embed width="100" height="50" src="audio.swf">
    </object>
</audio>

<video width="100" height="50" controls>
    <source src="video.mp4" type="video/mp4">
    <source src="video.ogg" type="video/ogg">
    <object width="100" height="50" data="video.mp4">
        <embed width="100" height="50" src="video.swf">
    </object>
</video>
4.	只在必要的时候开启音视频的自动播放
5.	在 object 标签内部提供指示浏览器不支持该标签的说明
<object width="100" height="50" data="something.swf">DO NOT SUPPORT THIS TAG</object>

(八)	模板中的 HTML
---------------------

### 1.模板代码的缩进优先保证 HTML 代码的缩进规则

	{if $display == true}
	
	<div>
		<ul>
		{foreach $item_list as $item}
			<li>{$item.name}<li>
		{/foreach}
		</ul>
	</div>
	
	{/if}
	
### 2.模板代码应以保证HTML单个标签语法的正确性为基本原则

	<!--好的示例-->
	<li class="{if $item.type_id == $current_type}focus{/if}">
		{ $item.type_name }
	</li>
	
	<!--不良示例-->
	<li {if $item.type_id == $current_type} class="focus"{/if}>
		{ $item.type_name }
	</li>

(九)	其它
---------------------

1.	使用规范化的html，可以使用W3C HTML validator之类的工具来进行检测

2.	建议省略ur地址中的 http: 或 https: 的部分 (只有http:和https:是可以省略) 
在引用样式表文件、脚本文件、图片以及其它媒体文件时，都可以这样做。
除非使用这两种协议都无法获取到资源，也就是说必须使用其它协议才能获取到资源的，就不能省略。这样做的好处是能减少文件的体积，而且还能避免一些相对url中混乱问题的产生。

3.	特殊符号
因为大于号（>）小于号（<）等符号以作为HTML的语法符号，所以，如果要在页面中显示这些特殊符号，就必须使用相应的HTML的代码表示，这些特殊符号对应的HTML代码称为字符实体。
常用的特殊符号和对应的字符实体表，这些字符实体都是以"&"开头，以英文分号"；"结束。
特殊符号        字符实体
   空格            &nbsp;
   大于号（>）     &gt;
   小于号（<）     &lt;
   引号（"）       &quot;
版权符号（©）   &copy;


二、	CSS书写规范
---------------------

### (一)注释
1.	文件顶部注释
 
2.	单行注释
单行注释可以单独写在一行，也可以写在行尾
3.	整段注释
/* 模块样式 */
模块的样式...
/* 模块样式结束 */
4.	特殊注释
用于标注修改、待办等信息。
/* TODO: xxxx by zehua.Ye 2015-07-27 02:05 */
/* BUGFIX: xxxx by zehua.Ye 2015-07-27 02:05 */

### (二)	代码风格

1.	一个tab设置为四个空格宽度
2.	规则可以写成单行，或者多行，但是整个文件内的规则排版必须统一
如果是在html中写内联的css，则必须写成单行。
3.	单行形式书写风格的排版约束
1)	每一条规则的大括号 { 前后加空格
2)	多个selector共用一个样式集，则多个selector必须写成多行形式
3)	每一条规则结束的大括号 } 前加空格。
4)	属性名冒号之前不加空格，冒号之后加空格
5)	每一个属性值后必须添加分号; 并且分号后空格
例如：
 
4.	多行形式书写风格的排版约束
1)	每一条规则的大括号 { 前添加空格。
2)	多个selector共用一个样式集，则多个selector必须写成多行形式。
3)	每一条规则结束的大括号 } 必须与规则选择器的第一个字符对齐。
4)	属性名冒号之前不加空格，冒号之后加空格。
5)	属性值之后添加分号; 
5.	其他规范
1)	使用单引号，不允许使用双引号 。
2)	如果使用CSS3的属性，如果有必要加入浏览器前缀，则按照 
-webkit- / -moz- / -ms- / -o-  
的顺序进行添加，标准属性写在最后，并且属性名称要对齐，例如： 
 
### (三)	书写规范

除16进制颜色和字体设置外，CSS文件中的所有的代码都应该小写。
1.	命名原则
1)	规则命名中，一律采用小写加中划线的方式，不允许使用大写字母或 _
2)	命名避免使用中文拼音，应该采用更简明有语义的英文单词进行组合。
3)	命名注意缩写，但是不能盲目缩写，具体请参见常用的CSS命名规则。
4)	建议使用类选择器，放弃ID选择器。
ID在一个页面中的唯一性导致了如果以ID为选择器来写CSS，就无法重用。
5)	避免class与id重名。
6)	id用于标识模块或页面的某一个父容器区域，名称必须唯一，不要随意新建id 
7)	class用于标识某一个类型的对象，命名必须言简意赅。
8)	尽可能提高代码模块的复用，样式尽量用组合的方式 。
9)	规则名称中不应该包含颜色（red/blue）、定位（left/right）等与具体显示效
果相关的信息。应该用意义命名，而不是样式显示结果命名。
.red{color:red}（错误） 
.important-news{color:red}（正确）
10)	禁止直接为html tag添加css样式设置。
11)	每一条规则应该确保选择器唯一，禁止直接为全局.nav/.header/.body等类设置属性。
12)	命名应简约而不失语义

	/* 反对：表现化的或没有语义的命名 */
	.m-abc .green2{}
	.g-left2{}
	
	/* 推荐：使用有语义的简短的命名 */
	.m-list .wrap2{}
	.g-side2{}

2.	命名规则
1)	特殊字符："-"连字符
"-"在本规范中并不表示连字符的含义。
她只表示两种含义：分类前缀分隔符、扩展分隔符，详见以下具体规则。
2)	分类的命名方法：使用单个字母+"-"为前缀
a.	重置（reset）和默认（base）（tags）：
消除默认样式和浏览器差异，并设置部分标签的初始样式，以减少后面的重复劳动！你可以根据你的网站需求设置！
b.	统一处理：
建议在这个位置统一调用背景图（这里指多个布局或模块或元件共用的图）和清除浮动（这里指通用性较高的布局、模块、元件内的清除）等统一设置处理的样式！
c.	布局（grid）（.g-）：
将页面分割为几个大块，通常有头部、主体、主栏、侧栏、尾部等！
d.	模块（module）（.m-）：
通常是一个语义化的可以重复使用的较大的整体！比如导航、登录、注册、各种列表、评论、搜索等！
e.	元件（unit）（.u-）：
通常是一个不可再分的较为小巧的个体，通常被重复用于各种模块中！比如按钮、输入框、loading、图标等！
f.	功能（function）（.f-）：
为方便一些常用样式的使用，我们将这些使用率较高的样式剥离出来，按需使用，通常这些选择器具有固定样式表现，比如清除浮动等！不可滥用！
g.	皮肤（skin）（.s-）：
如果你需要把皮肤型的样式抽离出来，通常为文字色、背景色（图）、边框色等，非换肤型网站通常只提取文字色！非换肤型网站不可滥用此类！
h.	状态（.z-）：
为状态类样式加入前缀，统一标识，方便识别，它只能组合使用或作为后代出现（.u-ipt.z-dis{}，.m-list li.z-sel{}），具体详见命名规则的扩展相关项。
示例：


	/* 重置 */
	div,p,ul,ol,li{margin:0;padding:0;}
	/* 默认 */
	strong,em{font-style:normal;font-weight:bold;}
	/* 统一调用背景图 */
	.m-logo a,.m-nav a,.m-nav em{background:url(images/sprite.png) no-repeat 9999px 9999px;}
	/* 统一清除浮动 */
	.g-bdc:after,.m-dimg ul:after,.u-tab:after{display:block;visibility:hidden;clear:both;height:0;overflow:hidden;content:'.';}
	.g-bdc,.m-dimg ul,.u-tab{zoom:1;}
	/* 布局 */
	.g-sd{float:left;width:300px;}
	/* 模块 */
	.m-logo{width:200px;height:50px;}
	/* 元件 */
	.u-btn{height:20px;border:1px solid #333;}
	/* 功能 */
	.f-tac{text-align:center;}
	/* 皮肤 */
	.s-fc,a.s-fc:hover{color:#fff;}

*注：在你样式中的选择器总是要以上面五类（.g-，.m-，.u-，.f-,.s-,.z-）开头，然后在里面使用后代选择器。
　　如果这五类不能满足你的需求，你可以另外定义一个或多个大类，但必须符合单个字母+"-"为前缀的命名规则，即 .x- 的格式。
　　特殊：.j-将被专用于JS获取节点，请勿使用.j-定义样式。
3)	后代选择器命名
a.	约定不以单个字母+"-"为前缀且长度大于等于2的类选择器为后代选择器，如：.item为m-list模块里的每一个项，.text为m-list模块里的文本部分：.m-list .item{}.m-list .text{}。
b.	一个语义化的标签也可以是后代选择器，比如：.m-list li{}
c.	不允许单个字母的类选择器出现，原因详见下面的“模块和元件的后代选择器的扩展类”。
d.	后代选择器不需要完整表现结构树层级，尽量能短则短。
e.	后代选择器不要在页面布局中使用，因为污染的可能性较大；
通过使用后代选择器的方法，你不需要考虑他的命名是否已被使用，因为它在当前模块或元件中生效，同样的样式名可以在不同的模块或元件中重复使用，互不干扰。
示例：


	/* 这里的.itm和.cnt只在.m-list中有效 */
	.m-list{margin:0;padding:0;}
	.m-list .itm{margin:1px;padding:1px;}
	.m-list .cnt{margin-left:100px;}
	
	/* 这里的.cnt和.num只在.m-page中有效 */
	.m-page{height:20px;}
	.m-page .cnt{text-align:center;}
	.m-page .num{border:1px solid #ddd;}

4)	相同语义的不同类命名
直接加数字或字母区分即可（如：.m-list、.m-list2、.m-list3等，都是列表模块，但是是完全不一样的模块）。
其他举例：.f-fw0、.f-fw1、.s-fc0、.s-fc1、.m-logo2、.m-logo3、u-btn、u-btn2等等。

5)	模块和元件的扩展类的命名方法
当A、B、C、...它们类型相同且外形相似区别不大，那么就以它们中出现率最高的做成基类，其他做成基类的扩展。
方法：+“-”+数字或字母（如：.m-list的扩展类为.m-list-1、.m-list-2等）。
补充：基类自身可以独立使用（如：class="m-list"即可），扩展类必须基于基类使用（如：class="m-list m-list-2"）。
如果你的扩展类是表示不同状态，那么你可以这样命名：u-btn-dis，u-btn-hov，m-box-sel，m-box-hov等等，然后像这样使用：class="u-btn u-btn-dis"。
如果网站可以不兼容IE6等浏览器，那么标识状态的方法也可以采取独立状态分类（.z-）方法：.u-btn.z-dis，.m-box.z-sel，然后像这样使用：class="u-btn z-dis"。

6)	模块和元件的后代选择器的扩展类
有时候模块内会有些类似的东西，如果你没有把它们做成元件和扩展，那么也可以使用后代选择器和扩展。
后代选择器：.m-login .btn{}。
后代选择器扩展：.m-login .btn-1{}，.m-login .btn-dis{}。
同样也可以采取独立状态分类（.z-）方法：.m-login .btn.z-dis{}，然后像这样使用：class="btn z-dis"。
注：此方法用于类选择器，直接使用标签做为选择器的则不需要使用此命名方法。
注：为防止后代选择器的扩展类和大类命名规范冲突，后代选择器不允许使用单个字母。
　　比如：.m-list .a{}是不允许的，因为当这个.a需要扩展的时候就会变成.a-bb，这样就和大类的命名规范冲突。

7)	分组选择器有时可以代替扩展方法
有时候虽然两个同类型的模块很相似，但是你希望他们之间不要有依赖关系，也就是说你不希望使用扩展的方法，那么你可以通过合并选择器来设置共性的样式。
使用本方法的前提是：相同类型、功能和外观都相似，写在同一片代码区域方便维护。
示例：

	/* 两个元件共性的样式 */
	.u-tip1,.u-tip2{}
	.u-tip1 .itm,.u-tip2 .itm{}
	/* 在分别是两个元件各自的样式 */
	/* tip1 */
	.u-tip1{}
	.u-tip1 .itm{}
	/* tip2 */
	.u-tip2{}
	.u-tip2 .itm{}

8)	防止污染和被污染
当模块或元件之间互相嵌套，且使用了相同的标签选择器或其他后代选择器，那么里面的选择器就会被外面相同的选择器所影响。
所以，如果你的模块或元件可能嵌套或被嵌套于其他模块或元件，那么要慎用标签选择器，必要时采用类选择器，并注意命名方式，可以采用.m-layer .layerxxx、.m-list2 .list2xxx的形式来降低后代选择器的污染性。
3.	属性编写顺序
推荐的样式编写顺序：
1)	显示属性 

	display/list-style/position/float/clear
	 
2)	自身属性（盒模型） width/height/margin/padding/border
3)	背景 background
4)	行高 line-height
5)	文本属性

	color/font/text-decoration/text-align/ 
	text-indent/vertical-align/white-space/content

6)	其他cursor/z-index/zoom
7)	CSS3属性 transform/transition/animation/box-shadow/border-radius
8)	如果属性间存在关联性，则不要隔开写。

	/* 这里的height和line-height有关联性 */
	.m-box{position:relative;height:20px;line-height:20px;padding:5px;color:#000;}
	
9)	链接<a>标签的样式请严格按照：a:link -> a:visited -> a:hover -> a:active（LoVeHAte）的顺序添加。
4.	选择器编写顺序
请综合考虑以下顺序依据：
•	从大到小（以选择器的范围为准）
•	从低到高（以等级上的高低为准）
•	从先到后（以结构上的先后为准）
•	从父到子（以结构上的嵌套为准）
以下仅为简单示范：

	/* 从大到小 */
	.m-list p{margin:0;padding:0;}
	.m-list p.part{margin:1px;padding:1px;}
	
	/* 从低到高 */
	.m-logo a{color:#f00;}
	.m-logo a:hover{color:#fff;}
	
	/* 从先到后 */
	.g-hd{height:60px;}
	.g-bd{height:60px;}
	.g-ft{height:60px;}
	
	/* 从父到子 */
	.m-list{width:300px;}
	.m-list .itm{float:left;}

5.	选择器等级（优先级）
a = 行内样式style。
b = ID选择器的数量。
c = 类、伪类和属性选择器的数量。
d = 类型选择器和伪元素选择器的数量。

选择器等级(a,b,c,d)

	style=””	1,0,0,0
	#wrapper #content {}	0,2,0,0
	#content .dateposted {}	0,1,1,0
	div#content {}	0,1,0,1
	#content p {}	0,1,0,1
	#content {}	0,1,0,0
	p.comment .dateposted {}	0,0,2,1
	div.comment p {}	0,0,1,2
	.comment p {}	0,0,1,1
	p.comment {
	}	0,0,1,1
	.comment {}	0,0,1,0
	div p {}	0,0,0,2
	p {}	0,0,0,1

然后从左到右逐位比较大小，数字大的CSS样式的优先级就高。
6.	性能优化
1)	合并margin、padding、border的-left/-top/-right/-bottom的设置，尽量使用短名称。
2)	选择器应该在满足功能的基础上尽量简短，减少选择器嵌套，查询消耗。但是一定要避免覆盖全局样式设置。
3)	注意选择器的性能，不要使用低性能的选择器，例如：

	div > * {}
	ul > li > a {} 
	body.profile ul.tabs.nav li a {}
	
4)	禁止在css中使用*选择符
5)	除非必须，否则，一般有class或id的，不需要再写上元素对应的tag，例如

	div#test { width:100px; } 
	
6)	0后面不需要单位，比如0px可以省略成0，0.8px可以省略成.8px 。
7)	如果是16进制表示颜色，则颜色取值应该大写。
8)	如果可以，颜色尽量用三位字符表示，例如#AABBCC写成#ABC 。
9)	如果没有边框时，不要写成border:0，应该写成border:none。
10)	尽量避免使用AlphaImageLoader 
11)	在保持代码解耦的前提下，尽量合并重复的样式，例如：
 
12)	background、font等可以缩写的属性，尽量使用缩写形式。

	background: color image repeat attachment position; 
	font: style weight size/lineHeight family; 
	
7.	CSS属性取值规范
现将font-size取值的单位类型约束如下：
1)	font-size必须以px或pt为单位，推荐用px（注：pt为打印版字体大小设置）。
2)	不允许使用xx-small/x-small/small/medium/large/x-large/xx-large等值。
3)	字体系列font-family 
为了对font-family取值进行统一，更好的支持各个操作系统上各个浏览器的兼容性，现将font-family统一约束如下：
i.	font-family不允许在业务代码中随意设置。
ii.	font-family目前取值为(待确定)：
 
8.	hack使用规范
重要原则：尽量少用hack，能不hack坚决不hack，不允许滥用hack。
如果需要使用hack，请参考以下hack方式：
区分规则
 
区分属性
 
9.	z-index取值规范（建议）
如果z-index取值比较随意，导致层相互覆盖的情况。这个具体的目前还无法确定，尽	量不要随便设置的过大。以下是参考：
头部导航区域  [1999 - 2100] 
页面主要内容区域 [-1 - 1997]
页面底部 [1999 - 2100] 
全站公共组件 [-1 - 1999]
全页面蒙层弹窗空间 [10000-11000]

(四)常用的 CSS 命名
---------------------

(五)其它
---------------------

1.	字体名称请映射成对应的英文名
例如：黑体(SimHei) 宋体(SimSun) 微软雅黑 (Microsoft Yahei)，如果字体名称中有空格，则必须加单引号。
2.	背景图片请合理使用csssprites，按照模块、业务、页面来划分均可
3.	css背景图片的文件类型请按照以下原则来保存：
1)	如果背景图片有动画，则保存成gif。
2)	如果没有动画，也没有半透明效果，则保存成png-8 。
3)	如果有半透明效果，则保存成png-24 。
4.	不要在html中加入标签来清理浮动，通过在浮动元素的父元素上添加.clearfix来清除浮动。
5.	为了SEO和页面可用性，请使用text-indent来隐藏文本内容
6.	制作csssprites时，尽量把颜色相近的图标放在一起，存储为png8格式，存储完以后还能用一些压缩工具进行无损压缩
7.	避免过小的背景图片平铺
8.	尽量少用!important
9.	避免使用非一次性expression
10.	文件名必须由小写字母、数字、中划线-组成
11.	浏览器特效情况
 

(六)	最佳实践
---------------------

1.	最佳选择器写法（模块）


	/* 这是某个模块 */
	.m-nav{}/* 模块容器 */
	.m-nav li,.m-nav a{}/* 先共性  优化组合 */
	.m-nav li{}/* 后个性  语义化标签选择器 */
	.m-nav a{}/* 后个性中的共性 按结构顺序 */
	.m-nav a.a1{}/* 后个性中的个性 */
	.m-nav a.a2{}/* 后个性中的个性 */
	.m-nav .z-crt a{}/* 交互状态变化 */
	.m-nav .z-crt a.a1{}
	.m-nav .z-crt a.a2{}
	.m-nav .btn{}/* 典型后代选择器 */
	.m-nav .btn-1{}/* 典型后代选择器扩展 */
	.m-nav .btn-dis{}/* 典型后代选择器扩展（状态） */
	.m-nav .btn.z-dis{}/* 作用同上，请二选一（如果可以不兼容IE6时使用） */
	.m-nav .m-sch{}/* 控制内部其他模块位置 */
	.m-nav .u-sel{}/* 控制内部其他元件位置 */
	.m-nav-1{}/* 模块扩展 */
	.m-nav-1 li{}
	.m-nav-dis{}/* 模块扩展（状态） */
	.m-nav.z-dis{}/* 作用同上，请二选一（如果可以不兼容IE6时使用） */
	
2.	统一语义理解和命名
1)	布局（.g-）
语义	命名	简写
文档	doc	doc
头部	head	hd
主体	body	bd
尾部	foot	ft
主栏	main	mn
主栏子容器	mainc	mnc
侧栏	side	sd
侧栏子容器	sidec	sdc
盒容器	wrap/box	wrap/box

2)	模块（.m-）、元件（.u-）
语义	命名	简写
导航	nav	nav
子导航	subnav	snav
面包屑	crumb	crm
菜单	menu	menu
选项卡	tab	tab
标题区	head/title	hd/tt
内容区	body/content	bd/ct
列表	list	lst
表格	table	tb
表单	form	fm
热点	hot	hot
排行	top	top
登录	login	log
标志	logo	logo
广告	advertise	ad
搜索	search	sch
幻灯	slide	sld
提示	tips	tips
帮助	help	help
新闻	news	news
下载	download	dld
注册	regist	reg
投票	vote	vote
版权	copyright	cprt
结果	result	rst
标题	title	tt
按钮	button	btn
输入	input	ipt

3)	功能（.f-）
语义	命名	简写
浮动清除	clearboth	cb
向左浮动	floatleft	fl
向右浮动	floatright	fr
内联块级	inlineblock	ib
文本居中	textaligncenter	tac
文本居右	textalignright	tar
文本居左	textalignleft	tal
垂直居中	verticalalignmiddle	vam
溢出隐藏	overflowhidden	oh
完全消失	displaynone	dn
字体大小	fontsize	fs
字体粗细	fontweight	fw

4)	皮肤（.s-）
语义	命名	简写
字体颜色	fontcolor	fc
背景	background	bg
背景颜色	backgroundcolor	bgc
背景图片	backgroundimage	bgi
背景定位	backgroundposition	bgp
边框颜色	bordercolor	bdc

5)	状态（.z-）
语义	命名	简写
选中	selected	sel
当前	current	crt
显示	show	show
隐藏	hide	hide
打开	open	open
关闭	close	close
出错	error	err
不可用	disabled	dis

3.	其它
1)	如果不确定模块的上下margin特别稳定，最好不要将它写到模块的类里，而是使用类的组合单独为上下margin挂用于边距的原子类（例如mt10、mb20）。模块最好不要混用margin-top和margin-bottom，统一使用margin-top。
2)	划分模块的技巧：
a.从视觉上进行划分，样式和功能相对独立且稳定的一部分就可以视为模块。
b.模块与模块之间尽量不要包含相同的部分，如果有相同部分，应将它们提取出来，拆分成一个独立的模块。
c.模块应在保证数量尽可能少的原则下，做到尽可能简单，以提高重用性。
3)	低权重原则——避免滥用子选择器。
使用子选择器，会增加css选择符的权重，css选择符的权重越高，样式越不容易被覆盖，越容易对其他选择符产生影响。为了保证样式容易被覆盖，提高可维护性，css选择符需要保证权重尽可能低。
(七)	典型错误
1.	不符合规范的选择器用法
1)	.class{}
不要以一个没有类别的样式作为主选择器，这样的选择器只能作为后代选择器使用，比如.m-xxx .class{}。
2)	.m-xxx div{}
不要以没有语义的标签作为选择器，这会造成大面积污染，除非你可以断定现在或将来你的这个选择器不会污染其他同类。
3)	.g-xxx .class{}
不要在页面布局中使用后代选择器，因为这个后代选择器可能会污染里面的元素。
4)	.g-xxx .m-yyy{}.g-xxx .u-yyy{}
不要用布局去控制模块或元件，模块和元件应与布局分离独立。
5)	.m-xxx .f-xxx{}.m-xxx .s-xxx{}
不要通过模块或其他类来重定义或修改或添加已经定义好的功能类选择器和皮肤类选择器。
6)	.m-xxx .class .class .class .class{}
不要将选择器写的过于冗长，这会额外增加文件大小并且限制了太小范围的选择器，使树形结构过于严格应用范围过于局限，建议3-4个长度之内写完。
选择器并不需要完整反映结构嵌套顺序，相反，能简则简。
7)	.m-xxx .m-yyy .zzz{}
不要越级控制，如果.zzz是.m-yyy的后代选择器，那么不允许.m-yyy之外的选择器控制或修改.zzz。
此时可以使用.m-yyy的扩展来修改.zzz，比如.m-yyy-1 .zzz{}。
2.	扩展类使用错误
扩展类必须和其基类同时使用于同一个节点。
错误：class="g-xxx g-yyy-1" class="m-xxx-1" class="u-xxx u-yyy-1" class="xxx-yyy"。
正确：class="g-xxx g-xxx-1" class="m-xxx m-xxx-1" class="u-yyy u-yyy-1" class="xxx xxx-yyy"。

三、	JS书写规范
---------------------

(一)	注释
1.	原则
1)	生涩的代码就没必要添加注释了，需要重写它们。
2)	所有的注释请使用英文或中文。
3)	注释必须与代码相关。
4)	大量的变量申明后必须跟随一段注释。
5)	注释需要说明的是代码段的用处，尤其是接下来的代码段。
6)	注释没有必要每行都添加。
2.	文件注释
文件注释位于文件的最前面，应包括文件的以下信息：
概要说明及版本（必须）
项目地址（开源组件必须）
版权声明（必须）
开源协议（开源组件必须）
版本号（必须）
修改时间（必须，至少写到日期）

	/*!
	 * jRaiser 2 Javascript Library
	 * waterfall - v1.0.0 (2013-03-15T14:55:51+0800)
	 * http://jraiser.org/ | Released under MIT license
	 */

	/*!
	 * kan.56.com - v1.1 (2013-03-08T15:30:32+0800)
	 * Copyright 2005-2013 56.com
	 */

如果文件内包含了一些开源组件，则必须在文件注释中进行说明。例如：

	/*!
	 * jRaiser 2 Javascript Library
	 * sizzle - v1.9.1 (2013-03-15T10:07:24+0800)
	 * http://jraiser.org/ | Released under MIT license
	 *
	 * Include sizzle (http://sizzlejs.com/)
	 */

3.普通注释
普通注释是为了帮助开发者和阅读者更好地理解程序，不会出现在API文档中。其中，单行注释以“//”开头；多行注释以“/*”开头，以“*/”结束。普通注释的使用需遵循以下规定。
1)	总是在单行注释符后留一个空格。例如：
	
	// this is comment
	
2)	总是在多行注释的结束符前留一个空格（使星号对齐）。例如：

	/*
		this is comment
	 */
	 
3)	不要把注释写在多行注释的开始符、结束符所在行。例如：

	/* start
		this is comment
	end*/
	
4)	不要编写无意义的注释。例如:

	// 初始化value变量为0
	var value = 0;
	
5)	如果某段代码有功能未实现，或者有待完善，必须添加“TODO”标记，“TODO”前后应留一个空格。例如:

	// TODO 未处理IE6-8的兼容性
	function setOpacity(node, val) {
		node.style.opacity = val;
	}
	
4.	文档注释
文档注释将会以预定格式出现在API文档中。它以“/**”开头，以“*/”结束，其间的每一行均以“*”开头（均与开始符的第一个“*”对齐），且注释内容与“*”间留一个空格。例如


	/**
	 * comment
	 */
	 
文档注释必须包含一个或多个注释标签。

1)	@module。声明模块，用法:

	/**
	 * 模块说明
	 * @module 模块名
	 */
	 
例如：

	/**
	 * Core模块提供最基础、最核心的接口
	 * @module Core
	 */

2)	@class。声明类，用法:

	/**
	 * 类说明
	 * @class 类名
	 * @constructor
	 */
	 
3)@class必须搭配@constructor或@static使用，分别标记非静态类与静态类。

	/**
	 * 节点集合类
	 * @class NodeList
	 * @constructor
	 * @param {ArrayLike<Element>} nodes 初始化节点
	 */
	 
4)	@method。声明函数或类方法，用法

	/**
	 * 方法说明
	 * @method 方法名
	 * @for 所属类名
	 * @param {参数类型} 参数名 参数说明
	 * @return {返回值类型} 返回值说明
	 */
	 
5)	没有指定@for时，表示此函数为全局或模块顶层函数。当函数为静态函数时，必须添加@static；当函数有参数时，必须使用@param；当函数有返回值时，必须使用@return。

	/**
	 * 返回当前集合中指定位置的元素
	 * @method
	 * @for NodeList
	 * @param {Number} [i=0] 位置下标。如果为负数，则从集合的最后一个元素开始倒数
	 * @return {Element} 指定元素
	 */
@param。声明函数参数，必须与@method搭配使用。
@property。声明类属性，用法：

	/**
	 * 属性说明
	 * @property {属性类型} 属性名
	 */
	 
(二)	代码风格
1.	一个tab设置为四个空格宽度
2.	变量声明
1)	所有的变量应该在使用前用var声明。
2)	var语句应该为方法体内的第一个语句。 每个变量声明应该自己占一行并有注释。它们应该按字母顺序排列。 

	var currentEntry; // currentyly selected table entry  
	var level;        // indentation level  
	var size;         // size of table
	
尽量少使用全局变量。隐式的全局变量应该从来不使用。

3.	方法声明 
所有的方法应该在它们使用前声明。内部方法应该位于var语句后面。这让哪些变量包含在它的scope里更清楚。
方法名和参数列表的“(”(左圆括号)之间不应该有空格。在“)”(右圆括号)和“{”(左大括号)之间有一个空格。 
方法体本身缩进4个空格。“}”(右大括号)应该和方法声明处对齐。 
  
如果一个方法字面量为匿名的，则在“function”和“(”(左圆括号)之间应该有一个空格。如果省略空格，则它可能看起来方法名是“function”，而这是错误的。
 
尽量少用全局方法。

4.	语句
1)	简单语句 
每行应该包含至少一个语句。在每个简单语句末尾添加一个“;”(分号)。注意一个给方法字面量或对象字面量赋值的赋值语句仍然是一个赋值语句，所以也必须以分号结尾。 
2)	复合语句
复合语句是包含一个用“{}”(大括号)包围语句列表的的语句。 
a.	包围的语句应该再缩进4个空格。 
b.	“{”(左大括号)应该位于开始复合语句的行的末尾。 
c.	“}”(右大括号)应该新起一行并且和相匹配的“{”所在那行的起始位置对齐 
d.	当语句是控制结构的一部分时，所有语句都应该用括号包围，即使是单行语句，例如if或for语句。这让添加语句更容易而且不会引起bug。 
5.	标签 
语句标签是可选的。只有如下语句需要被标签标识: while，do，for，switch。 
6.	return语句
具有值的return语句不应该使用“()”(圆括号)包围值。返回值表达式必须和return关键字在同一行从而避免插入分号。
7.	if语句 
if语句应该使用如下格式: 


	if (condition) {  
		statements
	}  
	  
	if (condition) {  
		statements 
	} else {  
		statements 
	}  
	  
	if (condition) {  
		statements 
	} else if (condition) {  
		statements 
	} else {  
		statements  
	} 
	
8.	for语句
for语句应该使用如下格式:


	for (initialization; condition; update) {  
		statements 
	}  
	  
	for (variable in object) {  
		statements 
	}  

第一种格式应该和数组使用。 
第二种格式应该和对象使用。注意添加到对象的prototype中的成员将被包含在遍历中。通过使用hasOwnProperty方法来区分对象自身成员。

	for (variable in object) {  
		if (object.hasOwnProperty()) {  
			statements 
		}  
	} 

9.	while语句 
	while语句应该使用如下格式: 


	while (condition) {  
		statements
	} 
	
10.	do语句 
do语句应该使用如下格式: 


	do {  
	    statements
	} while (condition); 
	
不像其它复合语句，do语句始终使用“;”(分号)结尾。

11.	switch语句
switch语句应该有如下格式: 


	switch (expression) {  
	case expression:  
		statements 
	default:  
		statements 
	} 
	
每个case和switch对齐，这避免了缩进过度。 每组语句(除了default)应该以break，return或者throw结束。不要fall through。 

12.	try语句 
try语句应该使用如下格式:


	try {  
		statements 
	} catch (variable) {  
		statements
	}  
	  
	try {  
		statements 
	} catch (variable) {  
		statements
	} finally {  
		statements 
	}  

13.	continue语句
不要使用continue语句。它会让方法的控制流程模糊。 

14.	with语句 
不要使用with语句。

15.	空格
空行通过将逻辑相关的代码放到一起来增加可读性。 
	空格应该用于如下情况:
1)	关键字后面跟“(”(左圆括号)时应该用一个空格隔开。
1.	while (true) {  
2)	方法名和方法的“(”(左圆括号)之间不要有空格。这利于区分关键字和方法调用。 
3)	所有的二元操作符，除了“.”(圆点)、“(”(左圆括号)和“[”(左中括号)，都应该使用一个空格来和操作数隔开。 
4)	一元操作符和操作数之间不应该使用空格隔开，除了操作符是一个单词时，如typeof。 
5)	for语句控制部分的每个“;”(分号)应该在后面跟一个空格。 
6)	每个“,”(逗号)后面应该跟一个空格。
16.	额外的建议  
1)	{}和[] 
使用{}替代new Object()。使用[]替代new Array()。 
当成员名字为连续的整数时使用数组。当成员名字为任意的字符串或名字时使用对象。 
2)	逗号操作符 
3)	不要使用逗号操作符，除了for语句的控制部分的严格使用。(这不适合逗号操作符，它应该用于对象字面量，数组字面量，var语句和参数 
列表。) 
4)	在JavaScript里块没有作用域，只有方法有作用域。不要使用块，除了复合语句一定需要用到外。
5)	赋值表达式 
6)	不要在if和while语句的条件部分做赋值。不要写不易懂的代码。 
7)	===和!==操作符 
始终使用===和!==操作符会更好。==和!=操作符会做类型强制转换。特别是，不要使用==来和“假”值做比较。 
8)	令人混淆的加和减 
注意不要在“+”后面跟“+”或“++”。这种模式令人混淆。在它们之间插入圆括号来让你的意图更清晰。


	total = subtotal + +myInput.value;  

	// is better written as  
	  
	total = subtotal + (+myInput.value);  
	
这样“+ +”就不会被读错成“++”。 

9)	邪恶的eval
eval方法是JavaScript里最滥用的特性。不要使用它。eval有别名。不要使用function构造函数。不要传递字符串给setTimeout或者setInterval。

(三)	命名规范
1.命名组成
命名应该由26个大小写字母(A .. Z, a .. z)，10个数字(0 .. 9)和_(下划线)组成。不要使用国际字符，因为它们可能不易读或者不能在 任何地方都能容易理解。不要在名字里使用$(美元符号)或\(反斜线符号)。 
2.不要使用_(下划线)作为名字的首字母
它有时被用来表示私有，但是它实际上不提供私有性。
3.大多数变量和方法名应该以小写字母开始
4.必须使用new前缀的构造函数应该以大写字母开始
5.全局变量应该全部使用大写字母

JavaScript没有宏或常量，所以没有多少要求使用大写字母来表示JavaScript的特性的场景。
