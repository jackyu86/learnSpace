#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
#标准输入(0)，标准输出(1)，标准错误(2)
ls -al other.sh 1>>1.out 2>>2.out
#变量
echo "shell name: $SHELL"
echo "shell file name: $0"
val="value"
echo "$val or ${val}"

#环境变量及作用范围
:<<EOF
env显示用户的环境变量；
set 显示当前shell的定义的私有变量，包括用户的环境变量，按变量名称排序；
export 显示当前导出成用户变量的shell变量，并显示变量的属性(是否只读)，按变量名称排序；
declare 同set 一样，显示当前shell的定义的变量，包括用户的环境变量；
EOF

#显示变量长度
val2=123456789012345678
echo " variable length : ${#val2}"

#根据UID识别当用户 root uid 0
if [ $UID -ne 0 ];
then
echo "Non root user . Please run as root ."
else
echo "root user"
echo "shell running ..."
fi

#判断最后执行的命令的执行状态是否成功
CMD="ps -ef"
$CMD
if [ $? -eq 0 ];
then
echo "$CMD executed successfully"
else
echo "$CMD terminated unsuccessfully"
fi


