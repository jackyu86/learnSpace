#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
:<<EOF
if条件
if condition;
then
command;
elif
else
fi
简洁写法
[ condition ] && action 如果condition为真则执行action
[ condition ] || action 如果condition为假则执行action
 EOF