#!/bin/bash
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

while true; do
    read -p "Do you wish to install this program? Port number 3306 will be mapped to mariadb container's and port numbers 5672, 15672, 25672 will be mapped to rabbitmq container's! [y/n]" yn
    echo ""
    case $yn in
        [Yy]* )
		echo "Swipe docker containers and images related to MMS Monitoring."
		sudo docker stop $(sudo docker ps | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb|mcp_mms_monitoring_rabbitmq" | cut -c1-12)
		sudo docker rm $(sudo docker ps --all | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb|mcp_mms_monitoring_rabbitmq" | cut -c1-12)
		sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring:0.7')
		sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring_mariadb:0.7')
		sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring_rabbitmq:0.7')
		sudo docker-compose stop && sudo docker-compose rm -v
		sudo docker volume prune
		#sudo docker volume rm $(sudo docker volume ls -q)


		echo "Web server pre-setting."
		#sudo ./clear.sh
		#sudo tar -hxvf ../MMSMonitor.tar.gz -C ../
		#sudo cp -r ../MMSMonitor/var ./ 
		#sudo cp -r ../MMSMonitor/apache2 ./
		#sudo cp -r ../MMSMonitor/ssl ./
		#sudo cp -r ../MMSMonitor/database.sql ./
		#sudo cp -r ../MMSMonitor/wp-cli.phar ./
		sudo tar -xf mcp_mms_monitoring_html_backup.tar
		sudo sed -i 's/mms\.smartnav\.org/'$newdomain'/g' ./var/www/html/wp-config.php 

		echo "Docker pull."
		export MY_WEB=$1
		sudo echo $MY_WEB
		sudo docker pull lovesm135/mcp_mms_monitoring_mariadb:0.7
		sudo docker pull lovesm135/mcp_mms_monitoring_rabbitmq:0.7
		sudo docker pull lovesm135/mcp_mms_monitoring:0.7

		sleep 2
		echo "Make directories."
		sudo mkdir --parents /var/lib/mcp_mms_monitoring_mariadb
		sudo mkdir --parents /var/www/mcp_mms_monitoring/html
        sudo cp -r ./var/www/html/* /var/www/mcp_mms_monitoring/html/
		
		echo "Set up docker-compose."
		sudo docker-compose -f ./docker-compose.yml up -d

		#sleep 10
		#docker exec -d mms-monitor -w /etc/wp-cli/ php wp-cli.phar search-replace 'http://143.248.57.144' 'http://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/

		echo "Database post-setting."
		sleep 10
		mysql -h 172.17.0.1 --port 3306 -u root -proot mydb < mcp_mms_monitoring_database.sql;

		sleep 10
		sudo echo $MY_WEB
		echo "Replace database contents."
		#sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'http://mms.smartnav.org' 'http://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		#sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'https://mms.smartnav.org' 'https://$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'mms.smartnav.org' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '192.168.202.193' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '143.248.55.83' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '143.248.57.144' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sleep 3
		
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

		#sleep 5
		#delete completed files
		#web and database pre-setting
		#sudo rm -rf var
		#sudo rm -rf apache2
		#sudo rm -rf ssl 
		#sudo rm -rf database.sql
		#sudo rm -rf wp-cli.phar

		echo "Start MMS."
		cd ../MMSServer/Linux
		sudo nohup sudo sh ./start_mms.sh 2>&1 &
		sudo nohup sudo sh ./start_mns.sh 2>&1 &
		sleep 5

		#cd ../target
		#sudo ln -sf $(pwd)/logs /var/mms/logs

		echo "Remove temporary files."
		cd ../../scripts
		#sudo rm -r MMSMonitor
		sudo rm -r ./var

		exit
		;;
        No ) exit
		;;
	* ) echo "Please answer yes or no.";;
    esac
done


