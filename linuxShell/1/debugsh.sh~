#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
:<<EOF
命令行模式 bash -x scriptFile
 _DEBUG=on bash debugsh.sh
EOF
#定义调试范围
for a in {1..6}
do
set -x
echo $a
set +x
done
echo "Scrpit finshed.."
echo "----------------------------------------------------------------------------"
#定义调试函数
function DEBUG()
{
[ "$_DEBUG" == "on" ] && $@ || :
}

for b in {1..10}
do
DEBUG echo $b
done

