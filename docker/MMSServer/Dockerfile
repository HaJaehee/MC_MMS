FROM openjdk:8-jdk-alpine

COPY MMSServer.jar /app/mms/MMSServer.jar
COPY lib/ /app/mms/lib/

COPY start_mms.sh /app/mms/start_mms.sh

COPY MMS-configuration/ /app/mms/MMS-configuration/

WORKDIR /app/mms

RUN ["chmod", "+x", "/app/mms/start_mms.sh"]
CMD ["/app/mms/start_mms.sh"]

