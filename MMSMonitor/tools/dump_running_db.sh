#!/bin/sh
#Warning! this tools should be modified. Currently, this does not work.
help()
{
    echo "Usage: $0 [host name or ip address] [target directory to dump the database]"
}


if [ $# -ne 2 ]
then
    help
    exit 0
fi
hostName=$1
targetdir=$2

#dumpCurrentDB
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'http://$hostName', 'http://www.mywebsite.com') WHERE option_name = 'home' OR option_name = 'siteurl';";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'https://$hostName', 'https://www.mywebsite.com') WHERE option_name = 'home' OR option_name = 'siteurl';";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'http://$hostName', 'http://www.mywebsite.com');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'https://$hostName', 'https://www.mywebsite.com');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'http://$hostName', 'http://www.mywebsite.com');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'https://$hostName', 'https://www.mywebsite.com');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'http://$hostName', 'http://www.mywebsite.com');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'https://$hostName', 'https://www.mywebsite.com');";
mysqldump --extended-insert=FALSE -h$hostName -uroot -proot mydb > $targetdir
sleep 1

#restoreDB
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'http://www.mywebsite.com', 'http://$hostName') WHERE option_name = 'home' OR option_name = 'siteurl';";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_options SET option_value = replace(option_value, 'https://www.mywebsite.com', 'https://$hostName') WHERE option_name = 'home' OR option_name = 'siteurl';";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'http://www.mywebsite.com', 'http://$hostName');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET guid = replace(guid, 'https://www.mywebsite.com', 'https://$hostName');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'http://www.mywebsite.com', 'http://$hostName');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_posts SET post_content = replace(post_content, 'https://www.mywebsite.com', 'https://$hostName');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'http://www.mywebsite.com', 'http://$hostName');";
mysql -h $hostName -u root -proot mydb -e "UPDATE wp_postmeta SET meta_value = replace(meta_value,'https://www.mywebsite.com', 'https://$hostName');";

