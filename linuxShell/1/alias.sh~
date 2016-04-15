#!/bin/bash
#author jack
export LANG="en_US.UTF-8"

export alias install='sudo apt-get install'
:<<EOF
inux中shell变量$#,$@,$0,$1,$2的含义解释: 
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
添加到Shell的各参数值。$1是第1参数、$2是第2参数…。 
EOF

#别名转义 \aliasName
#命令行模式直接压入.bashrc 因为:alist终端结束失效
#echo 'alias install="sudo apt-get install"' >>~/.bashrc

#删除备份
#echo 'alias rm="cp $@ ~/backup ; rm $@"' >>~/.bashrc





