#!/bin/sh
#This script deploys current version of MMS to local server.

sudo docker stop $(sudo docker ps -a -q)
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi $(sudo docker images -q)
sudo docker-compose -f ./docker-compose.yml up -d


