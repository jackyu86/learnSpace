#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
:<<EOF
linux中shell变量$#,$@,$0,$1,$2的含义解释: 
变量说明: 
$$ 
Shell本身的PID（ProcessID） 
$! 
Shell最后运行的后台Process的PID 
$? 
最后运行的命令的结束代码（返回值） 
$- 
使用Set命令设定的Flag一览 
$* 
所有参数列表。如"$*"用「"」括起来的情况、以"$1 $2 … $n"的形式输出所有参数。 
$@ 
所有参数列表。如"$@"用「"」括起来的情况、以"$1" "$2" … "$n" 的形式输出所有参数。 
$# 
添加到Shell的参数个数 
$0 
Shell本身的文件名 
$1～$n 
添加到Shell的各参数值。$1是第1参数、$2是第2参数
EOF

ageTest()
{
   echo $1,$2
   echo $@
   echo $*
   echo $$
arrayaaa=$@
for aa in $arrayaaa
do
	echo "------$aa"
done
}
ageTest 1 2 3 5 6 7
#ageTest [1="ad" 2="vvc"]
export -f ageTest

#递归函数
function F(){
	echo $1
	F hello
	sleep 1
}
#fork 炸弹 :(){ :|:$};:
#修改配置文件/etc/security/limits.conf 来限制可生产的最大进程数量



