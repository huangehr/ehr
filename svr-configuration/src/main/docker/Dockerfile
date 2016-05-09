# 此docker档案基于 java:8
# Author: Sand Wen
FROM java:8

MAINTAINER Sand sand.fj.wen@gmail.com

ENV spring.profiles.active default,dev

VOLUME /tmp

ADD ehr-config-svr-1.1.0.jar app.jar

RUN bash -c 'touch /app.jar'

EXPOSE 0.0.0.0:1221:1221

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]