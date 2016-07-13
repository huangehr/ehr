# 说明
此次改造目的是减少微服务的运行内存，并支持多部署方式，即jar,war在dev,test,prod三种环境中的部署，以下是部署环境矩阵：

[部署环境与Maven配置](../../images/deploy-env.png)

其中，打勾表示此次改造后支持的部署模式，打叉表示暂时不支持，但稍做修改即可或未来可能会提供部署支持。
此次修改内容较多，如遇到问题，请及时提出讨论。

# 改造内容

- 增加ehr-parent-pom.xml全局模板 ，位于 ehr 目录。此POM用于管理EHR应用所有的包依赖（不含EHR所定义的公用包），插件依赖，版本定义，库定义及其他选项。
- 增加ehr-lib-parent-pom.xml公共包模板，定义微服务公用包所需要依赖，插件依赖。若有新增公用模块，请使用commons开头命名，并在此模板中增加子模块定义，并在ehr-ms-parent-pom.xml中添加依赖定义。
- 增加ehr-ms-parent-pom.xml微服务应用模板，定义微服务应用所需要的包依赖，插件依赖。微服务的Maven POM定义通过继承此模板实现。删除原commons-dependency-native的POM模板。
- 原commons-utils模块拆分出一个新的公共包：commons-web，定义微服务所需要的控制器，异常处理及REST参数处理工具。
- 为方便部署，ehr-ms-parent-pom.xml定义“说明”一节中描述的四种部署环境所需profile，部署时可通过如下方式指定profile
    - 以下Maven命令将会使用war包打包应用
    
        mvn -P test-war

    - Intellij可通过Maven Project面板切换profile
- svr-discovery与svr-configuration服务作为全局服务依赖，而tomcat启动服务是按内部顺序执行，为保障这两个服务先行启动，它们不使用tomcat部署，而使用jar模式部署。


# 编译

- 安装ehr-parent-pom，在Maven Project中执行install操作。
- 安装所有公用模块：ehr-lib-parent-pom，在Maven Project中执行install操作。
- 批量打包微服务：ehr-ms-parent-pom，在Maven Project中执行package操作。
- 单独打包微服务：在Maven Project中，执行微服务的package操作。
- 以上操作请注意选择的profile。


# 部署
## 使用Jar模式部署

此模式使用以下命令启动，不再赘述

    java -Djava.security.egd=file:/dev/./urandom -jar app.jar

## 使用War模式部署

War模式需要在应用服务器上预告安装并配置好tomcat服务器。微服务设计成独立端口调用模式，而tomcat若要支持多端口监听，需要分别为服务定义connector，具体步骤如下：

- 定义connector，修改tomcat_home/conf/server.xml配置文件，为了方便可以使用此模板作为配置基础，稍微修改即可使用，可避免很多配置陷阱。此次使用svr-app作为示例：


    <Service name="svr-app">
        <Connector port="10160" protocol="HTTP/1.1" connectionTimeout="20000"/>
        <Engine name="svr-app" defaultHost="localhost">
          <Host name="localhost"  appBase="ehr_apps/svr-app" unpackWARs="true" autoDeploy="true">
            <Context path="" docBase=""></Context>
            <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                   prefix="svr-app" suffix=".log"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
        </Engine>
    </Service>

- ehr_apps表示EHR应用所在顶级目录，此目录位于tomcat_home下
- 10160表示此应用监听的端口号，请换微服务所需要端口进行配置，端口参见：GIT，Appliction Port部分。
- svr-app部分全部与应用名称一致，但含义不一样。
    - 第一个表示微服务名称。
    - 第二个表示微服务所使用的Engine名称，此名称用于解决tomcat启动时的JMX冲突问题。
    - 第三个表示微服务所在的目录位置，请统一放在ehr_apps目录下。
    - 第四个表示微服务日志文件的名称，首次配置需要启动后方可在tomcat_home/logs目录下找到此文件。
- 首次部署，需在tomcat根目录下创建ehr_apps目录，然后创建svr-app目录。
- 源代码编译后，可以将target目录下的“svr-app-版本号.war”文件使用zip工具打开，将内容解压到ehr_apps/svr-app，即“META-INF，org，WEB-INF"三个目录。
- 首次部署，需要将svr-app/WEB-INF下的lib-provied所有包移动至tomcat_home/lib，这些包为公共依赖，以后若是有升级或引用新的第三方包才需要将lib-provieded下的包移动到tomcat_home/lib，否则只需要更新应用的class文件即可。
- 以下是配置四个服务后，tomcat_home/conf/server.xml的结构：


    <Service name="svr-app">
        <Connector port="10160" protocol="HTTP/1.1" connectionTimeout="20000"/>
        <Engine name="svr-app" defaultHost="localhost">
          <Host name="localhost"  appBase="ehr_apps/svr-app" unpackWARs="true" autoDeploy="true">
            <Context path="" docBase=""></Context>
            <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                   prefix="svr-app" suffix=".log"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
        </Engine>
      </Service>
      <Service name="svr-organization">
        <Connector port="10070" protocol="HTTP/1.1" connectionTimeout="20000"/>
        <Engine name="svr-organization" defaultHost="localhost">
          <Host name="localhost"  appBase="ehr_apps/svr-organization" unpackWARs="true" autoDeploy="true">
            <Context path="" docBase=""></Context>
            <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                   prefix="svr-organization" suffix=".log"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
        </Engine>
      </Service>
      <Service name="svr-pack-mgr">
        <Connector port="10140" protocol="HTTP/1.1" connectionTimeout="20000"/>
        <Engine name="svr-pack-mgr" defaultHost="localhost">
          <Host name="localhost"  appBase="ehr_apps/svr-pack-mgr" unpackWARs="true" autoDeploy="true">
            <Context path="" docBase=""></Context>
            <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                   prefix="svr-pack-mgr" suffix=".log"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
        </Engine>
      </Service>
      <Service name="svr-user">
        <Connector port="10120" protocol="HTTP/1.1" connectionTimeout="20000"/>
        <Engine name="svr-user" defaultHost="localhost">
          <Host name="localhost"  appBase="ehr_apps/svr-user" unpackWARs="true" autoDeploy="true">
            <Context path="" docBase=""></Context>
            <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
                   prefix="svr-user" suffix=".log"
                   pattern="%h %l %u %t &quot;%r&quot; %s %b" />
          </Host>
        </Engine>
      </Service>


# 总结
微服务使用Spring Boot框架，使用Jar模式运行的时候，会独立启动一个tomcat，并负责自己的内容，此方式虽然对应用的独立性有好处，但对硬件资源的消耗非常大。
鉴于Spring Boot的所有依赖被都可以被EHR微服务所共享，因此使用tomcat部署，并将公共依赖放置于tomcat_home/lib目录下，解决内存占用问题。
Spring Boot内存占用，参见Spring Boot Memory Performance。此次调整的方案是通过共享Jar解决。另外也可以通过深入的细节调优处理内存占用问题。

# 可能遇到的问题

Q：服务启动后，每个微服务都被启动两次
A：遇到此问题请参照第3节的部署，仔细检查。

Q：启动时出错，catalina.out日志中有JMX相关的错误
A：遇到此问题请参照第3节的部署，仔细检查。
