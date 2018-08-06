FROM openjdk:8-jdk-alpine

COPY MMSServer/target/*.jar /app/mms/MMSServer.jar
COPY MMSServer/target/lib/ /app/mms/lib/

COPY start_mms.sh /app/mms/start_mms.sh
COPY start_mns_dummy.sh /app/mms/start_mns_dummy.sh

CMD /app/mms/start_mms.sh
CMD /app/mms/start_mns_dummy.sh

EXPOSE 8088
