# 概述
该微服务专门用以定时将 redis 内存分析报告导入到 MySQL 中，作为微服务 svr-redis 统计 redis 内存用。


# 发布说明
该微服务需要发布在 redis 的服务器上。

需要事先在 redis 服务器上定义好配置文件中两个路径 rdbFilePath、outFilePath。


# 原由
由于生产环境服务器维护通过堡垒机管理，不允许代码直接通过SSH访问远程服务器。

而内存分析报告需要使用工具 rdb-redis-tools 的命令导出，所以没办法在 svr-redis 微服务中使用代码执行命令的方式。