## Caution!
## This branch is currently under development.
## Can be corrected without notice.

# MC_MMS
Beta Version 0.9.1 <br/>
2019.05.29 <br/>

# Web Site
https://www.mms-kaist.com <br/>
https://mms.smartnav.org

## Features
MMS Beta Version:<br/>
Supports Docker's setup environment.<br/>
Supports javadoc API documents.<br/>
Supports fully relaying HTTP(S) messages based on ID.<br/>
Supports fully HTTPS communication.<br/>
Supports long polling.<br/>
Supports switching polling method between long polling or normal polling.<br/>
Supports maintaining long polling client session.<br/>
Supports saving MMS logs automatically.<br/>
Supports displaying MMS logs on web browser.<br/>
Supports MMS web management.<br/>
Supports MMS REST API.<br/>
Has Maritime Name System Dummy.<br/>
Has Service Consumer Examples.<br/>
Has Service Provider Examples.<br/>

## Developing
Jaehee Ha<br/>
email: jaehee.ha@kaist.ac.kr<br/>
Jin Jeong<br/>
email: jungst0001@kaist.ac.kr<br/>
Youngjin Kim<br/>
email: jcdad3000@kaist.ac.kr<br/>
Yoonho Choi<br/>
email: choiking10@kaist.ac.kr<br/>
Jaehyun Park<br/>
email: jae519@kaist.ac.kr<br/>

## Usage
First of all, MMS Server including MMS Monitoring Module depends on Ubuntu 16.04, OpenJDK 1.8, <br/>
RabbitMQ 3.7.14, erlang/OTP 22.0, Maven 3.3.9 and Docker 18.09.2.<br/>
Also, MMS Server is compilable and runnable on Windows 10.<br/>
When using Debian/Ubuntu Linux, follow instructions below and install dependencies before executing <br/>
script [MMSServer/scripts/deploy-mms.sh]:<br/>
<code>sudo apt update</code><br/>
<code>sudo apt install default-jdk maven rabbitmq-server docker docker-compose</code> <br/>
Before building MMS Server, specify configuration files in [MMSServer/MMS-configuration] directory.<br/>

<b>QUICK DEPLOY:</b> run the script [deploy-mms.sh] at the directory [MMSServer/scripts/]. <br/>
<code>cd MMSServer/scripts </code><br/>
<code>sudo sh deploy-mms.sh </code><br/>
It will automatically build and run MMS Server, MMS Monitoring Module and related services.<br/>

<b>ONLY BUILD AND START MMS:</b> run the scripts at the directory [MMSServer/Linux/].<br/>
<code>cd MMSServer/Linux </code><br/>
<code>sudo sh build_mms.sh </code><br/>
<code>sudo sh start_mms.sh </code><br/>
<code>sudo sh start_mns.sh </code><br/>

### After accomplishment
In order to use email service, please reconfigure the WP Mail SMTP Plugin of WordPress admin panel.<br/>
We recommend to use Google SSMTP service.<br/>
Default admin account of MMS Monitoring Module (WordPress wp-login.php) is Administrator/wins2-champion.<br/>

