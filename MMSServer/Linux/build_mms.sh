#!/bin/bash

cd ..
mvn clean install 
cp External\ Jars/pkilib-1.4.jar $HOME/.m2/repository/net/etri/net.etri.pkilib/1.4/net.etri.pkilib-1.4.jar
mvn install
cp -r MMS-configuration ./target/

