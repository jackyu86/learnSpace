#!/bin/bash
#author jack
export LANG="en_US.UTF-8"

#数组  普通数组，关联数组

array1=(1 2 3 4 5 6 75 4 34 36 37)
count=0
arraylength=${#array1[@]}
echo $arraylength
for((j=0;j<$arraylength;j++));
do
	let count++
	array1[$j]="test$count"
	echo ${array1[$j]}
done;




