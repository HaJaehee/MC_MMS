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
	read -p "Do you wish to install this program? Port number 3306 will be mapped to mariadb container's port number and port numbers 80, 443, 25, 465 and 587 will be mapped to mms monitoring container's port numbers. If you want to remap port numbers, please modify docker-compose.yml before this setup. In addition, existing WordPress files and database will be overwritten after this setup. If you want not to overwrite WordPress files and database, just execute docker-compose. [y/n]" yn
	echo ""
	case $yn in
		[Yy]* )
		#echo "Swipe docker containers and images related to MMS Monitoring."
		#sudo docker stop $(sudo docker ps | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb|mcp_mms_monitoring_rabbitmq" | cut -c1-12)
		#sudo docker rm $(sudo docker ps --all | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb|mcp_mms_monitoring_rabbitmq" | cut -c1-12)
		sudo docker stop $(sudo docker ps | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb" | cut -c1-12)
		sudo docker rm $(sudo docker ps --all | grep -E "mcp_mms_monitoring|mcp_mms_monitoring_mariadb" | cut -c1-12)
		#sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring:0.7')
		#sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring_mariadb:0.7')
		#sudo docker rmi $(sudo docker images -q  --filter=reference='lovesm135/mcp_mms_monitoring_rabbitmq:0.7')
		#sudo docker-compose stop && sudo docker-compose rm -v
		#sudo docker volume prune
		#sudo docker volume rm $(sudo docker volume ls -q)


		echo "Web server pre-setting."
		sudo tar -xf mcp_mms_monitoring_html.tar
		sudo sed -i 's/mms\.smartnav\.org/'$newdomain'/g' ./var/www/mcp_mms_monitoring/html/wp-config.php 

		echo "Docker pull."
		export MY_WEB=$1
		sudo echo $MY_WEB
		sudo docker pull lovesm135/mcp_mms_monitoring_mariadb:0.8.2
		sudo docker pull lovesm135/mcp_mms_monitoring:0.8.2
		#sudo docker pull lovesm135/mcp_mms_monitoring_rabbitmq:0.7

		sleep 2
		echo "Make directories."
		sudo mkdir --parents /var/lib/mcp_mms_monitoring_mariadb
		sudo mkdir --parents /var/www/mcp_mms_monitoring/html
		sudo mkdir --parents /var/mail/mcp_mms_monitoring
		sudo cp -r ./var/www/mcp_mms_monitoring/html/* /var/www/mcp_mms_monitoring/html/
		
		echo "Set up docker-compose."
		sudo docker-compose -f ./docker-compose.yml up -d


		echo "Database post-setting."
		sleep 10
		mysql -h 172.17.0.1 --port 3306 -u root -proot mydb < mcp_mms_monitoring_database.sql;

		sleep 10
		sudo echo $MY_WEB
		echo "Replace database contents."
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '192.168.0.104' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'mms.smartnav.org' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '192.168.202.193' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '143.248.55.83' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace '143.248.57.144' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sudo docker exec -it mcp_mms_monitoring bash -c "php /etc/wp-cli/wp-cli.phar search-replace 'mms-kaist.com' '$MY_WEB' --skip-columns=guid --allow-root --path=/var/www/html/"
		sleep 3
		
		echo "Install rabbitmq-server"
		sudo apt install -y rabbitmq-server
		sleep 3

		systemctl start rabbitmq-server
		systemctl status rabbitmq-server

		#echo "Start MMS."
		#cd ../MMSServer/Linux
		#sudo nohup sudo sh ./start_mms.sh >/dev/null 2>&1 &
		#sudo nohup sudo sh ./start_mns.sh >/dev/null 2>&1 &
		#sleep 5

		#cd ../target
		#sudo ln -sf $(pwd)/logs /var/mms/logs

		echo "Remove temporary files."
		cd ../../scripts
		sudo rm -r ./html
		
		echo "Please reconfigure the WP Mail SMTP Plugin."
		echo "We recommend to use Google GMail SMTP service."
		
		 		
 
		exit
		;;
		No ) exit
		;;
	* ) echo "Please answer yes or no.";;
	esac
done


