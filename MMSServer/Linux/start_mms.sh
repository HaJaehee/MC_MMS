#!/bin/bash
cd ../target
java -cp ../target/MC_MMS-0.9.1-SNAPSHOT.jar kr.ac.kaist.mms_server.MMSServer -mq rabbitmq-db
