#/bin/sh
#author jack
export LANG="en_US.UTF-8"
DATE=`date +%F`
#打包工程mvn pom路径
read -p "enter programme path :"path
if  [ ! -n "$path" ] ;then
    	echo "you have not input a programme path!"
else
	cd $path
	read -p "enter programme idc config :"config

	if  [ ! -n "$path" ] ;then
		echo "you have not input a programme idc config!"
	else
		mvn clean install
		#scp CloudBiz.tar.gz 192.168.1.103:/var/ftp/pub/webOld/
		ssh -p 65535 qa@222.73.176.24 "mkdir -p /home/yuhy/adapter/release/$DATE"
		scp -P65535  CloudBiz.tar.gz qa@222.73.176.24:/home/qa/release/$DATE	
	fi
	
fi

