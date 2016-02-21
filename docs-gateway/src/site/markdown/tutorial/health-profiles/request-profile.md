档案调阅
====================

> 作者：温富建，2016.02.18

本文档描述健康档案网关档案调阅逻辑，包括档案模型获取，档案结构及轻量级档案。不同档案的调阅及获取逻辑均不同，使用时请注意区别。

个人版档案
---------------------

个人版档案提供两种访问方式：

- API方式（参见：API列表），此方式返回的数据只包含基本数据，而不包含展示页面。
- 直接访问健康档案浏览器，并提供患者身份证号与时间段即可返回WEB页面。访问URL如下：


	GET /browser/personal-profile/index
	
**参数**

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>source</td>
		<td>string</td>
		<td>请求来源，PC或移动平台。值为：pc、pad、phone，默认为pc</td>
	</tr>
	<tr>
		<td>demographic_id</td>
		<td>string</td>
		<td>患者身份证号</td>
	</tr>
	<tr>
		<td>from</td>
		<td>string</td>
		<td>起始日期，格式：yyyy-MM-dd</td>
	</tr>
	<tr>
		<td>to</td>
		<td>string</td>
		<td>结束日期，格式：yyyy-MM-dd</td>
	</tr>
</table>

医护版档案
---------------------

医护版仅提供WEB页浏览。暂时不提供接口。

4 API列表
---------------------

以下为当前开放的档案接收接口，后续将会开放更多的接口。

## 时间轴

### 健康事件时间轴

	GET /personal-profile/timeline
	
**参数**

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>demographic_id</td>
		<td>string</td>
		<td>患者身份证号</td>
	</tr>
	<tr>
		<td>from</td>
		<td>string</td>
		<td>起始日期，格式：yyyy-MM-dd</td>
	</tr>
	<tr>
		<td>to</td>
		<td>string</td>
        <td>结束日期，格式：yyyy-MM-dd</td>
	</tr>
</table>

### 文档

	GET /personal-profile/document/datasets

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>cda_version</td>
		<td>string</td>
		<td>CDA数据标准版本</td>
	</tr>
	<tr>
		<td>data_set_list</td>
		<td>string</td>
		<td>数据集列表，JSON结构，包含一次档案请求中包含的数据集及相应的记录ID。最好直接使用*/personal-profile/timeline*方法接收到的数据集列表，不必再次组装</td>
	</tr>
	<tr>
		<td>origin_data</td>
		<td>boolean</td>
        <td>true获取原始档案数据，反之获取标准数据。对于非结构化文档，false时不可用</td>
	</tr>
</table>

## 康赛项目

获取指定时间段内的患者体征数据。

	GET /sanofi/archive/search
	
**参数**

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>demographic_id</td>
		<td>string</td>
		<td>患者身份证号</td>
	</tr>
	<tr>
		<td>from</td>
		<td>string</td>
		<td>起始日期，格式：yyyy-MM-dd</td>
	</tr>
	<tr>
		<td>to</td>
		<td>string</td>
		<td>结束日期，格式：yyyy-MM-dd</td>
	</tr>
</table>

### 体征数据提取

暂无。


