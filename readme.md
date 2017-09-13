# EHR.cloud

EHR云平台项目，以微服务为业务基础容器。在介入本项目之前，您可能需要熟悉一些背景知识，工具及工作流组织。
以下链接可以帮您做一些入门引导，但绝大多数内容还需要在工作中才能掌握。

## 应用结构

### 开发框架及服务

EHR.cloud基于Spring Boot框架开发，由多个服务组成，服务分为两种：网关与微服务

- 网关以ag开头，负责对外提供服务
    - ag-ehr-admin：管理网关，提供全局管理接口
    - ag-ehr-cloud：开放网关，提供给第三方使用，如档案浏览器，互联网医院
- 微服务以svr开头，负责业务实现，并由网关调用

### 项目与源代码管理

项目使用maven管理，使用git作为代码管理工具，Intellij IDEA为IDE，请注意不要将与IDE相关的配置添加到代码管理中(git)，
以免给其人的IDE造成混淆。

## 环境配置

本项目基于Spring Cloud开发，所以请先熟悉Spring Could和Spring Boot开发框架以便您更快了解本节内容。

### 服务器配置

#### hadoop, hbase, solr服务

本项目需要hadoop, hbase, solr支持。请参照相关说明配置好hadoop, hbase, solr服务。

#### mysql服务

提供基础业务数据管理服务。

#### redis服务

本项目需要redis服务支持。在服务的交互过程中，redis将用于缓存数据和发送消息。为确保服务的正常运行，请确认redis服务可用。

#### git服务

本项目的代码与配置全部使用git管理，除了有集中管理的好处外，还可以对参数做版本化，方便跟踪。

本项目使用[gogs](https://gogs.io/) 作为git服务器。

### 环境变量配置

#### spring.profiles.active

EHR.cloud以Spring Boot框架开发，Spring Boot使用Profile模式配置在不同的环境中使用不同的参数，因此服务运行前需要在系统环境变量添加当前所需要的全局配置模板，
即配置spring.profiles.active变量，指定spring boot应用启动时加载哪个Profile的参数。例如，开发环境配置
```
spring.profiles.active=default,dev
```

其中default表示使用默认Profile，dev表示使用dev Profile。default,dev表示混合两种Profile。

本项目的Profile如下：
- default: 默认配置，定义不与具体环境依赖的通用参数，如：应用程序名称，连接池数量，连接超时时间等
- dev: 开发配置，定义开发环境中的服务位置，IP，数据库连接等
- alpha: 开发后交予测试之前的配置，由于开发环境的问题较多，如果联合调试时使用开发配置，会导致联合开发困难
- test: 测试配置，与dev定义内容相同，只是与测试的环境相关
- prod: 生产配置，与dev定义内容相同，只是与生产的环境相关

不同的OS环境配置变量方法不一样，请在部署前确保此环境变量已经生效。
在*nix系统中，环境变量不能使用`.`分隔，需要将`spring.profiles.active`替换为`spring_profiles_active`。

#### 部署前需要准备的参数

在运行服务之前，需要对参数做修改，以便您在运行微服务时出错不会感到束手无解，当然如果您对Spring Boot框架了解，您可以轻松理解本节。
系统整体环境参数在git中维护，因此您需要在git环境中修改参数，并提交到git服务器使configuration服务能够正确读取到这些参数。

#### JDK

请使用JDK1.8版本。若使用OpenJDK，请注意是日志是否正确。

## 安装与运行

在您在Git上取得代码后，若上述环境已经配置好，请按以下步骤执行maven命令（请确保mvn在系统路径中），本节描述的是在开发环境中的执行。

### 安装全局Pom依赖

进入源代码根目录，执行命令：
```
mvn install
```

此命令将安装其他服务所需要的配置依赖。

### 安装公用jar包

进入ehr-lib-parent-pom目录，执行命令：
```
mvn install
```
首次执行可能会从网络下载所需要的第三方依赖，请耐心等待。

### 服务编译

服务编译可以通过ehr-ms-parent-pom目录，执行命令：
```
mvn compile
```

即可全部编译。若要打包这些服务，可以通过命令：
```
mvn clean compile package
```

根据maven的profile，默认会使用dev-jar模式打包。

## 工作流

- [Git工作流](docs/markdown/workflow/git-workflow.md)

## 重大重构历史

- [微服务内存占用过高解决方案及部署说明](docs/markdown/refactor/multi-deploy-env-refactor.md)