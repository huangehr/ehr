档案调阅
====================

> 作者：温富建，2016.02.18

概述
---------------------

本文档介绍健康档案调阅模式及档案类别。

根据使用对象的不同，健康档案分为个人版与医护版。其中，个人版立足于患者角度，提供个人健康档案的管理及浏览，医护版立足于医疗专业人员，
为医疗专业人员提供定制化的患者健康数据分析。

平台提供Web页面及API调阅模式。其中，Web页面模式由健康档案浏览器提供，可嵌入第三方应用中，
API模式提供档案数据模型，由第三方自行组织展示。

个人版档案
---------------------

个人版档案访问方式：

- API方式（参见：API列表），此方式返回与患者档案相关的数据，第三方需要自行组织展示。
- 在第三方应用中嵌入健康档案浏览器页面。访问URL如下：

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

医护版访问方式：

- API方式：暂时不提供接口。
- 在第三方应用中嵌入健康档案浏览器页面（未完成）。

API列表
---------------------

以下为当前开放的档案接收接口，后续将会开放更多的接口。

### 健康事件列表（时间轴）与事件文档列表

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
              "cdaDocumentId": "0dae000656254674bed9a905c253de37",
              "cdaDocumentName": "门诊病历",
              "orgCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001293827_1443489861000"
            },
            {
              "cdaVersion": "000000000000",
              "cdaDocumentId": "0dae00065625491ebed9a905c253debc",
              "cdaDocumentName": "门诊处方",
              "orgCode": "41872607-9",
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
              "cdaDocumentId": "0dae00065618852549f6321eb42ce5e7",
              "cdaDocumentName": "检查报告",
              "orgCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001291948_1443402097000"
            },
            {
              "cdaVersion": "000000000000",
              "cdaDocumentId": "0dae0006561c7e3949f6320b6c60f07f",
              "cdaDocumentName": "检验报告",
              "orgCode": "41872607-9",
              "archiveKey": "41872607-9_11603508_1001291948_1443402097000"
            ｝
          ]
        }
      ]
    }
    
返回值中的archiveKey表示档案ID。

### 获取文档

	GET /personal-profile/document/datasets

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

