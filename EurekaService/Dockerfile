

FROM java:8

MAINTAINER Richard Liu <richard@androidminds.cn>

ADD EurekaService-1.0.0.jar app.jar

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8761