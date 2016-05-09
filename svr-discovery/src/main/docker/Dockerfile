# 此docker档案基于 java:8
# Author: Sand Wen
FROM java:8

MAINTAINER Sand sand.fj.wen@gmail.com

ENV spring.profiles.active default,dev
ENV TZ 'Asia/Shanghai'
ENV LC_ALL en_US.UTF-8

VOLUME /tmp

ADD ehr-discovery-svr-1.1.0.jar app.jar

RUN bash -c 'touch /app.jar'

EXPOSE 0.0.0.0:8761:8761

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]