备注：<> 指变量

ElasticSearch
    a.服务说明：
        1.该服务是对ElasticSearch搜索引擎的集成，提供了一些相对常用的操作接口
    b.配置说明：
	    2.详见application.yml文件，其中如果有多个cluster-nodes请以逗号(,)分隔
    c.调用说明：
	    1.初始化索引（相当于建立数据库表）
		    1).索引文档之前请勿必请求mapping接口进行字段映射，不然可能发生检索结果不准确的情况
		    2).参数
			    index
				    - 参数格式："<index>"
				    - 参数说明：索引名称
			    type 
				    - 参数格式："<type>"
				    - 参数说明：索引类型
			    source
				    - 参数格式：["<field>":{"type":"<string|byte|short|integer|long|float|double|boolean|date>","store":"<yes|or>"},"<...>":{<...>}]
				    - 参数说明：
                        因{"...":{...}}格式的字符串FeignClient请求的时候会出错，故用[]替换{}到后台的时候再进行替换处理
                        field值为字段名称，字段可选属性还有index、analyzer、search_analyzer、format
                        当字段不分词时形如：["name":{"type":"string","index":"not_analyzed"}]
                        当字段要分词时形如：["name":{"type":"string","analyzer":"ik","search_analyzer":"ik"}]
                        当字段为date类型时可指定时间格式：["createDate":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"}]
                        当字段特别长的时候可选择store为yes否则默认为no
        2.索引文档（相当于插入数据）
            1).请求index接口
            2).参数
                index
                    - 同上
                type
                    - 同上
                source
                    - 参数格式：{"<field>":"<value>","<field>":<value>,"field":"<yyyy-MM-dd HH:mm:ss>"}
                    - 参数说明：filed指字段名称，value指字段值，其中时间格式为yyyy-MM-dd HH:mm:ss
        3.删除索引（相当于删除数据）
            1).请求delete接口
            2).参数
                index
                    - 同上
                type
                    - 同上
                id
                    - 参数格式："id1,id2,..."
                    - 参数说明：多个数值以逗号(,)分隔
        4.更新索引（相当于更新数据）
            1).请求update接口
            2).参数
                index
                    - 同上
                type
                    - 同上
                id
                    - 参数格式："<id>"
                    - 参数说明：原文档ID
                source
                    - 参数格式：同index接口
                    - 参数说明：数据中请勿包含原文档ID(_id)
        5.获取单条数据
            1).请求findById接口
            2).参数
                index
                    - 同上
                type
                    - 同上
                id
                    - 同上
        6.简单数据检索（findByFiled）
            1).请求findByField接口
            2).参数
                index
                    - 同上
                type
                    - 同上
                field
                    - 参数格式："<field>"
                    - 参数说明：字段名称
                value
                    - 参数格式：初始化索引时，该字段对应的类型
                    - 参数说明：字段值
        7.组合数据检索（page）
            1).请求page接口，支持模糊查询，完全匹配，范围查询
            2).参数
                index
                    - 同上
                type
                    - 同上
                filter
                    - 参数格式：[{"andOr":"and|or","condition":">|=|<|>=|<=|?"},"field":"<filed>","value":"<value>"},<{...}>]
                    - 参数说明：andOr跟数据库的中的AND和OR相似；condition指条件匹配程度，?相当于数据库中的like；filed指检索的字段；value为检索的值
                page
                    - 参数说明：页码
                size 
                    - 参数说明：分页大小
				
FastDFS
    a.服务说明：
        1.此服务在FastDFS的文件服务中加入了建立在ElasticSearch基础上的文件检索功能，以解决FastDFS在文件管理中的缺陷
        --- 索引信息 Start ---
        ["sn":{"type":"string","index":"not_analyzed"},"name":{"type":"string","analyzer":"ik","search_analyzer":"ik"},"path":{"type":"string","index":"not_analyzed"},"objectId":{"type":"string","index":"not_analyzed"},"size":{"type":"integer"},"type":{"type":"string","index":"not_analyzed"},"createDate":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"creator":{"type":"string","index":"not_analyzed"},"modifyDate":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"modifier":{"type":"string","index":"not_analyzed"}]
        [
            "sn": {
                "type": "string",
                "index": "not_analyzed"
            },
            "name": {
                "type": "string",
                "analyzer": "ik",
                "search_analyzer": "ik"
            },
            "path": {
                "type": "string",
                "index": "not_analyzed"
            },
            "objectId": {
                "type": "string",
                "index": "not_analyzed"
            },
            "size": {
                "type": "integer"
            },
            "type": {
                "type": "string",
                "index": "not_analyzed"
            },
            "createDate": {
                "type": "date",
                "format": "yyyy-MM-dd HH:mm:ss"
            },
            "creator": {
                "type": "string",
                "index": "not_analyzed"
            },
            "modifyDate": {
                "type": "date",
                "format": "yyyy-MM-dd HH:mm:ss"
            },
            "modifier": {
                "type": "string",
                "index": "not_analyzed"
            }
        ]
        --- 索引信息 End ---
    b.配置说明：
        1.详见application.yml文件，其中如果有多个tracker-server，请用逗号（,）分隔
        2.新增部署服务的时候请确保基础信息管理的系统字典里面有名称为"FastDFS外链地址"的字典项，并添加值为可用的http外链地址的字典值，可添加多个
    c.使用说明：
	
	
	
	
	
	
	
	
	
	
	
	
	