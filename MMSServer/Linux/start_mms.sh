#!/bin/bash
if [ -f "/var/mms/logs/mms-server.html" ]; then	
	now=$(date +"%Y-%m-%d.%T")
	mv /var/mms/logs/mms-server.html /var/mms/logs/mms-server.$now.html
fi
cd ../target
java -cp ../target/MC_MMS-0.9.3-SNAPSHOT.jar kr.ac.kaist.mms_server.MMSServer -mqmngproto https
