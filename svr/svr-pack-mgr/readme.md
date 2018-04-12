版本变更：
    MySQL -> ElasticSearch
1. MySQL:json_archives -> ElasticSearch:json_archives
    mapping: {"pwd":{"type":"string","index":"not_analyzed"},"remote_path":{"type":"string","index":"not_analyzed"},"receive_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"parse_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"finish_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"archive_status":{"type":"integer"},"message":{"type":"string","analyzer":"ik","search_analyzer":"ik"},"org_code":{"type":"string","index":"not_analyzed"},"client_id":{"type":"string","index":"not_analyzed"},"resourced":{"type":"integer"},"md5_value":{"type":"string","index":"not_analyzed"},"event_type":{"type":"integer"},"event_no":{"type":"string","index":"not_analyzed"},"event_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"patient_id":{"type":"string","index":"not_analyzed"},"fail_count":{"type":"integer"},"analyze_status":{"type":"integer"},"analyze_fail_count":{"type":"integer"},"analyze_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"}}
    index: json_archives
    type: info 

2. MySQL:archive_relation -> ElasticSearch:archive_relation
    mapping: {"name":{"type":"string","index":"not_analyzed"},"org_code":{"type":"string","index":"not_analyzed"},"org_name":{"type":"string","index":"not_analyzed"},"id_card_no":{"type":"string","index":"not_analyzed"},"gender":{"type":"integer"},"telphone":{"type":"string","index":"not_analyzed"},"card_type":{"type":"string","index":"not_analyzed"},"card_no":{"type":"string","index":"not_analyzed"},"event_type":{"type":"integer"},"event_no":{"type":"string","index":"not_analyzed"},"event_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"sn":{"type":"string","index":"not_analyzed"},"relation_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"create_date":{"type":"date","format":"yyyy-MM-dd HH:mm:ss"},"apply_id":{"type":"integer"},"card_id":{"type":"integer"},"identify_flag":{"type":"integer"}}
	index: archive_relation
	type: info
	
	
	
	
	
	
	