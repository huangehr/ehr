部署
====================

概述
---------------------

本文档描述微服务平台部署所需工具及安装步骤。

工具
---------------------

微服务平台需要使用CMS，Docker，Tomcat作为运行环境。

### Docker

微服务平台使用Docker作为基础运行环境，减少分发及部署带来的重新配置问题。由于Spring Boot是基于Java开发的，所以需要使用Java镜像作为基础镜像。

#### Ubuntu环境

**安装Docker引擎**

1. 登录Ubuntu系统，并打开terminal窗口
2. 添加gpg key: 


	$ apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D

  
3. 使用你喜欢的编辑器，编辑*/etc/apt/sources.list.d/docker.list*文件。如果文件不存在，先创建一个
4. 删除所有内容，并将下列内容添加进文件：


	# Ubuntu Precise
	deb https://apt.dockerproject.org/repo ubuntu-precise main# Ubuntu Trusty
	deb https://apt.dockerproject.org/repo ubuntu-trusty main# Ubuntu Vivid
	deb https://apt.dockerproject.org/repo ubuntu-vivid main# Ubuntu Wily
	deb https://apt.dockerproject.org/repo ubuntu-wily main
	
7. 保存并关闭文件/etc/apt/sources.list.d/docker.list
8. 更新apt包索引


    $ apt-get update

9. 如果存在，更新旧的库


	$ apt-get purge lxc-docker*

10. 检查apt是否已经下载好docker引擎，并查看


	$ apt-cache policy docker-engine

	~# apt-cache policy docker-engine
    docker-engine:
      Installed: 1.8.3-0~precise
      Candidate: 1.8.3-0~wily
      Version table:
         1.8.3-0~wily 0
            500 https://apt.dockerproject.org/repo/ ubuntu-wily/main amd64 Packages
         1.8.3-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.8.3-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages
     *** 1.8.3-0~precise 0
            500 https://apt.dockerproject.org/repo/ ubuntu-precise/main amd64 Packages
            100 /var/lib/dpkg/status
         1.8.2-0~wily 0
            500 https://apt.dockerproject.org/repo/ ubuntu-wily/main amd64 Packages
         1.8.2-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.8.2-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages
         1.8.2-0~precise 0
            500 https://apt.dockerproject.org/repo/ ubuntu-precise/main amd64 Packages
         1.8.1-0~wily 0
            500 https://apt.dockerproject.org/repo/ ubuntu-wily/main amd64 Packages
         1.8.1-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.8.1-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages
         1.8.1-0~precise 0
            500 https://apt.dockerproject.org/repo/ ubuntu-precise/main amd64 Packages
         1.8.0-0~wily 0
            500 https://apt.dockerproject.org/repo/ ubuntu-wily/main amd64 Packages
         1.8.0-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.8.0-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages
         1.8.0-0~precise 0
            500 https://apt.dockerproject.org/repo/ ubuntu-precise/main amd64 Packages
         1.7.1-0~wily 0
            500 https://apt.dockerproject.org/repo/ ubuntu-wily/main amd64 Packages
         1.7.1-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.7.1-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages
         1.7.1-0~precise 0
            500 https://apt.dockerproject.org/repo/ ubuntu-precise/main amd64 Packages
         1.7.0-0~vivid 0
            500 https://apt.dockerproject.org/repo/ ubuntu-vivid/main amd64 Packages
         1.7.0-0~trusty 0
            500 https://apt.dockerproject.org/repo/ ubuntu-trusty/main amd64 Packages

11. 最后选定特定发行版的一个docker-engine版本进行安装即可


	$apt-get install docker-engine=1.7.1-0~precise

	
**安装Docker Compose**

使用Docker Compose可以加快镜像部署，一次性定义多个容器并运行。为你的应用或服务编写compose文件后，运行一条简单的命令即可完成部署。
compose可用于部署，测试及预配置环境，类似CI工作流。使用compose有三步：

- 使用Dockerfile定义你的应用环境
- 在docker-compose.yml中定义服务，这些服务将在独立的环境中运行
- 最后运行*docker-compose*命令，compose将运行整个应用

1.安装compose，OS X和64位的Linux用如下命令安装。

	$ curl -L https://github.com/docker/compose/releases/download/1.6.2/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose

	$ chmod +x /usr/local/bin/docker-compose

2. 编写docker-compose.yml

编写ehr微服务的compose的配置文件，文件名为docker-compose.yml，内容如下：

	discovery:
	  image: discovery:ehr
	  ports:
	   - "8761:8761"
	config:
	  image: config:ehr
	  ports:
		 - "1221:1221"
	  links:
	   - discovery
	dict:
	  image: dict:ehr
	  ports:
		 - "10050:10050"
	  links:
	   - discovery
	   - config

### CentOS环境

CentOS至少需要7.0才能运行Docker引擎，所以请先确保CentOS已经升级至7.0。

安装步骤略。

CMS
---------------------

微服务配置中心使用git管理全局配置，方便配置文件的管理与版本化。当然也可以使用svn工具。服务器上需要安装git工具，并初始化一个git库

**Ubuntu环境**

	$apt-get install git
	
	# 进入指定目录
	
	$git init
	
共享此目录给配置服务即可。

Tomcat
---------------------



镜像
---------------------

EHR微服务应用以Docker镜像形式提供，分为基础平台应用，网关及应用。当前为下列模块提供镜像：

[模块列表](../commons/modules.html)
