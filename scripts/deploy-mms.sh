#!/bin/sh
#This script deploys current version of MMS to local server.

help()
{
    echo "Usage: $0 [domain name to run]"
}

if [ $# -ne 1 ]
then
    help
    exit 0
fi
newdomain=$1

sudo docker stop $(sudo docker ps -a -q)
sudo docker rm $(sudo docker ps -a -q)
sudo docker rmi $(sudo docker images -q)
sudo docker-compose stop && sudo docker-compose rm -v
sudo docker volume rm $(sudo docker volume ls -q)


#web and database pre-setting
sudo ./clear.sh
sudo cp -r ../MMSMonitor/var ./ 
sudo cp -r ../MMSMonitor/apache2 ./
sudo cp -r ../MMSMonitor/ssl ./
sudo cp -r ../MMSMonitor/database.sql ./
sudo cp -r ../MMSMonitor/wp-cli.phar ./

sleep 1
sed -i 's/www\.mywebsite\.com/'$newdomain'/g' ./var/www/html/wp-config.php

export MY_WEB=$1
sudo echo $MY_WEB
sudo docker-compose -f ./docker-compose.yml up -d

sleep 10
#docker exec -d mms-monitor -w /etc/wp-cli/ php wp-cli.phar search-replace 'http://143.248.57.144' 'http://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/

#database post-setting
mysql -h $newdomain -u root -proot mydb < database.sql;
sleep 10
sudo echo $MY_WEB
sudo docker exec -it mms-monitor bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'http://www.mywebsite.com' 'http://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
sudo docker exec -it mms-monitor bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'https://www.mywebsite.com' 'https://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"

#sudo docker exec -it mms-monitor bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'http://143.248.57.144' 'http://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
#sudo docker exec -it mms-monitor bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'https://143.248.57.144' 'https://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"


#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'http://www.mywebsite.com', 'http://$newdomain') WHERE option_name = 'home' OR option_name = 'siteurl';";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'https://www.mywebsite.com', 'https://$newdomain') WHERE option_name = 'home' OR option_name = 'siteurl';";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'http://www.mywebsite.com', 'http://$newdomain');";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'https://www.mywebsite.com', 'https://$newdomain');";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'http://www.mywebsite.com', 'http://$newdomain');";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'https://www.mywebsite.com', 'https://$newdomain');";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'http://www.mywebsite.com', 'http://$newdomain');";
#mysql -h $newdomain -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'https://www.mywebsite.com', 'https://$newdomain');";

sleep 1
#delete completed files
#web and database pre-setting
sudo rm -rf var
sudo rm -rf apache2
sudo rm -rf ssl 
sudo rm -rf database.sql
sudo rm -rf wp-cli.phar


