FROM openjdk:8-jdk-alpine

COPY MMSServer.jar /app/mms/MMSServer.jar
COPY lib/ /app/mms/lib/

COPY start_mns.sh /app/mms/start_mns.sh

WORKDIR /app/mms

RUN ["chmod", "+x", "/app/mms/start_mns.sh"]
CMD ["/app/mms/start_mns.sh"]

