#!/bin/bash

sudo mysqldump --single-transaction --databases mydb -h 172.17.0.1 --port 3306 -u root -proot > mcp_mms_monitoring_database.sql
