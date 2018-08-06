FROM openjdk:8-jdk-alpine

COPY MMSServer.jar /app/mms/MMSServer.jar
COPY lib/ /app/mms/lib/

COPY start_mns_dummy.sh /app/mms/start_mns_dummy.sh

WORKDIR /app/mms

CMD ["/app/mms/start_mns_dummy.sh"]

