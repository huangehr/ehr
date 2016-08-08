档案调阅
====================

> 作者：Sand，2016.02.18

概述
---------------------

本文档介绍健康档案调阅模式及档案类别。

健康档案提供两种调阅模式：前端页面及API。根据使用对象的不同，前端页面又分为个人版与医护版，可嵌入第三方应用中。其中，个人版立足于患者角度，提供个人健康档案的管理及浏览。
医护版立足于医疗专业人员，为医疗专业人员提供定制化的患者健康数据分析。

档案
---------------------

### 个人版档案

访问URL：

	GET /patient/profiles

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
		<td>患者身份证号，使用Base64编码</td>
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

### 医护版档案

访问URL：

	未提供
	
档案搜索
---------------------

档案搜索提供健康档案搜索功能。为了防止档案被误认领或冒领，目前开放以下字段进行搜索，若匹配的结果大于1，则不会返回具体的档案内容，并且会返回*404 Not Found*错误。

	GET /patient/health-profiles/search

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>demographic_id</td>
		<td>string</td>
		<td>患者身份证号，使用Base64编码。若不为空，优先使用此字段</td>
	</tr>
	<tr>
		<td>org_code</td>
		<td>string</td>
		<td>机构代码</td>
	</tr>
	<tr>
		<td>card_no</td>
		<td>string</td>
		<td>就诊卡号（医保/社保卡、居民健康卡、院内临时卡）</td>
	</tr>
	<tr>
		<td>name</td>
		<td>string</td>
        <td>姓名</td>
	</tr>
	<tr>
		<td>gender</td>
		<td>string</td>
        <td>性别</td>
	</tr>
	<tr>
		<td>mobile</td>
		<td>string</td>
        <td>手机号</td>
	</tr>
	<tr>
		<td>native_place</td>
		<td>string</td>
        <td>籍贯</td>
	</tr>
</table>

API列表
---------------------

以下为当前开放的档案接收接口，后续将会开放更多的接口。

### 健康事件列表（时间轴）与文档列表

	GET /patient/health-profiles/timeline
	
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
		<td>患者身份证号，使用Base64编码</td>
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

**返回值**

    {
      "events": [
        {
          "date": "2015-11-05 9:30:56",
          "org_code": "41872607-9",
          "org_name": "郸城县人民医院",
          "summary": "门诊",
          "docs": [
            {
              "cdaVersion": "000000000000",
              "id": "0dae000656254674bed9a905c253de37",
              "name": "门诊病历",
              "organizationCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001293827_1443489861000"
            },
            {
              "cdaVersion": "000000000000",
              "id": "0dae00065625491ebed9a905c253debc",
              "name": "门诊处方",
              "organizationCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001293827_1443489861000"
            }
          ]
        },
        {
          "date": "2015-09-05 15:30:56",
          "org_code": "41872607-9",
		  "org_name": "郸城县人民医院",
          "summary": "门诊",
          "docs": [
            {
              "cdaVersion": "000000000000",
              "id": "0dae00065618852549f6321eb42ce5e7",
              "name": "检查报告",
              "organizationCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001291948_1443402097000"
            },
            {
              "cdaVersion": "000000000000",
              "id": "0dae0006561c7e3949f6320b6c60f07f",
              "name": "检验报告",
              "organizationCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001291948_1443402097000"
            ｝
          ]
        }
      ]
    }
    
返回值中的archiveKey表示档案ID。

### 获取文档

	GET /patient/health-profiles/{id}
	
**参数**

<table>
	<tr>
		<td>名称</td>
		<td>类型</td>
		<td>描述</td>
	</tr>
	<tr>
		<td>id</td>
		<td>string</td>
		<td>档案ID</td>
	</tr>
</table>

### 获取数据集

	GET /patient/health-profiles/document/datasets

**参数**

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

**返回值**

    {
        "HDSA00_01":[
            {
                "HDSA00_01_009":"朱蕊连",
                "HDSA00_01_010":"",
                "HDSA00_01_011_CODE":"2",
                "HDSA00_01_011_VALUE":"女性",
                "HDSA00_01_012":"1940-07-19 00:00:00",
                "HDSA00_01_014_CODE":"1",
                "HDSA00_01_014_VALUE":"汉族",
                "HDSA00_01_015_CODE":"10",
                "HDSA00_01_015_VALUE":"未婚",
                "HDSA00_01_017":"412726194007158467",
                "HDSA00_01_018":"",
                "HDSA00_01_019":"0",
                "HDSA00_01_036":"郸城县特种合金厂",
                "HDSA00_01_050":""
            }
        ]
    }

### 档案搜索

	GET /patient/health-profiles/search
	
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
		<td>身份证号，使用Base64编码</td>
	</tr>
	<tr>
		<td>name</td>
		<td>string</td>
		<td>患者就诊时填写的姓名</td>
	</tr>
	<tr>
		<td>birthday</td>
		<td>string</td>
		<td>患者就诊时填写的生日，yyyy-MM-dd格式</td>
	</tr>
	<tr>
		<td>gender</td>
		<td>string</td>
        <td>患者性别，1表示女性，2表示男性</td>
	</tr>
	<tr>
		<td>mobile</td>
		<td>string</td>
		<td>患者就诊时填写的手机号</td>
	</tr>
</table>

参数使用以下几种模式，按顺序搜索：

1. 使用身份证号搜索
2. 使用姓名与手机号搜索档案
3. 使用姓名，性别，生日搜索档案


	// 身份证号搜索参数，使用Base64编码
	{
		"demographic_id": MTIzNDU2Nzg5MDEzMjQ1Njc4
	}
	
	// 姓名+手机号搜索参数
	{
		"name": "朱蕊",
		"mobile": "13545678901"
	}

	// 姓名+性别+生日搜索参数 
	{
		"name": "朱蕊",
		"gender": "1",
		"birthday": "1975-01-02"
	}

若找到符合条件的档案，将返回这些档案ID（注意，若后面两种搜索模式匹配多份档案，将返回404 NOT FOUND）。应用根据需要再使用相应的接口获取数据。