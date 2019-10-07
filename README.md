# MC_MMS
[![Build Status](https://travis-ci.org/MaritimeConnectivityPlatform/MC_MMS.svg?branch=beta-0.9.5-closed)](https://travis-ci.org/MaritimeConnectivityPlatform/MC_MMS)  
Beta Version 0.9.5   
2019.09.26   

# Web Site
https://www.mms-kaist.com   
https://mms.smartnav.org

## Features
### MMS Beta Version      
* Relaying HTTP(S) messages based on ID.  
* Long polling method.  
* Switching polling method between long polling or normal polling.  
* Maintaining long polling client session.  
* Sorting messages using message priority in a message queue.  
* HTTPS communication.  
* Saving MMS logs automatically.  
* Displaying MMS logs on web browser.  
* MMS web management.  
* MMS REST API.  
* Maritime Name System Dummy (will be depricated soon).  
* Service Consumer examples.  
* Service Provider examples.  
* Javadoc documents of MMSClient examples.  
* Docker setup scripts.

## Developing
* Jaehee Ha  
  - email: jaehee.ha@kaist.ac.kr  
* Jin Jeong  
  - email: jungst0001@kaist.ac.kr  
* Yoonho Choi  
  - email: choiking10@kaist.ac.kr  

## Usage
First of all, MMS Server including MMS Monitoring Module depends on Ubuntu 16.04, OpenJDK 1.8,   
RabbitMQ 3.7.14, erlang/OTP 22.0, Maven 3.3.9 and Docker 18.09.2.  
Also, MMS Server is compilable and runnable on Windows 10.  
When using Debian/Ubuntu Linux, follow instructions below and install dependencies before executing   
script [MC_MMS/scripts/deploy-mms.sh]:  
```
sudo apt update
sudo apt install default-jdk maven rabbitmq-server docker docker-compose
```

### Quick deploy
Run the script [deploy-mms.sh] at the directory [MC_MMS/scripts/].   
Please read it carefully. Port number **3306** will be mapped to mariadb container's port number and port numbers   
**80 and 443** will be mapped to MMS Monitoring Module container's port numbers. If you want to remap port numbers, please   
modify [docker-compose.yml] before executing this setup script. In addition, existing WordPress files and database   
will be overwritten after this setup. If you want not to overwrite WordPress files and database, just execute   
docker-compose with [docker-compose.yml].
Before building MMS Server, specify configuration files in [MC_MMS/MMSServer/MMS-configuration/] directory.  
Do not use loopback, localhost, and 127.0.0.1 as a domain name.  
```
cd MC_MMS/scripts 
sudo sh deploy-mms.sh [domain name] [docker bridge IP address connecting database]
```
It will automatically build and run MMS Server, MMS Monitoring Module and related services.  
After running MMS Monitoring Module docker container, re-configure detailed configurations of   
apache2 in [/etc/apache2/] in the docker container.  

### Only bulid and start MMS Server
Run the scripts at the directory [MC_MMS/MMSServer/Linux/].  
Before building MMS Server, specify configuration files in [MC_MMS/MMSServer/MMS-configuration/] directory.  
```MMS.conf, logback-Linux.xml and logback-Windows.xml```  
After building MMS Server, the [MMS-configuration] directory is copied into [MC_MMS/MMSServer/target/] directory and  
MMS Server executable jar file is created in [MC_MMS/MMSServer/target/] directory.  
**Default configuration** is loaded if MMS Server cannot find [**MMS.conf**] in [MC_MMS/MMSServer/target/  
MMS-configuration/] directory and a user does not input options to MMS Server when the user runs MMS Server using command.  
[**MMS.conf**] is loaded if MMS Server find [**MMS.conf**] in [MC_MMS/MMSServer/target/MMS-configuration/] directory.  
If MMS Server executable jar file is moved from [MC_MMS/MMSServer/target/] directory to a different location, e.g.,  
$HOME directory, MMS Server will find [$HOME/MMS-configuraiton/**MMS.conf** (and logback-Linux.xml or  
logback-Windows.xml)].  
**CLI configuration options** are loaded if a user inputs options to MMS Server when the user runs MMS Server using command.  
MMS Server configuration options works like this:
1. ```Default configuration```; these values can be overridden by
2. ```[<MMS Server executable jar file location>/MMS-configuration/MMS.conf]```; these values can be overridden by
3. ```CLI configuration options```  

The user can input options to MMS Server by adding options in [start_mms.sh]. If you need learn **CLI configuration  
options**, please check [MC_MMS/MMSServer/README.md].   
```
cd MC_MMS/MMSServer/Linux 
sudo sh build_mms.sh 
sudo sh start_mms.sh 
sudo sh start_mns.sh 
```

### After accomplishment
In order to use email service, please reconfigure the WP Mail SMTP Plugin of WordPress admin panel.  
We recommend to use Google SSMTP service.  
Default admin account of MMS Monitoring Module (WordPress wp-login.php) is Administrator/Administrator.  

#### TLS support
Check web sites below:</br>
[RabbitMQ TLS Support](https://www.rabbitmq.com/ssl.html)    
[Docker Tutorial](https://www.tutorialspoint.com/docker/)  
[Apache Install SSL Certificate](https://www.digicert.com/csr-ssl-installation/apache-openssl.htm)  
[HTTPS for WordPress](https://make.wordpress.org/support/user-manual/web-publishing/https-for-wordpress/)   

#### MMS Server TLS support
Check instructions below:  
[Import Individual Certificates into your Keystore](https://www.attachmate.com/documentation/gateway-1-1/gateway-admin-guide/data/fxg_keytool_importcert.htm)  
In order to get Base64 endcoded keystore for SSL enabled MMS Server from the jks, use [MC_MMS/MMSKeystoreCoder]   
and get Base64 encoded string that is printed out to the console.   
Copy the string and paste it to a value of "KEYSTORE" in [MC_MMS/MMSServer/target/MMS-configuration/MMS.conf].   
After then, restart the MMS Server.  

  

### See also
* [OpenJDK](https://openjdk.java.net/) https://openjdk.java.net   
* [Maven](https://maven.apache.org/) https://maven.apache.org   
* [RabbitMQ](https://www.rabbitmq.com/#getstarted) https://www.rabbitmq.com/#getstarted   
* [Erlang/OTP](https://www.erlang.org/downloads) https://www.erlang.org/downloads   
* [Docker](https://www.docker.com/) https://www.docker.com   
* [WordPress](https://www.wordpress.com) https://www.wordpress.com   
* [MariaDB](https://mariadb.com/) https://mariadb.com   
  

## Tools and licenses
* **MMS is created with [Eclipse](https://www.eclipse.org/org/documents/epl-v10.php) https://www.eclipse.org/org/documents/epl-v10.php**  
  

* **MMS uses [Netty Project](http://netty.io/) http://netty.io/**  
Copyright 2014 The Netty Project  
The Netty Project licenses this file to you under the Apache License,  
version 2.0 (the "License"); you may not use this file except in compliance  
with the License. You may obtain a copy of the License at:  
http://www.apache.org/licenses/LICENSE-2.0  
  
   
* **MMS uses [RabbitMQ](https://www.rabbitmq.com/mpl.html) https://www.rabbitmq.com/mpl.html**  
 Mozilla Public License.  
''The contents of this file are subject to the Mozilla Public License  
Version 1.1 (the "License"); you may not use this file except in  
compliance with the License. You may obtain a copy of the License at  
http://www.mozilla.org/MPL/  
Software distributed under the License is distributed on an "AS IS"  
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the  
License for the specific language governing rights and limitations  
under the License.  
The Initial Developer of the Original Code is GoPivotal, Ltd.  
Copyright (c) 2007-2013 GoPivotal, Inc.  All Rights Reserved.''  
[NOTE: The text of this Exhibit A may differ slightly from the text of  
the notices in the Source Code files of the Original Code. You should  
use the text of this Exhibit A rather than the text found in the  
Original Code Source Code for Your Modifications.]  
  

* **MMS uses [LogBack](https://logback.qos.ch/license.html) https://logback.qos.ch/license.html**  
Logback License   
As of release 0.9.18, logback source code and binaries are dual-licensed   
under the EPL v1.0 and the LGPL 2.1, or more formally:   
Logback: the reliable, generic, fast and flexible logging framework.   
Copyright (C) 1999-2017, QOS.ch. All rights reserved.    
This program and the accompanying materials are dual-licensed under   
either the terms of the Eclipse Public License v1.0 as published by   
the Eclipse Foundation   
or (per the licensee's choosing)   
under the terms of the GNU Lesser General Public License version 2.1   
as published by the Free Software Foundation.   
  

* **MMS uses [WordPress](https://wordpress.org/about/license/) https://wordpress.org/about/license/**  
GNU Public License  
The license under which the WordPress software is released is the GPLv2  
(or later) from the Free Software Foundation. A copy of the license is  
included with every copy of WordPress, but you can also read the text  
of the license here.  
Part of this license outlines requirements for derivative works, such   
as plugins or themes. Derivatives of WordPress code inherit the GPL   
license. Drupal, which has the same GPL license as WordPress, has an   
excellent page on licensing as it applies to themes and modules (their   
word for plugins).  
There is some legal grey area regarding what is considered a derivative   
work, but we feel strongly that plugins and themes are derivative work  
and thus inherit the GPL license. If you disagree, you might want to  
consider a non-GPL platform such as Serendipity (BSD license) or Habari   
(Apache license) instead.  
  

* **MMS uses [MariaDB](https://mariadb.com/kb/en/library/mariadb-license/) https://mariadb.com/kb/en/library/mariadb-license/**  
MariaDB server license  
The MariaDB server is available under the terms of the GNU General Public   
License, version 2.  
The GNU project mantains an official page with information about the GNU   
GPL 2 license, including a FAQ and various translations.   
  

## Databases
* [RabbitMQ](https://www.rabbitmq.com/) https://www.rabbitmq.com  
* [MariaDB](https://mariadb.com/) https://mariadb.com
