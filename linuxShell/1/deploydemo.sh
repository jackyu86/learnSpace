#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
DATE=`date +%F`
server1=yuhy@222.73.182.27
server1path=/home/yuhy/adapterFile/shanghai

#打包工程mvn pom路径
path=$1
if  [ ! -n "$path" ] ;then
    	echo "you have not input a programme path!"
else
	cd $path
	pwd
	config=$2

	if  [ ! -n "$config" ] ;then
		mvn clean install -Dmaven.test.skip=true
	else
		mvn clean -P$config install -Dmaven.test.skip=true
	fi
	#cd ../cloudshops-soa-service-adapter/target
	cd ../1688-sdk/target
	#开始拷贝
	#cp cloudshops-soa-service-adapter.war ~/桌面 /home/yuhy/adapterFile/shanghai
	ssh -p 65535 $server1 "mkdir -p $server1path/$DATE"
	scp -P65535  1688-sdk.jar $server1:$server1path/$DATE
	sleep 1
	
		
	
fi

