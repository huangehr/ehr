# 工程结构

整体工程使用Maven管理项目。项目模块包含两部分：公共包与服务。
	
## 公共包
公用包即以commons-开头的Maven模块，其Artifact Id为ehr-commons-开头。
	
以下为公共包介绍。
	
### commons-data-fastdfs

### commons-data-hbase

### commons-data-mysql

### commons-data-redis
	
## 服务

服务模块以svr-开头，其Artifact Id为svr-开头。
	
### svr-address

### svr-app

### svr-archive

### svr-configuration

### svr-archive

# 命名规则
### 公共包

公共包全部
	
### 服务

# 调用协议

## HTTP 协议 REST API

### 模块依赖

### 返回值机制

基于HTTP协议的特点，返回值机制分为正常与异常情况下两种业务流程。以下对两种业务流程分别进行了描述。

#### 正常业务流程

对于一个正常的API调用，其返回值就是一个代表业务实现的Java对象。并且HTTP的状态码为200。

#### 异常业务流程

返回 ErrorEcho 对象，由两部分构成：错误代码及错误消息。并且HTTP的状态为403，这个特点可用于捕捉所有出错的请求结果。
	
### 异常处理机制

#### 异常描述来源

异常的错误描述信息存储在“配置服务”中，以服务为单位分别配置。即对于地址服务来讲，其应用名称为svr-address，那么异常的错误描述就配置在svr-address.preperties中。
	
需要注意的是，错误描述可能包含一些错误参数，如错误来源等。因此，在错误描述中包含一些占位符用于动态格式化这些错误。
	
#### 异常抛出

#### 异常捕捉

## RPC 协议：Thrift API

### 模块依赖

- commons-thrift-service
- commons-thrift-server
- commons-thrift-client

### 异常处理机制

由于Thrift是基于语言的协议，因为异常处理也是与语言相关。



