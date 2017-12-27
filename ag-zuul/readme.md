备注：<> 指变量

Ag-Zuul
    a.服务说明：
        1.该服务的主要功能是路由转发，根据不同的请求路径将其分发到相应的微服务上面，并且不会出现POST请求通过FeignClient调用的时候会将参数拼接在URL后面的情况
    b.配置说明：
        1. 
            zuul:
              routes:
                svr-dfs:                 #该节点配置微服务名称
                  path: /svr-dfs/**      #该节点为相应的请求路径
                  serviceId: svr-dfs     #该节点为注册到Eureka上的微服务的服务名称(要将请求转发到自己的服务的时候可将其更改为自己的服务的名称[bootstrap.yml])
    c.请求方式：
        请求svr-dfs: http://localhost:10002/svr-dfs/api/v1.0/fastDfs/page?page=1&size=15
        请求svr-redis: http://localhost:10002/svr-redis/api/v1.0/redis/orgName?key=jkzl
        请求svr-resource: http://localhost:10002/svr-resource/api/v1.0/resources/64
    d.注意项：
        1.用GET请求的时候参数不能携带'{,}'字符，不然会出现参数解析错误的情况，暂未找到解决办法
        2.其他待测试
    
    
	        
	
	
	
	
	
	
	
	
	