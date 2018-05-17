版本变更：
    MySQL -> ElasticSearch
1. MySQL:json_archives -> ElasticSearch:json_archives
    index: json_archives
    type: info 
    mapping: {"pwd":{"type":"string","index":"not_analyzed"},"remote_path":{"type":"string","index":"not_analyzed"},"receive_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"parse_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"finish_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"archive_status":{"type":"integer"},"message":{"type":"string","index":"not_analyzed"},"org_code":{"type":"string","index":"not_analyzed"},"client_id":{"type":"string","index":"not_analyzed"},"resourced":{"type":"integer"},"md5_value":{"type":"string","index":"not_analyzed"},"event_type":{"type":"integer"},"event_no":{"type":"string","index":"not_analyzed"},"event_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"patient_id":{"type":"string","index":"not_analyzed"},"fail_count":{"type":"integer"},"analyze_status":{"type":"integer"},"analyze_fail_count":{"type":"integer"},"analyze_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"profile_id":{"type":"string","index":"not_analyzed"},"re_upload_flg":{"type":"string","index":"not_analyzed"},"demographic_id":{"type":"string","index":"not_analyzed"},"pack_type":{"type":"integer"},"error_type":{"type":"integer"}}
    setting: {"index.max_result_window":"10000000","index.translog.flush_threshold_size":"1g","index.translog.flush_threshold_ops":"100000","index.translog.durability":"async","index.refresh_interval":"30s"}
    
2. MySQL:archive_relation -> ElasticSearch:archive_relation
	index: archive_relation
	type: info
    mapping: {"name":{"type":"string","index":"not_analyzed"},"org_code":{"type":"string","index":"not_analyzed"},"org_name":{"type":"string","index":"not_analyzed"},"id_card_no":{"type":"string","index":"not_analyzed"},"gender":{"type":"integer"},"telephone":{"type":"string","index":"not_analyzed"},"card_type":{"type":"string","index":"not_analyzed"},"card_no":{"type":"string","index":"not_analyzed"},"event_type":{"type":"integer"},"event_no":{"type":"string","index":"not_analyzed"},"event_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"sn":{"type":"string","index":"not_analyzed"},"relation_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"create_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"apply_id":{"type":"integer"},"card_id":{"type":"integer"},"identify_flag":{"type":"integer"},"profile_type":{"type":"integer"}}
	setting: {"index.max_result_window":"10000000","index.translog.flush_threshold_size":"1g","index.translog.flush_threshold_ops":"100000","index.translog.durability":"async","index.refresh_interval":"30s"}
	
	curl -X PUT 'http://172.19.103.9:9200/json_archives/_mapping/info' -d '{"info":{"properties":{"pack_type":{"type":"integer"}}}}'
	curl -X PUT 'http://172.19.103.9:9200/json_archives/_mapping/info' -d '{"info":{"properties":{"error_type":{"type":"integer"}}}}'
	curl -X PUT 'http://172.19.103.9:9200/json_archives/_settings' -d '{"index":{"max_result_window":"10000000"}}'
	curl -X PUT 'http://172.19.103.9:9200/archive_relation/_mapping/info' -d '{"info":{"properties":{"profile_type":{"type":"integer"}}}}'
	
	
	
	
	