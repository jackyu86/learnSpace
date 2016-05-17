#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
#.$$作为添加的后缀会被扩展成当前运行脚本的进程ID
temp_file="/tmp/varxx.$$"
#create a 100k file by zero 
dd if=/dev/zero bs=100k count=1 of=data.file
#spilt file
split -b 10k data.file
#split -b 10k data.file -d -a 4 data.file
#按行切割成多个文件
split -l 10 data.file
#find . -type f -maxdepth 1 -name "*.sh~" -print -delete 删除恢复文件
#csplit 按照制定的条件和字符串匹配选项对log文件进行分割
echo -e "SERVER-1\n[connection] 192.168.0.1 success \n[connection] 192.168.0.2 failed\n[connection] 192.168.0.3 pending\n[connection] 192.168.0.4 success">>server.log
echo -e "SERVER-2\n[connection] 192.168.0.1 failed \n[connection] 192.168.0.2 failed\n[connection] 192.168.0.3 success\n[connection] 192.168.0.4 failed">>server.log
echo -e "SERVER-3\n[connection] 192.168.0.1 pending \n[connection] 192.168.0.2 pending\n[connection] 192.168.0.3 pending\n[connection] 192.168.0.4 failed">>server.log
echo -e "SERVER-4\n[connection] 192.168.0.1 success \n[connection] 192.168.0.2 failed\n[connection] 192.168.0.3 pending\n[connection] 192.168.0.4 success">>server.log
#我们按照SERVER切割文件

#1)将文本文件textfile以12行为分界点切割成2份
csplit server.log 12
#2)将文本文件server.log以12行为分界点切割成2份， 并指定输出文件名的位数为3
csplit -n 3 server.log 12
#3)将文本文件server.log以12行为分界点切割成2份， 并指定输出文件名的前缘为FileName位数为
csplit -f FileName server.log 12
#4)将文件server.log以10行为单位分割8次， 并指定输出文件名的格式 mac os 无-b arg
csplit -b "myfile%o%" server.log 10 {8}
#5)将文本文件textfile以每20行为单位分割
csplit server.log 20 {*}
#6)把文件以字符串"SERVER"为分界符，分成两部分
csplit  server.log /"SERVER"/
#7)承上例， 但分割文件时以"SERVER"字符串往下4行才是分割点
csplit server.log /"SERVER"/+4


#根据扩展名切割文件
#借助%和#操作符可以轻松将名称部分从“名称.扩展名”提取出来

#提取文件名
file_jpg="sample.jpg"
name=${file_jpg%.*}
echo file name is:$name

#提取文件名的扩展名，可以借助#来完成
extension=${file_jpg#*.}
echo file extension is :${extension}


#%是非贪婪操作 %%属于贪婪操作  从右匹配截取
#如
file_name="jack.yu.txt"
filename1=${file_name%.*}
echo File Name1 is : ${filename1}
#echo jack.yu
filename2=${file_name%%.*}
echo File Name2 is : ${filename2}
#echo jack

# #与##类似 %与%%  ##是贪婪匹配   从左匹配截取


#实例截取url
URL="www.google.com"

echo ${URL%.*}
#www.google
echo ${URL%%.*}
#www
echo ${URL#*.}
#google.com
echo ${URL##*.}
#com












