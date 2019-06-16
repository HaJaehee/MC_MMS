# IMPORTANT

In order to do maven install on Eclipse, "External Jars/pkilib-1.4.jar" must be copied to "C:\Users\\<user_name>\\.m2\repository\net\etri\net.etri.pkilib\1.4\net.etri.pkilib-1.4.jar" and then 'maven update' is done, before to do 'maven install'.

# Usage
<pre>usage: java -cp MC_MMS.jar kr.ac.kaist.mms_server.MMSServer [-h help] [-https https] [-mc max_content_size] [-mls <br/>
            max_brief_log_list_size] [-mns mns_host] [-mnsp mns_port] [-mrn mms_mrn] [-mqhost rabbit_mq_host] [-mqport <br/>
            rabbit_mq_port] [-mqmnghost rabbit_mq_managing_host] [-mqmngport rabbit_mq_managing_port] [-mqmngproto <br/>
            rabbit_mq_managing_protocol] [-mquser rabbit_mq_user] [-mqpasswd rabbit_mq_passwd] [-mqconnpool <br/>
            rabbit_mq_conn_pool] [-p http_port] [-sp https_port] [-t waiting_message_timeout] [-wl web_log_providing] <br/>
            [-wm web_managing] <br/>
 -h,--help                                         Print a help message and exit. <br/>
 -https,--https                                    Set the HTTPS enabled. <br/>
 -mc,--max_content_size <arg>                      Set the maximum content size of this MMS. The unit of size is Kilo <br/>
                                                   Bytes. <br/>
 -mls,--max_brief_log_list_size <arg>              Set the maximum list size of the brief log in the MMS status. <br/>
 -mns,--mns_host <arg>                             Set the host of the Maritime Name System server. <br/>
 -mnsp,--mns_port <arg>                            Set the port number of the Maritime Name System server. <br/>
 -mqconnpool,--rabbit_mq_conn_pool <arg>           Set the size of Rabbit MQ connection pool. <br/>
 -mqhost,--rabbit_mq_host <arg>                    Set the host of the Rabbit MQ server. <br/>
 -mqmnghost,--rabbit_mq_managing_host <arg>        Set the host of the Rabbit MQ management server. <br/>
 -mqmngport,--rabbit_mq_managing_port <arg>        Set the port number of the Rabbit MQ management server. <br/>
 -mqmngproto,--rabbit_mq_managing_protocol <arg>   Set the protocol of the Rabbit MQ management server. <br/>
 -mqpasswd,--rabbit_mq_passwd <arg>                Set the password of the Rabbit MQ server. <br/>
 -mqport,--rabbit_mq_port <arg>                    Set the port number of the Rabbit MQ server. <br/>
 -mquser,--rabbit_mq_user <arg>                    Set the username of the Rabbit MQ server. <br/>
 -mrn,--mms_mrn <arg>                              Set the Maritime Resource Name of this MMS. <br/>
 -p,--http_port <arg>                              Set the HTTP port number of this MMS. <br/>
 -sp,--https_port <arg>                            Set the HTTPS port number of this MMS. <br/>
 -t,--waiting_message_timeout <arg>                Set the waiting message timeout of this MMS, when using sequence <br/>
                                                   number in a message. <br/>
 -wl,--web_log_providing <arg>                     Enable this MMS provide web logging if the argument is [true], <br/>
                                                   otherwise disable if [false]. Default is [true]. <br/>
 -wm,--web_managing <arg>                          Enable this MMS provide web management if the argument is [true], <br/>
                                                   otherwise disable if [false]. Default is [true]. <br/>
</pre>
