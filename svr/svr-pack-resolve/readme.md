#  版本变更：
#### 1.创建 HealthFile 表 ,列族 basic,d
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' 'http://172.17.110.227:10220/api/v1.0/createTable?tableName=HealthFile&columnFamilies=basic%2Cd' 
#### 2.创建 HealthFileSub 表,列族 basic,d   
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' 'http://172.17.110.227:10220/api/v1.0/createTable?tableName=HealthFileSub&columnFamilies=basic%2Cd'
#### 3.配置HBase集群replication时需要将该参数设置为1.   
    1.alter 'HealthFile', NAME => 'basic',  REPLICATION_SCOPE => '1' 
	2.alter 'HealthFile' , NAME => 'd',  REPLICATION_SCOPE => '1'
	3.alter 'HealthFileSub',  NAME => 'basic',  REPLICATION_SCOPE => '1' 
	4.alter 'HealthFileSub' , NAME => 'd',  REPLICATION_SCOPE => '1' 