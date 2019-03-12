#!/bin/bash

sudo docker run --rm --volumes-from mcp_mms_monitoring -v $(pwd):/backup ubuntu tar cvf /backup/backup.tar /var/www/html/
