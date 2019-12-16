# IMPORTANT

In order to do 'maven install' on Eclipse on Windows, "External Jars/pkilib-1.4.jar" has to be copied to "C:\Users\\<user_name>\\.m2\repository\net\etri\net.etri.pkilib\1.4\net.etri.pkilib-1.4.jar" and then 'maven update' has to be done. And then do 'maven install'.  

# Usage
```
usage: java -cp MC_MMS.jar kr.ac.kaist.mms_server.MMSServer [-h help] [-https https] [-mc max_content_size] [-mls   
            max_brief_log_list_size] [-mns mns_host] [-mnsp mns_port] [-mrn mms_mrn] [-mqhost rabbit_mq_host] [-mqport   
            rabbit_mq_port] [-mqmnghost rabbit_mq_managing_host] [-mqmngport rabbit_mq_managing_port] [-mqmngproto   
            rabbit_mq_managing_protocol] [-mquser rabbit_mq_user] [-mqpasswd rabbit_mq_passwd] [-mqconnpool   
            rabbit_mq_conn_pool] [-p http_port] [-sp https_port] [-t waiting_message_timeout] [-wl web_log_providing]   
            [-wm web_managing]   
-h,--help                                         Print a help message and exit.   
-https,--https                                    Set the HTTPS enabled.   
-mc,--max_content_size <arg>                      Set the maximum content size of this MMS. The unit of size is Kilo   
                                                   Bytes.   
-mls,--max_brief_log_list_size <arg>              Set the maximum list size of the brief log in the MMS status.   
-mns,--mns_host <arg>                             Set the host of the Maritime Name System server.   
-mnsp,--mns_port <arg>                            Set the port number of the Maritime Name System server.   
-mqconnpool,--rabbit_mq_conn_pool <arg>           Set the size of Rabbit MQ connection pool.   
-mqhost,--rabbit_mq_host <arg>                    Set the host of the Rabbit MQ server.   
-mqmnghost,--rabbit_mq_managing_host <arg>        Set the host of the Rabbit MQ management server.   
-mqmngport,--rabbit_mq_managing_port <arg>        Set the port number of the Rabbit MQ management server.   
-mqmngproto,--rabbit_mq_managing_protocol <arg>   Set the protocol of the Rabbit MQ management server.   
-mqpasswd,--rabbit_mq_passwd <arg>                Set the password of the Rabbit MQ server.   
-mqport,--rabbit_mq_port <arg>                    Set the port number of the Rabbit MQ server.   
-mquser,--rabbit_mq_user <arg>                    Set the username of the Rabbit MQ server.   
-mrn,--mms_mrn <arg>                              Set the Maritime Resource Name of this MMS.   
-p,--http_port <arg>                              Set the HTTP port number of this MMS.   
-sp,--https_port <arg>                            Set the HTTPS port number of this MMS.   
-t,--waiting_message_timeout <arg>                Set the waiting message timeout of this MMS, when using sequence   
                                                   number in a message.   
-wl,--web_log_providing <arg>                     Enable this MMS provide web logging if the argument is [true],   
                                                   otherwise disable if [false]. Default is [true].   
-wm,--web_managing <arg>                          Enable this MMS provide web management if the argument is [true],   
                                                   otherwise disable if [false]. Default is [true].   
```