<b>TLS SUPPORT:</b> check web sites below:</br>
[RabbitMQ TLS Support](https://www.rabbitmq.com/ssl.html)  <br/>
[Docker Tutorial](https://www.tutorialspoint.com/docker/)<br/>
[Apache Install SSL Certificate](https://www.digicert.com/csr-ssl-installation/apache-openssl.htm)<br/>
[HTTPS for WordPress](https://make.wordpress.org/support/user-manual/web-publishing/https-for-wordpress/) <br/>
<b>MMS Server TLS SUPPORT:</b> check instructions below:<br/>
[Import Individual Certificates into your Keystore](https://www.attachmate.com/documentation/gateway-1-1/gateway-admin-guide/data/fxg_keytool_importcert.htm)<br/>
In order to get Base64 endcoded keystore for SSL enabled MMS Server from the jks, use [MMSKeystoreCoder] <br/>
and get Base64 encoded string that is printed out to the console. <br/>
Copy the string and paste it to a value of "KEYSTORE" in [MMSServer/target/MMS-configuration/MMS.conf]. <br/>
After then, restart the MMS Server.<br/>

<br/>

### See also
See [OpenJDK](https://openjdk.java.net/) https://openjdk.java.net <br/>
See [Maven](https://maven.apache.org/) https://maven.apache.org <br/>
See [RabbitMQ](https://www.rabbitmq.com/#getstarted) https://www.rabbitmq.com/#getstarted <br/>
See [Erlang/OTP](https://www.erlang.org/downloads) https://www.erlang.org/downloads <br/>
See [Docker](https://www.docker.com/) https://www.docker.com <br/>
See [WordPress](https://www.wordpress.com) https://www.wordpress.com <br/>
See [MariaDB](https://mariadb.com/) https://mariadb.com <br/>
<br/>

## Tools and licenses
<b>MMS is created with [Eclipse](https://www.eclipse.org/org/documents/epl-v10.php) https://www.eclipse.org/org/documents/epl-v10.php</b><br/>
<br/>

<b>MMS uses [Netty Project](http://netty.io/) http://netty.io/</b><br/>
Copyright 2014 The Netty Project<br/>
The Netty Project licenses this file to you under the Apache License,<br/>
version 2.0 (the "License"); you may not use this file except in compliance<br/>
with the License. You may obtain a copy of the License at:<br/>
http://www.apache.org/licenses/LICENSE-2.0<br/>
<br/>
   
<b>MMS uses [RabbitMQ](https://www.rabbitmq.com/mpl.html) https://www.rabbitmq.com/mpl.html</b><br/>
 Mozilla Public License.<br/>
''The contents of this file are subject to the Mozilla Public License<br/>
Version 1.1 (the "License"); you may not use this file except in<br/>
compliance with the License. You may obtain a copy of the License at<br/>
http://www.mozilla.org/MPL/<br/>
Software distributed under the License is distributed on an "AS IS"<br/>
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the<br/>
License for the specific language governing rights and limitations<br/>
under the License.<br/>
The Initial Developer of the Original Code is GoPivotal, Ltd.<br/>
Copyright (c) 2007-2013 GoPivotal, Inc.  All Rights Reserved.''<br/>
[NOTE: The text of this Exhibit A may differ slightly from the text of<br/>
the notices in the Source Code files of the Original Code. You should<br/>
use the text of this Exhibit A rather than the text found in the<br/>
Original Code Source Code for Your Modifications.]<br/>
<br/>

<b>MMS uses [LogBack](https://logback.qos.ch/license.html) https://logback.qos.ch/license.html</b><br/>
Logback License <br/>
As of release 0.9.18, logback source code and binaries are dual-licensed <br/>
under the EPL v1.0 and the LGPL 2.1, or more formally: <br/>
Logback: the reliable, generic, fast and flexible logging framework. <br/>
Copyright (C) 1999-2017, QOS.ch. All rights reserved.  <br/>
This program and the accompanying materials are dual-licensed under <br/>
either the terms of the Eclipse Public License v1.0 as published by <br/>
the Eclipse Foundation <br/>
or (per the licensee's choosing) <br/>
under the terms of the GNU Lesser General Public License version 2.1 <br/>
as published by the Free Software Foundation. <br/>
<br/>

<b>MMS uses [WordPress](https://wordpress.org/about/license/) https://wordpress.org/about/license/</b><br/>
GNU Public License<br/>
The license under which the WordPress software is released is the GPLv2<br/>
(or later) from the Free Software Foundation. A copy of the license is<br/>
included with every copy of WordPress, but you can also read the text<br/>
of the license here.<br/>
Part of this license outlines requirements for derivative works, such <br/>
as plugins or themes. Derivatives of WordPress code inherit the GPL <br/>
license. Drupal, which has the same GPL license as WordPress, has an <br/>
excellent page on licensing as it applies to themes and modules (their <br/>
word for plugins).<br/>
There is some legal grey area regarding what is considered a derivative <br/>
work, but we feel strongly that plugins and themes are derivative work<br/>
and thus inherit the GPL license. If you disagree, you might want to<br/>
consider a non-GPL platform such as Serendipity (BSD license) or Habari <br/>
(Apache license) instead.<br/>
<br/>

<b>MMS uses [MariaDB](https://mariadb.com/kb/en/library/mariadb-license/) https://mariadb.com/kb/en/library/mariadb-license/</b><br/>
MariaDB server license<br/>
The MariaDB server is available under the terms of the GNU General Public <br/>
License, version 2.<br/>
The GNU project mantains an official page with information about the GNU <br/>
GPL 2 license, including a FAQ and various translations. <br/>
<br/>

## Databases
[RabbitMQ](https://www.rabbitmq.com/) https://www.rabbitmq.com<br/>
[MariaDB](https://mariadb.com/) https://mariadb.com
