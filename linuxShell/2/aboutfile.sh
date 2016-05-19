#!/bin/bash
#author:jack
#data:20160519
#func:文件相关
export "en_US.UTF-8"

#使用0创建一个1M的文件
dd if=/dev/zero of=createfile.data bs=1M count=1
#单位 c:字节(1B) w:字(2B) b:块(512B) k:千字节(1024b) M:兆字节(1024KB) G:吉字节(1024MB)aouut

#打印文件的交集，差，差集
#comm命令，comm必须使用排序后的文件作为输出且排序方式一样
echo -e "apple\norange\ngold\nsilver\nsteel\niron">A.txt
echo -e "orange\ngold\ncookies\ncarrot">B.txt
#排序文件内容
sort A.txt -o A.txt;sort B.txt -o B.txt

comm A.txt B.txt