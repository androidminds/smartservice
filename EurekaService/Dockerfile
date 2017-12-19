# Name:     EurekaService
# Time:     2017-12-18

FROM java:8

MAINTAINER Richard Liu <richard@androidminds.cn>

ADD EurekaService-0.0.1.jar app.jar

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8761