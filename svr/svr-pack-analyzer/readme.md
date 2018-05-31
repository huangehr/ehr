-- 质控统计 elasticsearch 新增mapping
orgCode 机构代码,
orgName 机构名称,
eventType 类型,
createTime 创建时间,
hospitalDataset 医院数据集,
hospitalArchives 医院档案数,
receiveDataset 接收数据集,
receiveArchives 接收档案数,
receiveException 接收质量异常数,
resourceSuccess 资源化-解析成功数,
resourceFailure 资源化-解析失败数,
resourceException 资源化-解析异常

curl -XPUT http://url/data_quality_index
{
        "mappings": {
            "data_quality_type": {
                "properties": {
                    "orgCode": {
                        "type": "string"
                    },
                    "orgName": {
                        "type": "string"
                    },
                    "eventType": {
                        "type": "string"
                    },
                    "createTime": {
                        "type": "date",
                        "format": "yyyy-MM-dd HH:mm:ss"
                    },
                    "hospitalDataset": {
                        "type": "string"
                    },
                    "hospitalArchives": {
                        "type": "string"
                    },
                    "receiveDataset": {
                        "type": "string"
                    },
                    "receiveArchives": {
                        "type": "string"
                    },
                    "receiveException": {
                        "type": "string"
                    },
                    "resourceSuccess": {
                        "type": "string"
                    },
                    "resourceFailure": {
                        "type": "string"
                    },
                    "resourceException": {
                        "type": "string"
                    }
                }
            }
        }
}

    index: data_quality_index
	type: data_quality_type
    mappings: {"orgCode": {"type": "string"},"orgName": {"type": "string"},"eventType": {"type": "string"},"createTime": {"type": "date","format": "yyyy-MM-dd HH:mm:ss"},"hospitalDataset": {"type": "string"},"hospitalArchives": {"type": "string"},"receiveDataset": {"type": "string"},"receiveArchives": {"type": "string"},"receiveException": {"type": "string"},"resourceSuccess": {"type": "string"},"resourceFailure": {"type": "string"},"resourceException": {"type": "string"}}
	setting: {"index.max_result_window":"10000000","index.translog.flush_threshold_size":"1g","index.translog.flush_threshold_ops":"100000","index.translog.durability":"async","index.refresh_interval":"30s"}
    curl -XPUT 'http://172.19.103.9:9200/upload' -d '{"settings":{"index.max_result_window":"10000000","index.translog.flush_threshold_size":"1g","index.translog.flush_threshold_ops":"100000","index.translog.durability":"async","index.refresh_interval":"30s"}, "mappings": {"data_quality_type": {"properties": {"orgCode": {"type": "string"},"orgName": {"type": "string"},"eventType": {"type": "string"},"createTime": {"type": "date","format": "yyyy-MM-dd HH:mm:ss"},"hospitalDataset": {"type": "string"},"hospitalArchives": {"type": "string"},"receiveDataset": {"type": "string"},"receiveArchives": {"type": "string"},"receiveException": {"type": "string"},"resourceSuccess": {"type": "string"},"resourceFailure": {"type": "string"},"resourceException": {"type": "string"}}}}'


