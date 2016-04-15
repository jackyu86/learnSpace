#!/bin/bash
#author jack
export LANG="en_US.UTF-8"

# let (()) [] expr bc
# int num 
nu1=12
let nu1++
nu2=13
let nu2++
let nu3=$nu1+$nu2
let nu3--
#other way
nu4=$[nu1+nu2]
nu5=$((nu1+nu2))
nu6=$(expr 2+1)
nu7=`expr 2+1`
echo "nu3 equals let nu1 + nu2 : $nu3";
echo "nu4 equals let nu1 + nu2 : $nu4";
echo "nu5 equals let nu1 + nu2 : $nu5";
echo "nu6 equals let nu1 + nu2 : $nu6";
echo "nu7 equals let nu1 + nu2 : $nu7";
#float num
# bc数学运算工具
resultf=`echo 12*1.023455 | bc`
echo "resultf:$resultf"

echo 4*1.0015 | bc

	#设置精度
echo "scale=2;123/2.1235" | bc

	#进制转换
no1=100
echo "obase=2;$no1" | bc
	#计算平方根
echo "sqrt(100)" | bc
echo "10^10" | bc


