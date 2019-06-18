#!/bin/sh
if [ -f "/var/mms/logs/mms-server.html" ]; then	
	now=$(date +"%Y-%m-%d.%T")
	mv /var/mms/logs/mms-server.html /var/mms/logs/mms-server.$now.html
fi
java -cp /app/mms/MMSServer.jar kr.ac.kaist.mms_server.MMSServer 