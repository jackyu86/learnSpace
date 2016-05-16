#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
#.$$作为添加的后缀会被扩展成当前运行脚本的进程ID
temp_file="/tmp/varxx.$$"
#create a 100k file by zero 
dd if=/dev/zero bs=100k count=1 of=data.file
#spilt file
split -b 10k data.file