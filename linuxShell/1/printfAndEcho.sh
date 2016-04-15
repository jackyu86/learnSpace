#!/bin/bash
# auth jack
export LANG="en_US.UTF-8"

echo Hello Work ! #echo Hello;Hello
echo 'Hello Work !'
echo "Hello Work !"
echo "Hello;Hello"

printf "%-5s %-10s %-4s \n" No Name Mark
printf "%-5s %-10s %-4.2f \n" 1 jack 88.88
printf "%-5s %-10s %-4.2f \n" 2 yu 99.00

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
